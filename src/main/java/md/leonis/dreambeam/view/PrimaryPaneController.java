package md.leonis.dreambeam.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import md.leonis.dreambeam.model.DiskImage;
import md.leonis.dreambeam.model.Version;
import md.leonis.dreambeam.statik.Config;
import md.leonis.dreambeam.statik.Storage;
import md.leonis.dreambeam.statik.VersionConfig;
import md.leonis.dreambeam.utils.FileUtils;
import md.leonis.dreambeam.utils.JavaFxUtils;
import md.leonis.dreambeam.utils.StringUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static md.leonis.dreambeam.statik.Config.*;

public class PrimaryPaneController implements Closeable {

    /*private static final String SERIAL = "SERIAL:";
    private static final String LABEL = "LABEL:";
    private static final String SYSTEM = "SYSTEM:";*/
    private static final String CDFS = "CDFS";

    public Button readFsButton;
    public Button readGdiButton;
    public Button rescanDrivesButton;
    public Label userLabel;
    public Button renameButton;
    public Label userFilesLabel;
    public Label baseFilesCountLabel;
    public VBox cdVBox;
    public Button viewBaseButton;
    public TextArea dragTextArea;
    public Button batchScanButton;

    private Map<String, Path> drives;
    private String volumeLabel;
    private String serialNumber;
    private String fileSystem;
    private long totalSpace;
    private long usableSpace;
    private long unallocatedSpace;

    @FXML
    private void initialize() {
        readGamesDat();
        createBaseDir();

        createUserDir();
        readFiles();
        updateUserData();
        long verifiedCount = Storage.baseHashes.values().stream().filter(v -> v.contains("[!]")).count();
        baseFilesCountLabel.setText(String.format(str("primary.base.disks.count"), Storage.baseHashes.size(), verifiedCount));

        rescanDrivesButtonClick();

        checkForUpdates();
    }

