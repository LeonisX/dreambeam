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

    public static final String DEFAULT_USER = "Unnamed";

    static final String resourcePath = "/fxml/";

    public static List<Path> files;
    public static List<String> saveFiles;
    public static String crc32;
    public static Map<String, String> baseHashes;
    public static Map<String, String> baseDuplicates;
    public static Map<String, String> userHashes = new HashMap<>();
    public static volatile boolean userHashesLoaded = false;
    public static Map<String, String> userDuplicates;
    public static Map<String, String> textMap;

    public static Properties properties = new Properties();
    public static Properties languages = new Properties();
    public static Locale locale = Locale.getDefault();
    public static String user = DEFAULT_USER;
    public static long userFiles;
    public static boolean admin;
    public static File lastDirectory;
    public static boolean isDirectory;
    public static String wizardName;
    public static boolean error;

    public static String projectVersion;
    public static String projectTime;


    public static Path getUserLogFile() {
        return getRootDir().resolve(user + ".log");
    }
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
            if (StringUtils.isBlank(user)) {
                user = DEFAULT_USER;
            }
            admin = "true".equals(properties.getProperty(ADMIN));
            locale = Locale.of(properties.getProperty(LOCALE).substring(0, 2), properties.getProperty(LOCALE).substring(3).toUpperCase());
        } catch (Exception ignored) {
        }
    }

    public static void loadLanguages() {
        try {
            doLoadLanguages();
        } catch (Exception e) {
            locale = Locale.of("en", "US");
            try {
                doLoadLanguages();
            } catch (Exception ignored) {
            }
        }
    }

    private static void doLoadLanguages() throws Exception {
        try (InputStream inputStream = MainApp.class.getResourceAsStream(String.format("/languages_%s.properties", locale))) {
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
        return StringUtils.isNotBlank(user) && !user.equals(DEFAULT_USER);
    }

    public static void setUser(String user) {
        Config.user = user;
        properties.put(NAME, user);
        properties.put(LOCALE, locale.toString());
        if (admin) {
            properties.put(ADMIN, admin);
        }
    }

    public static void saveProperties() throws IOException {
        properties.store(new FileOutputStream(getConfigFile().toFile()), "Settings");
    }
}
