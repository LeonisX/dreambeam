package md.leonis.dreambeam.statik;

import md.leonis.dreambeam.MainApp;
import md.leonis.dreambeam.model.Version;
import md.leonis.dreambeam.utils.StringUtils;

import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Properties;

public class VersionConfig {

    private static final String FILE_NAME = "version.properties";
    private static final String REMOTE = "https://raw.githubusercontent.com/LeonisX/dreambeam/refs/heads/main/src/main/resources/";

    private static String version;
    private static String date;
    private static String description; // todo short descr
    private static String remote;

    public static void load() {
        try (InputStream inputStream = MainApp.class.getResourceAsStream("/" + FILE_NAME)) {
            Version versionRecord = readProperties(inputStream);
            version = versionRecord.version();
            date = versionRecord.date();
            description = versionRecord.description();
        } catch (Exception ignored) {
        }
    }

    public static Version loadRemote() throws Exception {
        System.out.println(getRemoteURL());
        try (InputStream inputStream = getRemoteURL().openStream()) {
            return readProperties(inputStream);
        }
    }

    private static Version readProperties(InputStream inputStream) throws Exception {
        Properties prop = new Properties();
        prop.load(inputStream);
        System.out.println(prop);
        version = prop.getProperty("version", "@!#?@!");
        date = prop.getProperty("date", "");
        remote = prop.getProperty("remote", REMOTE);

        return new Version(version, date, null);
    }

    public static String getVersion() {
        return version;
    }

    public static String getDate() {
        return date;
    }

    public static String getAboutDate() {
        return StringUtils.isBlank(date) ? date : String.format("(%s)", date);
    }

    public static String getDescription() {
        return description;
    }

    public static URL getRemoteURL() throws Exception {
        return URI.create(remote + FILE_NAME).toURL();
    }
}