    private void readGamesDat() {
        Path path = FileUtils.getBaseGamesDatFile().normalize().toAbsolutePath();
        try {
            if (FileUtils.exists(path)) {
                try {
                    Storage.readGamesDat(path);
                } catch (Exception e) {
                    JavaFxUtils.showAlert(strError(), String.format(str("primary.short.list.file.read.error"), path), e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
                    FileUtils.deleteSilently(path);
                    MainStageController.calculateBaseHashesAndSave();
                }
            } else {
                MainStageController.calculateBaseHashesAndSave();
            }
        } catch (Exception ex) {
            JavaFxUtils.showAlert(strError(), String.format(str("primary.short.list.file.read.error"), path), ex.getClass().getSimpleName() + ": " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void checkForUpdates() {
        new Thread(() -> {
            try {
                Version remoteVersion = VersionConfig.loadRemote();

                if (!notifiedVersion.equals(remoteVersion.version())) {
                    JavaFxUtils.showAlert(str("primary.update.notification.alert"), str("primary.new.version.available.alert"),
                            String.format("DreamBeam %s", remoteVersion.version()), Alert.AlertType.INFORMATION);
                    notifiedVersion = remoteVersion.version();
                    saveProperties();
                } else {
                    JavaFxUtils.log(str("primary.log.no.update"));
                }
            } catch (Exception e) {
                JavaFxUtils.log(String.format("!%s: %s: %s", str("primary.log.new.version.verification.error"), e.getClass().getSimpleName(), e.getMessage()));
            }
        }).start();
    }

    private void readFiles() {
        new Thread(() -> {
            MainStageController.calculateBaseHashes(false, false);
            JavaFxUtils.log("#" + str("primary.log.base.files.loaded"));
            MainStageController.calculateUserHashes(false, false);
            JavaFxUtils.log("#" + str("primary.log.user.files.loaded"));
        }).start();
    }

    private void updateUserData() {
        renameButton.setVisible(!isUser());
        userLabel.setText(Config.user);
        userFilesLabel.setText(String.format(str("primary.user.disks.count"), Storage.readUserFilesCount()));
    }

    private void createBaseDir() {
        try {
            FileUtils.createDirectories(FileUtils.getBaseGamesDir());
        } catch (IOException e) {
            JavaFxUtils.showAlert(strError(), str("primary.create.base.directory.error"), e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void createUserDir() {
        if (StringUtils.isNotBlank(Config.user)) {
            try {
                FileUtils.createDirectories(FileUtils.getUserDir());
            } catch (IOException e) {
                JavaFxUtils.showAlert(strError(), str("primary.create.user.directory.error"), e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    public void readCdButtonClick(ActionEvent actionEvent) {
        File file = new File(((Button) actionEvent.getSource()).getUserData().toString());
        try {
            scanDriveAndOpenViewPane(file, false);
        } catch (Exception e) {
            showReadDiskAlert(file, e);
        }
    }

    public void readFsButtonClick() {
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle(str("primary.select.directory"));
            if (Config.lastDirectory != null) {
                directoryChooser.setInitialDirectory(Config.lastDirectory);
            }
            Config.lastDirectory = directoryChooser.showDialog(readFsButton.getScene().getWindow());

            if (Config.lastDirectory != null && Config.lastDirectory.exists()) {
                scanDriveAndOpenViewPane(Config.lastDirectory, true);
            }
        } catch (Exception e) {
            showReadDiskAlert(Config.lastDirectory, e);
        }
    }

    private void showReadDiskAlert(File file, Exception e) {
        JavaFxUtils.showAlert(strError(), String.format(str("primary.disk.read.error"), file.toString()), e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
    }

    private void scanDriveAndOpenViewPane(File driveRoot, boolean isDirectory) throws IOException {
        scanDrive(driveRoot);

        JavaFxUtils.log(Storage.HR);
        JavaFxUtils.log(str("primary.log.disk.read.ok.can.scan"));
        if (fileSystem != null) {
            String prefix = fileSystem.equals(CDFS) ? "#" : "";
            JavaFxUtils.log(String.format("%s%s: %s", prefix, str("primary.log.file.system"), fileSystem));
        }
        if (volumeLabel != null) {
            JavaFxUtils.log(String.format("#%s: %s", str("primary.log.volume.label"), volumeLabel));
        }
        if (serialNumber != null) {
            JavaFxUtils.log(String.format("%s: %s", str("primary.log.serial.number"), serialNumber));
        }
        if (totalSpace != -1) {
            JavaFxUtils.log(String.format("%s: %s", str("primary.total.space"), totalSpace));
        }
        if (usableSpace != 0 || unallocatedSpace != 0) {
            JavaFxUtils.log(String.format("%s: %s: %s", str("primary.wrong.disk.space.data"), usableSpace, unallocatedSpace));
        }
        boolean allIsBad = (volumeLabel == null && serialNumber == null && fileSystem == null);
        if (allIsBad) {
            JavaFxUtils.log("!" + str("primary.log.volume.label.read.error"));
        }

        Storage.diskImage = new DiskImage(totalSpace, usableSpace, unallocatedSpace, fileSystem,
                volumeLabel, serialNumber, FileUtils.listFiles(driveRoot), isDirectory, Config.lastDirectory);
        if (Storage.diskImage.getFiles().isEmpty() && allIsBad) {
            throw new RuntimeException(str("primary.disk.is.not.ready.error"));
        } else {
            JavaFxUtils.showViewPanel();
        }
    }

    public void readGdiButtonClick() {
        /*FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        if (Config.lastPath != null) {
            fileChooser.setInitialDirectory(Config.lastPath.getParentFile());
        }
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All files", "*.*")
        );
        Config.lastPath = fileChooser.showOpenDialog(readFsButton.getScene().getWindow());

        if (Config.lastPath != null && Config.lastPath.exists()) {
            JavaFxUtils.showPane("ViewPane.fxml");
        }*/
    }

    public void rescanDrivesButtonClick() {
        try {
            rescanDrives();
            cdVBox.getChildren().clear();
            drives.entrySet().forEach(drive -> cdVBox.getChildren().add(createCdButton(drive)));
        } catch (Exception e) {
            JavaFxUtils.showAlert(strError(), str("primary.disks.list.read.error"), e.getClass().getName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public Button createCdButton(Map.Entry<String, Path> drive) {
        Button button = new Button(String.format(str("primary.read.cd"), drive.getKey().replace("\\", "")));
        button.setUserData(drive.getKey());
        button.setOnAction(this::readCdButtonClick);

        return button;
    }

    private void rescanDrives() throws IOException {
        drives = new HashMap<>();

        for (Path root : FileSystems.getDefault().getRootDirectories()) {
            //System.out.println(root.toString());
            try {
                FileStore fileStore = Files.getFileStore(root);

                //name: DuxRev-RC1
                //type: CDFS
                //getTotalSpace: 23982080
                //getUsableSpace: 0
                //getUnallocatedSpace: 0
                //bytesPerSector: 2048
                //volume:vsn: 1233317977
                //volume:isRemovable: false
                //volume:isCdrom: true

                /*System.out.println("name: " + fileStore.name());
                System.out.println("type: " + fileStore.type());
                System.out.println("getTotalSpace: " + fileStore.getTotalSpace());
                System.out.println("getUsableSpace: " + fileStore.getUsableSpace());
                System.out.println("getUnallocatedSpace: " + fileStore.getUnallocatedSpace());
                System.out.println("bytesPerSector: " + fileStore.getBlockSize());
                System.out.println("volume:vsn: " + fileStore.getAttribute("volume:vsn")); // Serial number
                System.out.println("volume:isRemovable: " + fileStore.getAttribute("volume:isRemovable"));
                System.out.println("volume:isCdrom: " + fileStore.getAttribute("volume:isCdrom"));*/

                if ((Boolean) fileStore.getAttribute("volume:isCdrom")) {
                    drives.put(root.toString(), root);
                }
            } catch (Exception e) {
                if (e.getMessage().contains("The device is not ready")) {
                    drives.put(root.toString(), root);
                } else {
                    throw e;
                }
            }
        }
    }

    private void scanDrive(File driveRoot) {
        volumeLabel = null;
        serialNumber = null;
        fileSystem = null;
        totalSpace = -1;

        try {
            FileStore fileStore = Files.getFileStore(driveRoot.toPath());

            volumeLabel = fileStore.name();
            fileSystem = fileStore.type();
            serialNumber = StringUtils.formatSerialNumber((int) fileStore.getAttribute("volume:vsn"));

            if (fileSystem.equals(CDFS)) {
                totalSpace = fileStore.getTotalSpace();
                usableSpace = fileStore.getUsableSpace();
                unallocatedSpace = fileStore.getUnallocatedSpace();
            }
        } catch (Exception e) {
            JavaFxUtils.log("!" + e.getClass().getName() + ": " + e.getMessage());
        }
    }

    // C code (vol.exe)
    /*private void scanDrive(File driveRoot) {
        volumeLabel = null;
        serialNumber = null;
        fileSystem = null;
        scanDriveC(driveRoot);
    }

    private void scanDriveC(File driveRoot) {
        try {
            String[] command = new String[]{"vol", driveRoot.toString()};
            final Process process = Runtime.getRuntime().exec(command);

            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            List<String> lines = new ArrayList<>();
            String line;

            while ((line = input.readLine()) != null) {
                lines.add(line);
            }

            process.waitFor();

            if (lines.size() == 3) {
                volumeLabel = lines.get(1).replace(LABEL, "").trim();
                fileSystem = lines.get(2).replace(SYSTEM, "").trim();
                serialNumber = StringUtils.formatSerialNumber(Integer.parseInt(lines.get(0).replace(SERIAL, "").trim()));
            }
        } catch (Exception e) {
            JavaFxUtils.log("!" + e.getClass().getName() + ": " + e.getMessage());
        }
    }*/

    /*private void scanDrivePowerShell(File driveRoot) throws Exception {
        String driveLetter = driveRoot.toString().substring(0, 1).toUpperCase();
        String[] command = new String[]{"PowerShell", "Get-Volume", "-DriveLetter", driveLetter};
        final Process process = Runtime.getRuntime().exec(command);

        BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
        List<String> lines = new ArrayList<>();
        String line;

        while ((line = input.readLine()) != null) {
            if (line.startsWith(driveLetter)) {
                lines.add(line);
            }
        }

        process.waitFor();

        if (lines.isEmpty()) {
            throw new IOException(str("primary.volume.label.read.error"));
        }

        List<String> filtered = Arrays.stream(lines.get(lines.size() - 1).split(" ")).filter(StringUtils::isNotBlank).toList();
        //[E           DuxRev-RC1   Unknown        CD-ROM    Healthy      OK                          0 B 22.87 MB]
        //[E, DuxRev-RC1, Unknown, CD-ROM, Healthy, OK, 0, B, 22.87, MB]

        //[F                        Unknown        CD-ROM    Healthy      Unknown                     0 B  0 B]
        //[F, Unknown, CD-ROM, Healthy, Unknown, 0, B, 0, B]

        if (!filtered.get(filtered.size() - 2).equals("0")) {
            volumeLabel = filtered.get(1);
        } else {
            throw new IOException(str("primary.volume.label.read.error"));
        }
    }*/

    // AWT problem :(
    /*private void scanDrive(File driveRoot) throws IOException {
        System.out.println(driveRoot.toString());
        FileSystemView fsv = FileSystemView.getFileSystemView();

        if (fsv.getSystemTypeDescription(driveRoot) != null) {

            volumeLabel = formatVolumeLabel(fsv.getSystemDisplayName(driveRoot));
            //Drive Name: E:\
            //getSystemTypeDescription: CD Drive
            //getSystemDisplayName: DuxRev-RC1
            //isFileSystem: true
            //isDrive: true
            //isFloppyDrive: false

            //Drive Name: E:\
            //getSystemTypeDescription: null
            //getSystemDisplayName:
            //isFileSystem: true
            //isDrive: false
            //isFloppyDrive: false

            *//*System.out.println("Drive Name: " + driveRoot);
            System.out.println("getSystemTypeDescription: " + fsv.getSystemTypeDescription(driveRoot));
            System.out.println("getSystemDisplayName: " + volumeLabel);
            System.out.println("isFileSystem: " + fsv.isFileSystem(driveRoot));
            System.out.println("isDrive: " + fsv.isDrive(driveRoot));
            System.out.println("isFloppyDrive: " + fsv.isFloppyDrive(driveRoot));*//*
            //System.out.println("isComputerNode: " + fsv.isComputerNode(path)); // false
            //System.out.println("isLink: " + fsv.isLink(path)); // false
            //System.out.println("isFileSystemRoot: " + fsv.isFileSystemRoot(path)); // true
            //System.out.println("isRoot: " + fsv.isRoot(path)); // false
            //System.out.println("isTraversable: " + fsv.isTraversable(path)); // true
            //System.out.println("isHiddenFile: " + fsv.isHiddenFile(path)); // false
        } else {
            throw new IOException(Привод не готов);
        }
    }*/

    /*private String formatVolumeLabel(String volumeLabel) {
        if (!StringUtils.isBlank(volumeLabel)) {
            int index = volumeLabel.indexOf(") ");
            if (index == -1) {
                index = volumeLabel.lastIndexOf(" (");
                if (index > 0) {
                    volumeLabel = volumeLabel.substring(0, index).trim();
                }
            } else {
                volumeLabel = volumeLabel.substring(index + 2).trim();
            }
        }

        return volumeLabel;
    }*/

    public void viewBaseButtonClick() {
        JavaFxUtils.showBaseWindow();
    }

    public void renameButtonClick() {
        inputUserName();
    }

    private void inputUserName() {
        JavaFxUtils.showInputDialog(str("primary.new.user"), str("primary.enter.your.name"), null).ifPresentOrElse(this::setAndSaveUser, this::inputUserName);
    }

    private void setAndSaveUser(String user) {
        if (!Objects.equals(Config.user, user)) {
            Path oldUserDir = FileUtils.getUserDir();
            Path oldUserLogFile = FileUtils.getUserLogFile();
            Config.setUser(user);
            FileUtils.renameFileSilently(oldUserDir, FileUtils.getUserDir());
            FileUtils.renameFileSilently(oldUserLogFile, FileUtils.getUserLogFile());
            try {
                Config.saveProperties();
                FileUtils.createDirectories(FileUtils.getUserDir());
            } catch (IOException e) {
                JavaFxUtils.showAlert(strError(), str("primary.config.file.save.error"), e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
                Config.setUser(Config.DEFAULT_USER);
            }
        }

        updateUserData();
    }

    @Override
    public void close() throws IOException {
    }

    public void batchScanButtonClick() {
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("TODO");
            if (Config.batchDirectory != null) {
                directoryChooser.setInitialDirectory(Config.batchDirectory);
            }
            batchDirectory = directoryChooser.showDialog(readFsButton.getScene().getWindow());

            JavaFxUtils.showBatchWindow();
        } catch (Exception e) {
            showReadDiskAlert(Config.lastDirectory, e);
        }
    }
}
