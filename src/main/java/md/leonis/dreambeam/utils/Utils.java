package md.leonis.dreambeam.utils;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import md.leonis.dreambeam.model.DiskImage;
import md.leonis.dreambeam.model.Pair;
import md.leonis.dreambeam.model.Triple;
import md.leonis.dreambeam.model.enums.Style;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class Utils {

    public static Pair<Map<String, String>, Map<String, String>> loadShortListHashes(Path basePath) throws IOException {
        Map<String, String> hashes = new HashMap<>();
        Map<String, String> duplicates = new HashMap<>();
        FileUtils.readFromFile(basePath).forEach(line -> {
            int index = line.lastIndexOf("-");
            String hash = line.substring(index + 2).trim();
            String file = line.substring(0, index - 1).trim();
            addHash(hash, file, hashes, duplicates);
        });

        return Pair.of(hashes, duplicates);
    }

    public static void loadFiles(Path basePath, Map<String, DiskImage> images, Map<String, String> hashes, Map<String, String> duplicates) throws IOException {
        for (Path path : FileUtils.getFilesList(basePath)) {
            DiskImage diskImage = new DiskImage(FileUtils.readFromFile(path));
            String hash = diskImage.getCrc32();
            String file = path.getFileName().toString();
            addHash(hash, file, hashes, duplicates);
            images.put(hash, diskImage);
        }
    }

    public static void addHash(String hash, String file, Map<String, String> hashes, Map<String, String> duplicates) {
        if (hashes.containsKey(hash)) {
            duplicates.put(hashes.get(hash), file);
        }
        hashes.put(hash, file);
    }

    public static Map<String, String> loadTexts(Path basePath) throws IOException {
        FileUtils.createDirectories(basePath);
        Map<String, String> hashes = new HashMap<>();
        for (Path path : FileUtils.getFilesList(basePath)) {
            String hash = path.getFileName().toString();
            String text = String.join("\n", FileUtils.readFromRussianFile(path));
            hashes.put(hash, text);
        }

        return hashes;
    }

    public static List<String> cleanAndSortGameNames(Collection<String> games) {
        return games.stream().map(s -> {
            int index = s.indexOf('(');
            if (index > 0) {
                s = s.substring(0, index);
            }
            return s.trim();
        }).distinct().sorted().toList();
    }

    public static String formatSeconds(long millis) {
        return formatDuration(millis, ":");
    }

    public static String formatSecondsNoTick(long millis) {
        return formatDuration(millis, " ");
    }

    public static String formatDuration(long millis, String separator) {
        long minutes = millis / 60000;
        long seconds = (millis - minutes * 60000) / 1000;

        return String.format("%02d%s%02d", minutes, separator, seconds);
    }

    public static ListCell<String> colorSimpleLines(ListView<String> ignoredParam) {
        return new ListCell<>() {
            @Override
            protected void updateItem(String message, boolean empty) {
                super.updateItem(message, empty);

                if (empty || message == null) {
                    setText(null);

                } else {
                    getStyleClass().removeAll("green", "blue", "fuchsia", "red", "lightgray", "bold");

                    String prefix = message.isEmpty() ? "" : message.substring(0, 1);
                    Style style = Style.getByPrefix(prefix);

                    if (style != Style.DEFAULT) {
                        getStyleClass().add(style.getStyle());
                        setText(message.substring(1));
                    } else {
                        setText(message);
                    }
                }
            }
        };
    }

    public static ListCell<Triple<Style, String, Object>> colorLines2(ListView<Triple<Style, String, Object>> ignoredParam) {
        return new ListCell<>() {
            @Override
            protected void updateItem(Triple<Style, String, Object> message, boolean empty) {
                super.updateItem(message, empty);

                if (empty || message == null || message.getCenter() == null) {
                    setText(null);

                } else {
                    getStyleClass().removeAll("green", "blue", "fuchsia", "red", "lightgray", "bold");

                    if (Objects.requireNonNull(message.getLeft()) != Style.DEFAULT) {
                        getStyleClass().add(message.getLeft().getStyle());
                    }

                    setText(message.getCenter());
                }
            }
        };
    }
}
