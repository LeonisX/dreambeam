package md.leonis.dreambeam.utils;

import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        return DurationFormatUtils.formatDuration(millis, "mm:ss", true);
    }

    public static String formatSecondsNoTick(long millis) {
        return DurationFormatUtils.formatDuration(millis, "mm ss", true);
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

                    switch (firstSymbol) {
                        case '@' -> {
                            setStyle("-fx-text-fill: green;");       // @
                            setText(message.substring(1));
                        }
                        case '#' -> {
                            setStyle("-fx-text-fill: blue;");
                            setText(message.substring(1));  // #    crc32
                        }
                        case '?' -> {
                            setStyle("-fx-text-fill: fuchsia;");    // ?    both
                            setText(message.substring(1));
                        }
                        case '!' -> {
                            setStyle("-fx-text-fill: red;");        // !
                            setText(message.substring(1));
                        }
                        case '~' -> {
                            setStyle("-fx-text-fill: lightgray;");  // ~
                            setText(message.substring(1));
                        }
                        default -> {
                            setStyle("-fx-text-fill: -fx-text-base-color;");
                            setText(message);
                        }
                    }
                }
            }
        };
    }
}
