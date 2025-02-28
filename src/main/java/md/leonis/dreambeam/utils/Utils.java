package md.leonis.dreambeam.utils;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.commons.lang3.tuple.Pair;

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
        return DurationFormatUtils.formatDuration(millis, "mm:ss", true);
    }

    public static String formatSecondsNoTick(long millis) {
        return DurationFormatUtils.formatDuration(millis, "mm ss", true);
    }
}
