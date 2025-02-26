package md.leonis.dreambeam.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import md.leonis.dreambeam.utils.Config;
import md.leonis.dreambeam.utils.FileUtils;
import md.leonis.dreambeam.utils.JavaFxUtils;
import org.apache.commons.lang3.StringUtils;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PrimaryPaneController {

    public Button readCdButton;
    public Button readFsButton;
    public Button readGdiButton;
    public Button rescanDrivesButton;
    public Label userLabel;
    public Label userFilesLabel;
    public Label baseFilesCountLabel;

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

        userLabel.setText(Config.user);
        userFilesLabel.setText(String.format("In your collection %s image(s).", Config.userFiles));
        long verifiedCount = Config.hashes.values().stream().filter(v -> v.contains("[!]")).count();
        baseFilesCountLabel.setText(String.format("В базе данных %s записи; %s проверены на 100%%", Config.hashes.size(), verifiedCount));
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
            FileUtils.createDirectory(Config.getUserDir());
        } catch (IOException e) {
            JavaFxUtils.showAlert("Ошибка!", "Не удалось сохранить файл настроек!", e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
            Config.setUser("Anonymous");
        }
    }

    private void readGamesDat() {
        Config.hashes = new HashMap<>();

        Path path = Config.getBaseGamesDatFile().normalize().toAbsolutePath();
        System.out.println(path);
        if (FileUtils.exists(path)) {
            try {
                FileUtils.readFromFile(path).forEach(line -> {
                    int index = line.lastIndexOf("-");
                    Config.hashes.put(line.substring(index + 2).trim(), line.substring(0, index - 1).trim());
                });
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
        //todo перечитать, время, сохранить
        JavaFxUtils.showAlert("DreamBeam", "Создание краткого списка завершено!", "Время выполнения: 5 s", Alert.AlertType.INFORMATION);
    }



    //todo drives
    private void readDrives() {
        FileSystemView fsv = FileSystemView.getFileSystemView();

        for (File path : File.listRoots()) {
            try {
                System.out.println("Drive Name: " + path);
                System.out.println("getSystemTypeDescription: " + fsv.getSystemTypeDescription(path));

                String volumeLabel = fsv.getSystemDisplayName(path);
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

                System.out.println("getSystemDisplayName: " + volumeLabel);
                System.out.println("isFileSystem: " + fsv.isFileSystem(path));
                System.out.println("isDrive: " + fsv.isDrive(path));
                //System.out.println("isComputerNode: " + fsv.isComputerNode(path)); // false
                //System.out.println("isLink: " + fsv.isLink(path)); // false
                //System.out.println("isFileSystemRoot: " + fsv.isFileSystemRoot(path)); // true
                System.out.println("isFloppyDrive: " + fsv.isFloppyDrive(path));
                //System.out.println("isRoot: " + fsv.isRoot(path)); // false
                //System.out.println("isTraversable: " + fsv.isTraversable(path)); // true
                //System.out.println("isHiddenFile: " + fsv.isHiddenFile(path)); // false
            } catch (Exception e) {
                System.out.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }

        System.out.println(FileSystems.getDefault().supportedFileAttributeViews());

        for (Path root : FileSystems.getDefault().getRootDirectories()) {
            System.out.println(root.toString());
            try {
                FileStore fileStore = Files.getFileStore(root);

                System.out.println("getTotalSpace: " + fileStore.getTotalSpace());
                System.out.println("getUsableSpace: " + fileStore.getUsableSpace());
                System.out.println("getUnallocatedSpace: " + fileStore.getUnallocatedSpace());
                System.out.println("bytesPerSector: " + fileStore.getBlockSize());
                System.out.println("getTotalSpace: " + fileStore.getTotalSpace());
                System.out.println("volume:vsn: " + fileStore.getAttribute("volume:vsn"));
                System.out.println("volume:isRemovable: " + fileStore.getAttribute("volume:isRemovable"));
                System.out.println("volume:isCdrom: " + fileStore.getAttribute("volume:isCdrom"));
                //System.out.println("readonly: " + fileStore.getAttribute("readonly"));
                //System.out.println("hidden: " + fileStore.getAttribute("hidden"));
                //System.out.println("system: " + fileStore.getAttribute("system"));
                //System.out.println("archive: " + fileStore.getAttribute("archive"));
                //System.out.println("lastModifiedTime: " + fileStore.getAttribute("lastModifiedTime")); // FileTime
                //System.out.println("lastAccessTime: " + fileStore.getAttribute("lastAccessTime")); // FileTime
                //System.out.println("creationTime: " + fileStore.getAttribute("creationTime")); // FileTime
                //System.out.println("size: " + fileStore.getAttribute("size"));
                //System.out.println("isRegularFile: " + fileStore.getAttribute("isRegularFile"));
                //System.out.println("isDirectory: " + fileStore.getAttribute("isDirectory"));
                //System.out.println("isSymbolicLink: " + fileStore.getAttribute("isSymbolicLink"));
                //System.out.println("isOther: " + fileStore.getAttribute("isOther"));
                //System.out.println("fileKey: " + fileStore.getAttribute("fileKey")); // Object
            } catch (Exception e) {
                System.out.println(e.getClass().getName() + ": " + e.getMessage());
            }
        }
    }

    public void readCdButtonClick() {
        Config.files = listFiles(new File("E:\\"));
        JavaFxUtils.showPane("ViewPane.fxml");
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

    public void readFsButtonClick() {
        JavaFxUtils.showPane("SavePane.fxml");
    }

    public void readGdiButtonClick() {
    }

    public void rescanDrivesButtonClick() {
        JavaFxUtils.showPane("PrimaryPane.fxml");
    }
}
