package md.leonis.dreambeam.statik;

import md.leonis.dreambeam.MainApp;
import md.leonis.dreambeam.utils.FileUtils;
import md.leonis.dreambeam.utils.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;

public class Config {

    //todo нормальные названия настроек
    private static final String NAME = "Name";
    private static final String ADMIN = "Admin";
    private static final String LOCALE = "Locale";
    private static final String UPDATE_NOTIFICATION = "UpdateNotification";
    private static final String NOTIFIED_VERSION = "NotifiedVersion";
    private static final String ALCOHOL_PATH="AlcoholPath";
    private static final String ALCOHOL_LETTER="AlcoholLetter";

    public static final String DEFAULT_USER = "Unnamed";
    private static final String ALCOHOL_DEFAULT_PATH="C:\\Program Files (x86)\\Alcohol Soft\\Alcohol 52\\AxCmd.exe";
    private static final String ALCOHOL_DEFAULT_LETTER="E";

    public static Properties properties = new Properties();
    public static Properties languages = new Properties();
    public static Locale locale = Locale.getDefault();
    public static boolean updateNotification;
    public static String notifiedVersion;
    public static String user = DEFAULT_USER;
    public static boolean admin;

    public static String alcoholPath;
    public static String alcoholDriveLetter;


    // UI settings
    public static File lastDirectory; // Last directory for DirectoryChooser. //todo save to file
    public static File batchDirectory;

    public static void loadProperties() {
        try (InputStream inputStream = new FileInputStream(FileUtils.getConfigFile().toFile())) {
            properties.load(inputStream);
            user = properties.getProperty(NAME);
            if (StringUtils.isBlank(user)) {
                user = DEFAULT_USER;
            }
            updateNotification = "true".equals(properties.getProperty(UPDATE_NOTIFICATION));
            notifiedVersion = properties.getProperty(NOTIFIED_VERSION, VersionConfig.getVersion());
            admin = "true".equals(properties.getProperty(ADMIN));
            locale = Locale.of(properties.getProperty(LOCALE).substring(0, 2), properties.getProperty(LOCALE).substring(3).toUpperCase());

            alcoholPath = properties.getProperty(ALCOHOL_PATH, ALCOHOL_DEFAULT_PATH);
            alcoholDriveLetter = properties.getProperty(ALCOHOL_LETTER, ALCOHOL_DEFAULT_LETTER).toUpperCase();
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

    public static boolean isUser() {
        return StringUtils.isNotBlank(user) && !user.equals(DEFAULT_USER);
    }

    public static void setUser(String user) {
        Config.user = user;
    }

    public static void saveProperties() throws IOException {
        updateProperties();
        properties.store(new FileOutputStream(FileUtils.getConfigFile().toFile()), "Settings");
    }

    public static void updateProperties() {
        properties.put(NAME, user);
        properties.put(LOCALE, locale.toString());
        properties.put(UPDATE_NOTIFICATION, Boolean.toString(updateNotification));
        properties.put(NOTIFIED_VERSION, notifiedVersion);
        if (admin) {
            properties.put(ADMIN, admin);
        }

        properties.put(ALCOHOL_PATH, alcoholPath);
        properties.put(ALCOHOL_LETTER, alcoholDriveLetter);
    }
}
