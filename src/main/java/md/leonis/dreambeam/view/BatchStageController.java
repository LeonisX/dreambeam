package md.leonis.dreambeam.view;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import md.leonis.dreambeam.model.DiskImage;
import md.leonis.dreambeam.model.FileRecord;
import md.leonis.dreambeam.statik.Config;
import md.leonis.dreambeam.statik.Storage;
import md.leonis.dreambeam.utils.BinaryUtils;
import md.leonis.dreambeam.utils.FileUtils;
import md.leonis.dreambeam.utils.JavaFxUtils;
import md.leonis.dreambeam.utils.StringUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import java.util.zip.CRC32;

import static md.leonis.dreambeam.statik.Config.str;

public class BatchStageController implements Closeable {

    private static final String CDFS = "CDFS";

    public Button stopButton;
    public Button closeButton;
    public Label pathLabel;
    public Label fileLabel;
    public TextArea textArea;

    private final PathMatcher isoMatcher = FileSystems.getDefault().getPathMatcher("glob:*.{iso,isz,mds,mdx,cue,cdi,nrg,bwt,b5t,b6t,ccd,pdi}");

    private File driveRoot;

    List<Path> files = new ArrayList<>();

    boolean isRunning;

    private String volumeLabel;
    private String serialNumber;
    private String fileSystem;
    private long totalSpace;
    private long usableSpace;
    private long unallocatedSpace;

    private final String tableFormat = "| %-80s | %-25s | %-9s | %-9s | %-9s | %-8s | %-5s | %-150s |";

    @FXML
    private void initialize() {
        driveRoot = new File(Config.alcoholDriveLetter + ":\\");

        isRunning = true;
        stopButton.setDisable(false);

        textArea.textProperty().addListener((ChangeListener<Object>) (observable, oldValue, newValue) -> textArea.setScrollTop(Double.MAX_VALUE));

        textArea.setText(String.format(tableFormat, "Recognized Image", "Label", "S/N", "TotalSize", "Calc.Size", "CRC32", "Error", "Image Path") + "\n"
                + String.format(tableFormat, "", "", "", "", "", "", "", "").replace(" ", "-"));

        new Thread(() -> {
            try (Stream<Path> stream = Files.walk(Config.batchDirectory.toPath())) {
                files = stream.map(entry -> isoMatcher.matches(entry.getFileName()) ? entry : null).filter(Objects::nonNull).sorted().toList();
            } catch (Exception e) {
                e.printStackTrace();
            }

            go();

        }).start();
    }

    private void go() {
        for (int i = 0; i < files.size(); i++) {
            /*if (i < 232) {
                continue;
            }*/
            Path imageFile = files.get(i).normalize().toAbsolutePath();
            if (!isRunning) {
                break;
            }
            for (int j = 0; j < 3; j++) {
                try {
                    process(i, imageFile);
                    break;
                } catch (Exception e) {
                    textArea.setText(textArea.getText() + "\n" + imageFile + ": " + e.getClass().getName() + ": " + e.getMessage());
                }
            }
        }

        String result = ((isRunning) ? "OK" : "Stopped...");
        JavaFxUtils.log(result);
        textArea.setText(textArea.getText() + "\n\n" + result);
        stopButton.setDisable(true);
    }

    private void process(int i, Path imageFile) throws Exception {
        Platform.runLater(() -> pathLabel.setText(String.format("[%s of %s] %s", i + 1, files.size(), imageFile)));
        unmount();
        Thread.sleep(500);
        mount(imageFile);
        Thread.sleep(1000);
        scan(imageFile);
    }

