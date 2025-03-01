package md.leonis.dreambeam.utils;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Config {

    //todo may be not need
    public static final String HR = "------------------------------------------------------------------";

    private static final String NAME = "Name";
    private static final String ADMIN = "Admin";

    public static Properties languageTable;

    static final String resourcePath = "/fxml/";

    public static List<Path> files;
    public static List<String> saveFiles;
    public static String crc32;
    public static Map<String, String> baseHashes;
    public static Map<String, String> baseDuplicates;
    public static Map<String, String> userHashes;
    public static Map<String, String> userDuplicates;
    public static Map<String, String> textMap;

    public static Properties properties = new Properties();
    public static String user;
    public static long userFiles;
    public static boolean admin;
    public static File lastDirectory;
    public static boolean isDirectory;
    public static String wizardName;
    public static boolean error;


    public static Path getUserFile(String fileName) {
        return getUserDir().resolve(fileName);
    }

    public static Path getUserDir() {
        return getRootDir().resolve(user);
    }

    public static Path getConfigFile() {
        return getRootDir().resolve("DreamBeam.ini");
    }

    public static Path getTextFile(String fileName) {
        return getTextsDir().resolve(fileName);
    }

    public static Path getTextsDir() {
        return getBaseDir().resolve("txtz");
    }

    public static Path getBaseGamesDatFile() {
        return getBaseDir().resolve("games.dat");
    }

    public static Path getBaseGamesFile(String fileName) {
        return getBaseGamesDir().resolve(fileName);
    }

    public static Path getBaseGamesDir() {
        return getBaseDir().resolve("games");
    }

    public static Path getBaseDir() {
        return getRootDir().resolve("Base");
    }

    public static Path getRootDir() {
        return Paths.get(".");
    }

    public static void loadProperties() {
        try (InputStream inputStream = new FileInputStream(getConfigFile().toFile())) {
            properties.load(inputStream);
            user = properties.getProperty(NAME);
            admin = "true".equals(properties.getProperty(ADMIN));
        } catch (Exception ignored) {
        }
    }

    public static boolean isUser() {
        return StringUtils.isNotBlank(user);
    }

    public static void setUser(String user) {
        Config.user = user;
        properties.put(NAME, user);
        if (admin) {
            properties.put(ADMIN, admin);
        }
    }

    public static void saveProperties() throws IOException {
        properties.store(new FileOutputStream(getConfigFile().toFile()), "Settings");
    }

    public static String getKeyByValue(char value) {
        return getKeyByValue(value + "");
    }

    public static String getKeyByValue(String value) {
        return Config.languageTable.entrySet().stream()
                .filter(e -> {
                    String val = e.getValue().toString();
                    if (val.length() == 1) {
                        return val.equals(value);
                    } else {
                        return val.split(";")[0].trim().equals(value);
                    }
                }).findFirst()
                .map(objectEntry -> objectEntry.getKey().toString()).orElse("");
    }
}
