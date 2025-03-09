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
    public static volatile boolean userHashesLoaded = false;
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

    public static void calculateBaseHashesAndSave() throws Exception {
        calculateBaseHashes();
        FileUtils.writeToFile(FileUtils.getBaseGamesDatFile(), baseHashes.entrySet().stream().sorted(Map.Entry.comparingByValue()).map(e -> e.getValue() + " - " + e.getKey()).toList());
    }

    public static void calculateBaseHashes() throws Exception { //todo сделать по аналогии с calculateUserHashes
        var pair = Utils.calculateHashes(FileUtils.getBaseGamesDir());
        baseHashes = pair.getLeft();
        baseDuplicates = pair.getRight();
    }

    public static void calculateUserHashes(boolean force) throws Exception {
        if (userHashesLoaded && !force) {
            return;
        }
        userHashesLoaded = false;
        var pair = Utils.calculateHashes(FileUtils.getUserDir());
        userHashes = pair.getLeft();
        userDuplicates = pair.getRight();
        userHashesLoaded = true;
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
