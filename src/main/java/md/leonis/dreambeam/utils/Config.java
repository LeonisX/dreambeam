package md.leonis.dreambeam.utils;

import md.leonis.dreambeam.MainApp;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Config {

    //todo may be not need
    public static final String HR = "------------------------------------------------------------------";

    private static final String NAME = "Name";
    private static final String ADMIN = "Admin";
    private static final String LOCALE = "Locale";

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
    public static Properties languages = new Properties();
    public static Locale locale = Locale.getDefault();
    public static String user;
    public static long userFiles;
    public static boolean admin;
    public static File lastDirectory;
    public static boolean isDirectory;
    public static String wizardName;
    public static boolean error;

    public static String projectVersion;
    public static String projectTime;


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
            locale = new Locale(properties.getProperty(LOCALE));
        } catch (Exception ignored) {
        }
    }

    public static void loadLanguages() {
        try {
            doLoadLanguages();
        } catch (Exception e) {
            locale = new Locale("en_US");
            try {
                doLoadLanguages();
            } catch (Exception ignored) {
            }
        }
    }

    private static void doLoadLanguages() throws Exception {
        try (InputStream inputStream = MainApp.class.getResourceAsStream(String.format("/lang/languages_%s.properties", locale.toString()))) {
            languages.load(new InputStreamReader(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8));
        }
    }

    public static String str(String key) {
        return languages.getProperty(key, key);
    }

    public static String strError() {
        return languages.getProperty("error", "error");
    }

    public static void loadAppProperties() {
        Properties prop = new Properties();
        try (InputStream inputStream = MainApp.class.getResourceAsStream("/app.properties")) {
            prop.load(inputStream);

            projectVersion = prop.getProperty("version", "@!#?@!");
            projectTime = prop.getProperty("date", "");
            projectTime = StringUtils.isBlank(projectTime) ? projectTime : String.format("(%s)", projectTime);

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
