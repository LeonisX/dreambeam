package md.leonis.dreambeam.utils;

import md.leonis.dreambeam.statik.Config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FileUtils {

    public static final String resourcePath = "/fxml/";

    public static Path getUserLogFile() {
        return getRootDir().resolve(Config.user + ".log");
    }
    public static Path getUserFile(String fileName) {
        return getUserDir().resolve(fileName);
    }

    public static Path getUserDir() {
        return getRootDir().resolve(Config.user);
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

    public static List<Path> listFiles(File folder) {
        return listFiles(folder, new ArrayList<>());
    }

    public static List<Path> listFiles(File folder, List<Path> paths) {
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    listFiles(file, paths);
                }
            }
            for (File file : files) {  // костыль конечно, но так считал код на Delphi :(
                if (file.isFile()) {
                    paths.add(file.toPath());
                }
            }
        }
        return paths;
    }

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

    public static List<String> readFromRussianFile(Path path) throws IOException {
        //todo сконвертировать текста в юникод.
        return Files.readAllLines(path, Charset.forName("windows-1251"));
    }

    public static void writeToFile(Path path, List<String> lines) throws IOException {
        createDirectories(path.getParent());
        Files.write(path, lines);
    }

    public static void writeToRussianFile(Path path, String text) throws IOException {
        //todo сконвертировать текста в юникод.
        createDirectories(path.getParent());
        Files.writeString(path, text, Charset.forName("windows-1251"));
    }

    public static void renameFileSilently(Path source, Path target) {
        try {
            renameFile(source, target);
        } catch (IOException ignored) {
        }
    }

    public static void renameFile(Path source, Path target) throws IOException {
        Files.move(source, target);
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
