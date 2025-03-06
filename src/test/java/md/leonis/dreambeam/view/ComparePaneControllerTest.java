package md.leonis.dreambeam.view;

import md.leonis.dreambeam.model.FileRecord;
import md.leonis.dreambeam.model.Pair;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static md.leonis.dreambeam.view.ComparePaneController.withNullsList;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ComparePaneControllerTest {

    @Test
    void testWithNullsList() {
        Map<String, FileRecord> games1 = Stream.of(
                FileRecord.parseLine("file1 [0 bytes] - 00000000"),
                FileRecord.parseLine("file2 [0 bytes] - 00000000")
        ).collect(Collectors.toMap(FileRecord::title, Function.identity()));

        // empty
        assertEquals("""
                file1 [0 bytes] - 00000000 == file1 [0 bytes] - 00000000
                file2 [0 bytes] - 00000000 == file2 [0 bytes] - 00000000""", formatResult(withNullsList(games1, games1)));

        assertEquals("""
                file1 [0 bytes] - 00000000 == null
                file2 [0 bytes] - 00000000 == null""", formatResult(withNullsList(games1, new HashMap<>())));

        assertEquals("""
                null == file1 [0 bytes] - 00000000
                null == file2 [0 bytes] - 00000000""", formatResult(withNullsList(Map.of(), games1)));
    }

    private String formatResult(List<Pair<FileRecord, FileRecord>> games) {
        return String.join("\n", games.stream().map(g -> notNull(g.getLeft()) + " == " + notNull(g.getRight())).toList());
    }

    private String notNull(FileRecord record) {
        return record == null ? "null" : record.fullTitle();
    }

    @Test
    void withNullsList2() {
        Map<String, FileRecord> games1 = Stream.of(
                FileRecord.parseLine("file1 [0 bytes] - 00000000"),
                FileRecord.parseLine("file2 [0 bytes] - 00000000"),
                FileRecord.parseLine("file3 [0 bytes] - 00000000")
        ).collect(Collectors.toMap(FileRecord::title, Function.identity()));

        Map<String, FileRecord> games2abs1 = Stream.of(
                FileRecord.parseLine("file2 [0 bytes] - 00000000"),
                FileRecord.parseLine("file3 [0 bytes] - 00000000")
        ).collect(Collectors.toMap(FileRecord::title, Function.identity()));

        compare(games1, games2abs1, """
                file1 [0 bytes] - 00000000 == null
                file2 [0 bytes] - 00000000 == file2 [0 bytes] - 00000000
                file3 [0 bytes] - 00000000 == file3 [0 bytes] - 00000000""");

        Map<String, FileRecord> games2abs2 = Stream.of(
                FileRecord.parseLine("file1 [0 bytes] - 00000000"),
                FileRecord.parseLine("file3 [0 bytes] - 00000000")
        ).collect(Collectors.toMap(FileRecord::title, Function.identity()));

        compare(games1, games2abs2, """
                file1 [0 bytes] - 00000000 == file1 [0 bytes] - 00000000
                file2 [0 bytes] - 00000000 == null
                file3 [0 bytes] - 00000000 == file3 [0 bytes] - 00000000""");

        Map<String, FileRecord> games2abs3 = Stream.of(
                FileRecord.parseLine("file1 [0 bytes] - 00000000"),
                FileRecord.parseLine("file2 [0 bytes] - 00000000")
        ).collect(Collectors.toMap(FileRecord::title, Function.identity()));

        compare(games1, games2abs3, """
                file1 [0 bytes] - 00000000 == file1 [0 bytes] - 00000000
                file2 [0 bytes] - 00000000 == file2 [0 bytes] - 00000000
                file3 [0 bytes] - 00000000 == null""");
    }

    @Test
    void withNullsList3() {
        Map<String, FileRecord> games1 = Stream.of(
                FileRecord.parseLine("file1 [0 bytes] - 00000000"),
                FileRecord.parseLine("file2 [0 bytes] - 00000000"),
                FileRecord.parseLine("file3 [0 bytes] - 00000000")
        ).collect(Collectors.toMap(FileRecord::title, Function.identity()));

        Map<String, FileRecord> games2 = Stream.of(
                FileRecord.parseLine("file1 [0 bytes] - 10000000"),
                FileRecord.parseLine("file4 [0 bytes] - 00000000")
        ).collect(Collectors.toMap(FileRecord::title, Function.identity()));

        compare(games1, games2, """
                file1 [0 bytes] - 00000000 == file1 [0 bytes] - 10000000
                file2 [0 bytes] - 00000000 == null
                file3 [0 bytes] - 00000000 == null
                null == file4 [0 bytes] - 00000000""");

        games2 = Stream.of(
                FileRecord.parseLine("file2 [0 bytes] - 20000000"),
                FileRecord.parseLine("file4 [0 bytes] - 00000000")
        ).collect(Collectors.toMap(FileRecord::title, Function.identity()));

        compare(games1, games2, """
                file1 [0 bytes] - 00000000 == null
                file2 [0 bytes] - 00000000 == file2 [0 bytes] - 20000000
                file3 [0 bytes] - 00000000 == null
                null == file4 [0 bytes] - 00000000""");

        games2 = Stream.of(
                FileRecord.parseLine("file0 [0 bytes] - 00000000"),
                FileRecord.parseLine("file1 [0 bytes] - 10000000")
        ).collect(Collectors.toMap(FileRecord::title, Function.identity()));

        compare(games1, games2, """
                null == file0 [0 bytes] - 00000000
                file1 [0 bytes] - 00000000 == file1 [0 bytes] - 10000000
                file2 [0 bytes] - 00000000 == null
                file3 [0 bytes] - 00000000 == null""");

        games2 = Stream.of(
                FileRecord.parseLine("file0 [0 bytes] - 00000000"),
                FileRecord.parseLine("file2 [0 bytes] - 20000000")
        ).collect(Collectors.toMap(FileRecord::title, Function.identity()));

        compare(games1, games2, """
                null == file0 [0 bytes] - 00000000
                file1 [0 bytes] - 00000000 == null
                file2 [0 bytes] - 00000000 == file2 [0 bytes] - 20000000
                file3 [0 bytes] - 00000000 == null""");
    }

    private void compare(Map<String, FileRecord> games1, Map<String, FileRecord> games2, String result) {
        var result1 = formatResult(withNullsList(games1, games2));
        var result2 = formatResult(withNullsList(games2, games1));
        assertEquals(result, result1);
        String reversed = Arrays.stream(result.split("\n")).map(s -> {
            String left = s.split("==")[0].trim();
            String right = s.split("==")[1].trim();
            return String.format("%s == %s", right, left);
        }).collect(Collectors.joining("\n"));
        assertEquals(reversed, result2);
    }
}
