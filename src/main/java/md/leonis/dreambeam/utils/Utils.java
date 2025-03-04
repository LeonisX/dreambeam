package md.leonis.dreambeam.utils;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import md.leonis.dreambeam.model.Pair;

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
            Utils.addHash(hash, file, hashes, duplicates);
        });

        return Pair.of(hashes, duplicates);
    }

    public static Pair<Map<String, String>, Map<String, String>> calculateHashes(Path basePath) throws IOException {
        Map<String, String> hashes = new HashMap<>();
        Map<String, String> duplicates = new HashMap<>();
        for (Path path : FileUtils.getFilesList(basePath)) {
            String hash = BinaryUtils.crc32String((String.join("\r\n", FileUtils.readFromFile(path)) + "\r\n").getBytes());
            String file = path.getFileName().toString();
            addHash(hash, file, hashes, duplicates);
        }

        return Pair.of(hashes, duplicates);
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

    public static String formatRecord(String title, long size, String hash) {
        return String.format("%s [%s bytes] - %s", title, size, hash);
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

    public static ListCell<String> colorLines(ListView<String> ignoredParam) {
        return new ListCell<>() {
            @Override
            protected void updateItem(String message, boolean empty) {
                super.updateItem(message, empty);

                if (empty || message == null) {
                    setText(null);

                } else {
                    char firstSymbol = message.isEmpty() ? '-' : message.charAt(0);

                    getStyleClass().clear();
                    //setStyle("-fx-font-weight: regular");

                    switch (firstSymbol) {
                        case '@' -> {
                            getStyleClass().add("green");      // @
                            setText(message.substring(1));
                        }
                        case '#' -> {
                            getStyleClass().add("blue");       // #    crc32
                            setText(message.substring(1));
                        }
                        case '?' -> {
                            getStyleClass().add("fuchsia");    // ?    both
                            setText(message.substring(1));
                        }
                        case '!' -> {
                            getStyleClass().add("red");        // !
                            setText(message.substring(1));
                        }
                        case '~' -> {
                            getStyleClass().add("lightgray");  // ~
                            setText(message.substring(1));
                        }
                        case '+' -> {
                            getStyleClass().add("bold");       // +  bold text
                            setText(message.substring(1));
                        }
                        default -> {
                            setStyle(null);
                            //setStyle("-fx-text-fill: -fx-text-base-color;");
                            setText(message);
                        }
                    }
                }
            }
        };
    }
}
