package md.leonis.dreambeam.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class Config {
    /*static String apiPath;
    public static String sitePath;
    static String sampleVideo;*/

    public static Properties languageTable;

    static final String resourcePath = "/fxml/";

    public static List<Path> files;
    public static List<String> saveFiles;
    public static String crc32;
    public static Map<String, String> hashes;

    public static void loadProperties() throws IOException {
        try (InputStream inputStream = Config.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (inputStream == null) throw new FileNotFoundException("Property file isn't found...");
            Properties prop = new Properties();
            prop.load(inputStream);
            /*apiPath = prop.getProperty("api.path");
            sitePath = prop.getProperty("site.path");
            sampleVideo = prop.getProperty("sample.video");*/
        }
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