    private void scan(Path imageFile) throws Exception {
        scanDrive(driveRoot);

        boolean allIsBad = (volumeLabel == null && serialNumber == null && fileSystem == null);

        DiskImage diskImage = new DiskImage(totalSpace, usableSpace, unallocatedSpace, fileSystem,
                volumeLabel, serialNumber, FileUtils.listFiles(driveRoot), false, Config.lastDirectory);
        if (diskImage.getFiles().isEmpty() && allIsBad) {
            throw new RuntimeException(str("primary.disk.is.not.ready.error"));
        }

        boolean error = false;

        long totalSize = 0;

        for (int i = 0; i < diskImage.getFiles().size(); i++) {
            if (!isRunning) {
                break;
            }

            Path file = diskImage.getFiles().get(i);
            long size = 0;
            try {
                size = Files.size(file);
            } catch (IOException e) {
                JavaFxUtils.log(String.format("!%s: %s: %s", file, e.getClass().getSimpleName(), e.getMessage()));
            }
            totalSize += size;

            String currentFile = diskImage.getViewFileName(file);

            int finalI = i + 1;
            Platform.runLater(() -> fileLabel.setText(String.format("[%s of %s] %s", finalI, diskImage.getFiles().size(), file)));

            try {
                String newSrc32 = StringUtils.formatHex(readAndCalculateCrc32(file));

                byte[] bytes = Files.readAllBytes(file); //todo удалить если всё совпадает с новым методом

                //сравнивать на всякий случай с size
                if (bytes.length != size) {
                    throw new RuntimeException(String.format("%s: %s: %s != %s !", file, str("view.size.is.different.error"), bytes.length, size));
                }

                String oldCrc32 = BinaryUtils.crc32String(bytes);

                if (!newSrc32.equals(oldCrc32)) {
                    System.out.println("!!! " + newSrc32 + " != " + oldCrc32);
                }

                diskImage.getRecords().set(i, new FileRecord(currentFile, bytes.length, newSrc32, false));

            } catch (Exception e) {
                error = true;
                diskImage.getRecords().set(i, new FileRecord(currentFile, size, FileRecord.ERROR, true));
                JavaFxUtils.log(String.format("!%s: %s", file, str("view.read.error")));
            }
        }

        if (isRunning) {
            diskImage.setCalculatedSize(totalSize);
            diskImage.calculateCrc32();
            diskImage.setError(error);

            String recognizedImage = Storage.baseHashes.get(diskImage.getCrc32());

            String line = String.format(tableFormat, recognizedImage, volumeLabel, serialNumber, totalSpace, totalSize, diskImage.getCrc32(), error, imageFile);

            textArea.setText(textArea.getText() + "\n" + line);

            if (recognizedImage != null) {
                if (!imageFile.toString().contains(recognizedImage)) {
                    JavaFxUtils.log(imageFile + " DIR " + recognizedImage);
                }
            }
        }
    }

    public int readAndCalculateCrc32(Path path) throws IOException {
        CRC32 crc = new CRC32();
        try (SeekableByteChannel ch = Files.newByteChannel(path, StandardOpenOption.READ)) {
            ByteBuffer bf = ByteBuffer.allocate(1024 * 128);
            while (ch.read(bf) > 0) {
                bf.flip();
                crc.update(Arrays.copyOf(bf.array(), bf.remaining()));
                bf.clear();
            }
        }

        return (int) crc.getValue();
    }

    private void scanDrive(File driveRoot) throws Exception {
        volumeLabel = null;
        serialNumber = null;
        fileSystem = null;
        totalSpace = -1;

        FileStore fileStore = Files.getFileStore(driveRoot.toPath());

        volumeLabel = fileStore.name();
        fileSystem = fileStore.type();
        serialNumber = StringUtils.formatSerialNumber((int) fileStore.getAttribute("volume:vsn"));

        if (fileSystem.equals(CDFS)) {
            totalSpace = fileStore.getTotalSpace();
            usableSpace = fileStore.getUsableSpace();
            unallocatedSpace = fileStore.getUnallocatedSpace();
        }
    }

    private void mount(Path path) throws Exception {
        //"C:\Program Files (x86)\Alcohol Soft\Alcohol 52\AxCmd.exe" /L
        //"C:\Program Files (x86)\Alcohol Soft\Alcohol 52\AxCmd.exe" E: /M:"C:\\Users\\user\\Downloads\\torrents\\DUX 1.5 (Germany).cdi"
        int code = Runtime.getRuntime().exec(new String[]{
                String.format("\"%s\"", Config.alcoholPath),
                Config.alcoholDriveLetter + ":",
                String.format("/M:%s", path.normalize().toAbsolutePath())
        }).waitFor(); //69

        if (code != 69) {
            JavaFxUtils.log(String.format("Alcohol mount code != 69: %s: %s", code, path));
        }
    }

    private void unmount() throws Exception {
        //"C:\Program Files (x86)\Alcohol Soft\Alcohol 52\AxCmd.exe" E: /U
        int code = Runtime.getRuntime().exec(new String[]{
                String.format("\"%s\"", Config.alcoholPath),
                Config.alcoholDriveLetter + ":", "/U"}
        ).waitFor(); //1

        if (code != 1) {
            JavaFxUtils.log(String.format("Alcohol unmount code != 1: %s", code));
        }
    }

    public void stopButtonClick() {
        isRunning = false;
    }

    public void closeButtonClick(ActionEvent actionEvent) {
        stopButtonClick();
        JavaFxUtils.closeStage(actionEvent);
    }

    @Override
    public void close() throws IOException {
    }
}
