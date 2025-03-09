package md.leonis.dreambeam.statik;

import md.leonis.dreambeam.model.DiskImage;
import md.leonis.dreambeam.utils.FileUtils;
import md.leonis.dreambeam.utils.Utils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Storage {

    //todo may be not need
    public static final String HR = "------------------------------------------------------------------";

    // Scanned image processing
    public static DiskImage diskImage;

    public static String wizardName;    // Title, generated by Name Wizard

    // Preloaded data
    public static Map<String, DiskImage> baseFiles = new HashMap<>();
    public static Map<String, DiskImage> userFiles = new HashMap<>();
    public static Map<String, String> baseHashes; // crc32/fileName
    public static Map<String, String> userHashes = new HashMap<>(); // crc32/fileName
    public static volatile boolean baseFilesLoaded = false;
    public static volatile boolean userFilesLoaded = false;
    public static Map<String, String> baseDuplicates;
    public static Map<String, String> userDuplicates;

    public static Map<String, String> textMap;

    public static long userFilesCount; //todo may be not need in future

    public static void readGamesDat(Path path) throws Exception {
        baseHashes = new HashMap<>();
        var pair = Utils.loadShortListHashes(path);
        baseHashes = pair.getLeft();
        baseDuplicates = pair.getRight();
    }

    public static void calculateBaseHashesAndSave() throws Exception { //todo rename
        loadBaseFiles(true);
        FileUtils.writeToFile(FileUtils.getBaseGamesDatFile(), baseHashes.entrySet().stream().sorted(Map.Entry.comparingByValue()).map(e -> e.getValue() + " - " + e.getKey()).toList());
    }

    public static void loadBaseFiles(boolean force) throws Exception { //todo rename
        if (baseFilesLoaded && !force) {
            return;
        }
        baseFilesLoaded = false;
        loadFiles(baseFiles, baseHashes, baseDuplicates);
        baseFilesLoaded = true;
    }

    public static void loadUserFiles(boolean force) throws Exception {
        if (userFilesLoaded && !force) {
            return;
        }
        userFilesLoaded = false;
        loadFiles(userFiles, userHashes, userDuplicates);
        userFilesLoaded = true;
    }

    public static void loadFiles(Map<String, DiskImage> images, Map<String, String> hashes, Map<String, String> duplicates) throws Exception {
        images = new HashMap<>();
        hashes = new HashMap<>(); //todo избавиться бы
        duplicates = new HashMap<>();
        Utils.loadFiles(FileUtils.getBaseGamesDir(), images, hashes, duplicates);
    }

    public static void readUserFilesCount() {
        try {
            Storage.userFilesCount = FileUtils.getFilesCount(FileUtils.getUserDir());
        } catch (IOException e) {
            Storage.userFilesCount = 0;
        }
    }

    public static void loadTexts() throws Exception {
        textMap = Utils.loadTexts(FileUtils.getTextsDir());
    }

    public static void saveUserFile(String name) throws Exception {
        FileUtils.writeToFile(FileUtils.getUserFile(name), Storage.diskImage.getSaveLines());
        userHashes.put(diskImage.getCrc32(), name);
    }

    public static void saveUserFileToBase(String name) throws Exception {
        FileUtils.writeToFile(FileUtils.getBaseGamesFile(name), Storage.diskImage.getSaveLines());
        baseHashes.put(diskImage.getCrc32(), name);
    }
}
