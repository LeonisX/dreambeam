package md.leonis.dreambeam.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
        createBaseDir();

        userLabel.setText(Config.user);
        userFilesLabel.setText(String.format("In your collection %s image(s).", Config.userFiles));
        long verifiedCount = Config.hashes.values().stream().filter(v -> v.contains("[!]")).count();
        baseFilesCountLabel.setText(String.format("В базе данных %s записи; %s проверены на 100%%", Config.hashes.size(), verifiedCount));
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

    //todo файлов 1977 а хэшей только 1976 - где-то в файлах сидит дубликат!
    //Caesars Palace - Special Edition (Rus) (Kudos)
    //Caesars Palace 2000 - Millennium Gold Edition (Rus) (Kudos)
    //todo решить какой файл правильный
    //todo аудит дубликатов юзера (отдельная функция в меню)

    //todo перебрать, сравнить визуально, может новая утилита неправильно что-то считает (что врядли)
    //2-in-1 European Super League, NBA Hoopz (Rus) (Kudos) == 2-in-1 NBA Hoopz, European Super League (Rus) (Kudos)
    //Resident Evil 2 (Disc 1 of 2) (PAL-E) (Eng) (Kalisto) == Resident Evil 2 (Disk 1 of 2) (PAL-E) (Eng)
    //Draconus - Cult of the Wyrm (Disc 2 of 2) (Rus) (Kudos) == Draconus - Cult of the Wyrm (Disc 2 of 2) (Rus) (Kudos) (Alt) [!]
    //Urban Chaos (Non-Rus) (Studia Max) == Urban Chaos (PAL-E) (Eng) (-)
    //Hidden & Dangerous (Rus) (Vector) (Alt) == Hidden & Dangerous (Rus) (Vector) [!]
    //Shenmue (Disc 3 of 3) (Eng) (Kudos) == Shenmue (Disc 3 of 3) (PAL-E) (Eng) [!]
    //2-in-1 Raptors! Quake, Heavy Metal - Geomatrix (Rus) (Kudos) == 2-in-1 Raptors! Quake, Heavy Metal - Geomatrix (Rus) (Kudos) (Alt)
    //Draconus - Cult of the Wyrm (Disc 1 of 2) (Rus) (Kudos) == Draconus - Cult of the Wyrm (Disc 1 of 2) (Rus) (Kudos) (Alt2)
    //Godzilla Generations - Maximum Impact (Rus) (Kudos) (Alt) == Godzilla Generations - Maximum Impact (Rus) (Kudos) [!]
    //Caesars Palace - Special Edition (Rus) (Kudos) == Caesars Palace 2000 - Millennium Gold Edition (Rus) (Kudos)
    //Shenmue (Disc 2 of 3) (Eng) (Kudos) == Shenmue (Disc 2 of 3) (PAL-E) (Eng) [!]
    //Maximum Pool (Rus) (Kudos) == Maximum Pool (Rus) (Kudos) (Alt)
    //Shenmue (Disc 1 of 3) (Eng) (Kudos) == Shenmue (Disc 1 of 3) (PAL-E) (Eng) [!]
    //D2 (Disc 1 of 4) (NTSC-U) (Eng) (Hykan) (Alt) == D2 (Disc 1 of 4) (NTSC-U) (Eng) (Hykan) [!]
    //Tomb Raider - Chronicles (Rus) (Vector) (Alt) == Tomb Raider - Chronicles (Rus) (Vector) [!]
    //D2 (Disc 3a of 4) (NTSC-U) (Eng) (Hykan) (Alt) == D2 (Disc 3a of 4) (NTSC-U) (Eng) (Hykan) [!]
    //Millenium Soldier (Rus) (Vector) [!] == Millenium Soldier - Expendable (Rus) (Vector) (Alt)
    //Taxi 2 (Rus) (Kudos) == Taxi 2 (Rus) (Kudos) (Alt)

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
