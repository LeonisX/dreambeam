package md.leonis.dreambeam.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import md.leonis.dreambeam.utils.*;
import org.apache.commons.lang3.StringUtils;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static md.leonis.dreambeam.utils.Config.HR;

public class PrimaryPaneController {

    public Button readFsButton;
    public Button readGdiButton;
    public Button rescanDrivesButton;
    public Label userLabel;
    public Label userFilesLabel;
    public Label baseFilesCountLabel;
    public VBox cdVBox;

    private Map<String, Path> drives;
    private String volumeLabel;

    public List<Path> listFiles(File folder) {
        return listFiles(folder, new ArrayList<>());
    }

    public List<Path> listFiles(File folder, List<Path> paths) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    listFiles(file, paths);
                }
            }
            for (File file : files) {  // костыль конечно, но так считал код на Delphi :(
                if (file.isFile()) {
                    paths.add(file.toPath());
                }
            }
        }
        return paths;
    }

    @FXML
    private void initialize() {
        inputUserName();
        readGamesDat();
        readUserFilesCount();
        createBaseDir();

        userLabel.setText(Config.user);
        userFilesLabel.setText(String.format("In your collection %s image(s).", Config.userFiles));
        long verifiedCount = Config.hashes.values().stream().filter(v -> v.contains("[!]")).count();
        baseFilesCountLabel.setText(String.format("В базе данных %s записи; %s проверены на 100%%", Config.hashes.size(), verifiedCount));

        rescanDrivesButtonClick();
    }

    private void createBaseDir() {
        try {
            FileUtils.createDirectories(Config.getBaseGamesDir());
        } catch (IOException e) {
            JavaFxUtils.showAlert("Ошибка!", "Не удалось создать!", e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void readUserFilesCount() {
        try {
            Config.userFiles = FileUtils.getFilesCount(Config.getUserDir());
        } catch (IOException e) {
            JavaFxUtils.showAlert("Ошибка!", "Не удалось посчитать имеющиеся образы!", e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void inputUserName() {
        if (!Config.isUser()) {
            JavaFxUtils.showInputDialog("Новый аккаунт", "Введите имя", null).ifPresentOrElse(this::setAndSaveUser, this::inputUserName);
        }
    }

    private void setAndSaveUser(String user) {
        Config.setUser(user);
        try {
            Config.saveProperties();
            FileUtils.createDirectories(Config.getUserDir());
        } catch (IOException e) {
            JavaFxUtils.showAlert("Ошибка!", "Не удалось сохранить файл настроек!", e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
            Config.setUser("Anonymous");
        }
    }

    private void readGamesDat() {
        Config.hashes = new HashMap<>();

        Path path = Config.getBaseGamesDatFile().normalize().toAbsolutePath();
        if (FileUtils.exists(path)) {
            try {
                Map<String, String> duplicates = new HashMap<>();
                FileUtils.readFromFile(path).forEach(line -> {
                    int index = line.lastIndexOf("-");
                    String hash = line.substring(index + 2).trim();
                    String file = line.substring(0, index - 1).trim();
                    addHash(hash, file, duplicates);
                });
                reportDuplicates(duplicates);
            } catch (Exception e) {
                JavaFxUtils.showAlert("Ошибка!", "Не удалось прочитать файл " + path + "; Он будет пересоздан.", e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
                FileUtils.deleteSilently(path);
                recalculateHashes();
            }
        } else {
            recalculateHashes();
        }
    }

    private void recalculateHashes() {
        try {
            Instant start = Instant.now();
            calculateShortList();
            FileUtils.writeToFile(Config.getBaseGamesDatFile(), Config.hashes.entrySet().stream().sorted(Map.Entry.comparingByValue()).map(e -> e.getValue() + " - " + e.getKey()).toList());
            String time = Utils.formatSeconds(Duration.between(start, Instant.now()).toMillis());
            JavaFxUtils.showAlert("DreamBeam", "Создание краткого списка завершено!", String.format("Время выполнения: %s s", time), Alert.AlertType.INFORMATION);

        } catch (IOException e) {
            JavaFxUtils.showAlert("Ошибка!", "Не удалось пересчитать краткий список!", e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void calculateShortList() throws IOException {
        Map<String, String> duplicates = new HashMap<>();
        for (Path path : FileUtils.getFilesList(Config.getBaseGamesDir())) {
            String hash = BinaryUtils.crc32String((String.join("\r\n", FileUtils.readFromFile(path)) + "\r\n").getBytes());
            String file = path.getFileName().toString();
            addHash(hash, file, duplicates);
        }
        reportDuplicates(duplicates);
    }

    private void addHash(String hash, String file, Map<String, String> duplicates) {
        if (Config.hashes.containsKey(hash)) {
            duplicates.put(Config.hashes.get(hash), file);
        }
        Config.hashes.put(hash, file);
    }

    private void reportDuplicates(Map<String, String> duplicates) {
        if (!duplicates.isEmpty()) {
            duplicates.entrySet().stream().map(e -> e.getKey() + " == " + e.getValue()).forEach(System.out::println);
            JavaFxUtils.showAlert("Ошибка!", "В базе данных есть дубликаты!",
                    duplicates.entrySet().stream().map(e -> e.getKey() + " == " + e.getValue()).collect(Collectors.joining("\n")), Alert.AlertType.WARNING);
        }
    }

    public void readCdButtonClick(ActionEvent actionEvent) {
        File file = new File(((Button) actionEvent.getSource()).getUserData().toString());
        Config.isDirectory = false;
        try {
            scanDriveAndOpenViewPane(file);
        } catch (Exception e) {
            showReadDiskAlert(file, e);
        }
    }

    public void readFsButtonClick() {
        try {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select directory");
            if (Config.lastDirectory != null) {
                directoryChooser.setInitialDirectory(Config.lastDirectory);
            }
            Config.lastDirectory = directoryChooser.showDialog(readFsButton.getScene().getWindow());

            if (Config.lastDirectory != null && Config.lastDirectory.exists()) {
                Config.isDirectory = true;
                scanDriveAndOpenViewPane(Config.lastDirectory);
            }
        } catch (Exception e) {
            showReadDiskAlert(Config.lastDirectory, e);
        }
    }

    private void showReadDiskAlert(File file, Exception e) {
        JavaFxUtils.showAlert("Ошибка!", String.format("Не удалось прочитать диск %s!", file.toString()), e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
    }

    private void scanDriveAndOpenViewPane(File driveRoot) throws IOException {
        scanDrive(driveRoot);

        JavaFxUtils.log(HR);
        JavaFxUtils.log("Файлы успешно найдены, можно сканировать.");
        JavaFxUtils.log("#Метка диска (Volume Label): " + volumeLabel);

        Config.files = listFiles(driveRoot);
        JavaFxUtils.showPane("ViewPane.fxml");
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
            JavaFxUtils.showAlert("Ошибка!", "Не удалось получить список дисков!", e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public Button createCdButton(Map.Entry<String, Path> drive) {
        Button button = new Button(String.format("Read CD-ROM (%s)", drive.getKey().replace("\\", "")));
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

                //getTotalSpace: 23982080
                //getUsableSpace: 0
                //getUnallocatedSpace: 0
                //bytesPerSector: 2048
                //getTotalSpace: 23982080
                //volume:vsn: 1233317977
                //volume:isRemovable: false
                //volume:isCdrom: true

                /*System.out.println("getTotalSpace: " + fileStore.getTotalSpace());
                System.out.println("getUsableSpace: " + fileStore.getUsableSpace());
                System.out.println("getUnallocatedSpace: " + fileStore.getUnallocatedSpace());
                System.out.println("bytesPerSector: " + fileStore.getBlockSize());
                System.out.println("getTotalSpace: " + fileStore.getTotalSpace());
                System.out.println("volume:vsn: " + fileStore.getAttribute("volume:vsn"));
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

    private void scanDrive(File driveRoot) throws IOException {
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

            System.out.println("Drive Name: " + driveRoot);
            System.out.println("getSystemTypeDescription: " + fsv.getSystemTypeDescription(driveRoot));
            System.out.println("getSystemDisplayName: " + volumeLabel);
            System.out.println("isFileSystem: " + fsv.isFileSystem(driveRoot));
            System.out.println("isDrive: " + fsv.isDrive(driveRoot));
            //System.out.println("isComputerNode: " + fsv.isComputerNode(path)); // false
            //System.out.println("isLink: " + fsv.isLink(path)); // false
            //System.out.println("isFileSystemRoot: " + fsv.isFileSystemRoot(path)); // true
            System.out.println("isFloppyDrive: " + fsv.isFloppyDrive(driveRoot));
            //System.out.println("isRoot: " + fsv.isRoot(path)); // false
            //System.out.println("isTraversable: " + fsv.isTraversable(path)); // true
            //System.out.println("isHiddenFile: " + fsv.isHiddenFile(path)); // false
        } else {
            throw new IOException(String.format("Drive %s is not ready", driveRoot.toString()));
        }
    }

    private String formatVolumeLabel(String volumeLabel) {
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
    }
}
