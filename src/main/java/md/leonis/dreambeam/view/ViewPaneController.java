package md.leonis.dreambeam.view;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import md.leonis.dreambeam.model.FileRecord;
import md.leonis.dreambeam.statik.Storage;
import md.leonis.dreambeam.utils.BinaryUtils;
import md.leonis.dreambeam.utils.JavaFxUtils;
import md.leonis.dreambeam.utils.StringUtils;
import md.leonis.dreambeam.utils.Utils;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.CRC32;

import static md.leonis.dreambeam.statik.Config.str;

public class ViewPaneController implements Closeable {

    public ListView<FileRecord> filesListView;
    public Label timeLabel;
    public Button scanButton;
    public Button backButton;
    public Button breakButton;
    public ProgressBar fileProgressBar;
    public ProgressBar totalProgressBar;
    public VBox progressVBox;
    public Label fileProgressLabel;
    public Label totalProgressLabel;
    public StackPane stackPane1;
    public StackPane stackPane2;

    private Timeline timeline;

    private volatile boolean breaked;
    private volatile boolean error;

    @FXML
    private void initialize() {
        progressVBox.setManaged(false);

        filesListView.setItems(FXCollections.observableList(Storage.diskImage.getRecords()));
        filesListView.scrollTo(0);
    }

    public void backButtonClick() {
        JavaFxUtils.showPrimaryPanel();
    }

    public void scanButtonClick() {
        final Instant start = Instant.now();
        error = false;
        breaked = false;
        breakButton.setVisible(true);
        scanButton.setVisible(false);

        progressVBox.setManaged(true);
        fileProgressBar.setProgress(0); //todo обновлять прогресс файла когда научимся читать блоками
        totalProgressBar.setProgress(0);
        totalProgressLabel.setText("0%");
        AtomicBoolean tick = new AtomicBoolean(true);

        timeline = new Timeline(new KeyFrame(Duration.millis(500), e -> {
            long duration = java.time.Duration.between(start, Instant.now()).toMillis();
            String time = tick.getAndSet(!tick.get()) ? Utils.formatSeconds(duration) : Utils.formatSecondsNoTick(duration);
            timeLabel.setText(time);
        }));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();

        new Thread(() -> {
            long totalSize = 0;

            for (int i = 0; i < Storage.diskImage.getFiles().size(); i++) {
                if (breaked) {
                    break;
                }

                Path file = Storage.diskImage.getFiles().get(i);
                long size = 0;
                try {
                    size = Files.size(file);
                } catch (IOException e) {
                    JavaFxUtils.log(String.format("!%s: %s: %s", file, e.getClass().getSimpleName(), e.getMessage()));
                }
                totalSize += size;

                String currentFile = Storage.diskImage.getViewFileName(file);

                Platform.runLater(() -> {
                    fileProgressBar.setProgress(0);
                    fileProgressLabel.setText(currentFile);
                });

                try {
                    String newSrc32 = StringUtils.formatHex(readAndCalculateCrc32(file, size));

                    byte[] bytes = Files.readAllBytes(file); //todo удалить если всё совпадает с новым методом

                    //сравнивать на всякий случай с size
                    if (bytes.length != size) {
                        throw new RuntimeException(String.format("%s: %s: %s != %s !", file, str("view.size.is.different.error"), bytes.length, size));
                    }

                    String oldCrc32 = BinaryUtils.crc32String(bytes);

                    if (!newSrc32.equals(oldCrc32)) {
                        System.out.println("!!! " + newSrc32 + " != " + oldCrc32);
                    }

                    Storage.diskImage.getRecords().set(i, new FileRecord(currentFile, bytes.length, newSrc32, false));

                    double percents = i * 1.0 / Storage.diskImage.getFiles().size();
                    Platform.runLater(() -> {
                        totalProgressLabel.setText(String.format("%.2f%%", percents * 100));
                        totalProgressBar.setProgress(percents);
                    });
                    refreshControls(i);

                } catch (Exception e) {
                    error = true;
                    Storage.diskImage.getRecords().set(i, new FileRecord(currentFile, size, FileRecord.ERROR, true));
                    refreshControls(i);
                    JavaFxUtils.log(String.format("!%s: %s", file, str("view.read.error")));
                }
            }

            Platform.runLater(timeline::stop);

            if (!breaked) {
                long duration = java.time.Duration.between(start, Instant.now()).toMillis();
                JavaFxUtils.log(String.format("@%s: %s", str("view.scan.time"), Utils.formatSeconds(duration)));

                Storage.diskImage.setCalculatedSize(totalSize);
                Storage.diskImage.calculateCrc32();
                Storage.diskImage.setError(error);

                JavaFxUtils.showSavePanel();
            }
        }).start();
    }

    public int readAndCalculateCrc32(Path path, long size) throws IOException {
        CRC32 crc = new CRC32();
        try (SeekableByteChannel ch = Files.newByteChannel(path, StandardOpenOption.READ)) {
            ByteBuffer bf = ByteBuffer.allocate(1024 * 128);
            int completed = 0;
            while (ch.read(bf) > 0) {
                bf.flip();
                var array = Arrays.copyOf(bf.array(), bf.remaining());
                completed += array.length;
                double percents = completed * 1.0 / size;
                Platform.runLater(() -> fileProgressBar.setProgress(percents));
                crc.update(array);
                bf.clear();
            }
        }

        return (int) crc.getValue();
    }

    private void refreshControls(int i) {
        Platform.runLater(() -> {
            getFirstAndLast(filesListView);
            if (i >= last) {
                filesListView.scrollTo(first + 2);
            }
            filesListView.refresh();
        });
    }

    private int first = 0;
    private int last = 0;

    public void getFirstAndLast(ListView<?> t) {
        try {
            ListViewSkin<?> ts = (ListViewSkin<?>) t.getSkin();
            VirtualFlow<?> vf = (VirtualFlow<?>) ts.getChildren().get(0);
            first = vf.getFirstVisibleCell().getIndex();
            last = vf.getLastVisibleCell().getIndex();
        } catch (Exception ignored) {
        }
    }

    public void interruptButtonClick() {
        breaked = true;
        breakButton.setVisible(false);
        scanButton.setVisible(true);

        JavaFxUtils.log(String.format("!%s", str("view.interrupted")));
        JavaFxUtils.showPrimaryPanel();
    }

    @Override
    public void close() throws IOException {
        breaked = true;
        if (timeline != null) {
            timeline.stop();
        }
    }
}
