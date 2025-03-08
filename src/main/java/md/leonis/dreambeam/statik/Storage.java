package md.leonis.dreambeam.statik;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Storage {

    //todo may be not need
    public static final String HR = "------------------------------------------------------------------";

    //todo упорядочить, переименовать
    public static List<Path> files;
    public static List<String> saveFiles;
    public static String crc32;
    public static Map<String, String> baseHashes;
    public static Map<String, String> baseDuplicates;
    public static Map<String, String> userHashes = new HashMap<>();
    public static volatile boolean userHashesLoaded = false;
    public static Map<String, String> userDuplicates;
    public static Map<String, String> textMap;

    public static long userFiles;

    public static File lastDirectory;
    public static boolean isDirectory;
    public static String wizardName;
    public static boolean error;

}
