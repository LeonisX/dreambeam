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

    public static int getFilesCount(Path path) throws IOException {
        return getFilesList(path).size();
    }

    @SuppressWarnings("all")
    public static List<Path> getFilesList(Path path) throws IOException {
        try (Stream<Path> files = Files.list(path)) {
            return files.toList();
        }
    }

    public static List<String> readFromFile(Path path) throws IOException {
        return Files.readAllLines(path);
    }

    public static void writeToFile(Path path, List<String> lines) throws IOException {
        createDirectories(path.getParent());
        Files.write(path, lines);
    }

    public static void createDirectories(Path path) throws IOException {
        Files.createDirectories(path);
    }

    public static void deleteSilently(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
    }
}
