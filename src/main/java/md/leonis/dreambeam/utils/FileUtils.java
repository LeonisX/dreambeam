package md.leonis.dreambeam.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class FileUtils {

    public static boolean exists(Path path) {
        return Files.exists(path);
    }

    public static long getFilesCount(Path path) throws IOException {
        try (Stream<Path> files = Files.list(path)) {
            return files.count();
        }
    }

    public static List<String> readFromFile(Path path) throws IOException {
        return Files.readAllLines(path);
    }

    public static void saveToFile(Path path, List<String> lines) throws IOException {
        createDirectory(path.getParent());
        Files.write(path, lines);
    }

    public static void createDirectory(Path path) throws IOException {
        Files.createDirectory(path);
    }

    public static void deleteSilently(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
    }
}
