package md.leonis.dreambeam.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileUtils {

    public static boolean exists(Path path) {
        return Files.exists(path);
    }

    public static void saveToFile(Path path, List<String> lines) throws IOException {
        createDirectory(path.getParent());
        Files.write(path, lines);
    }

    public static void createDirectory(Path path) throws IOException {
        Files.createDirectory(path);
    }
}
