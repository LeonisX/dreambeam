package md.leonis.dreambeam.view;

import md.leonis.dreambeam.model.Game;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ComparePaneControllerTest {

    @Test
    void mapGamesListFull() {
        Map<String, Game> games1 = Stream.of(
                Game.parseLine("file1 [0 bytes] - 00000000"),
                Game.parseLine("file2 [0 bytes] - 00000000")
        ).collect(Collectors.toMap(Game::title, Function.identity(), (v1, v2) -> v1, LinkedHashMap::new));

        // equals
        assertEquals("""
                null
                null""", String.join("\n", ComparePaneController.mapGamesListFull(games1, games1, true)));
        assertEquals("""
                file1 [0 bytes] - 00000000
                file2 [0 bytes] - 00000000""", String.join("\n", ComparePaneController.mapGamesListFull(games1, games1, false)));

        // empty2
        assertEquals("""
                !file1 [0 bytes] - 00000000
                !file2 [0 bytes] - 00000000""", String.join("\n", ComparePaneController.mapGamesListFull(games1, Map.of(), true)));
        assertEquals("""
                !file1 [0 bytes] - 00000000
                !file2 [0 bytes] - 00000000""", String.join("\n", ComparePaneController.mapGamesListFull(games1, Map.of(), false)));

        // empty1
        assertEquals("""
                ~file1 [0 bytes] - 00000000
                ~file2 [0 bytes] - 00000000""", String.join("\n", ComparePaneController.mapGamesListFull(Map.of(), games1, true)));
        assertEquals("""
                ~file1 [0 bytes] - 00000000
                ~file2 [0 bytes] - 00000000""", String.join("\n", ComparePaneController.mapGamesListFull(Map.of(), games1, false)));
    }

    @Test
    void mapGamesListFull2() {
        Map<String, Game> games1 = Stream.of(
                Game.parseLine("file1 [0 bytes] - 00000000"),
                Game.parseLine("file2 [0 bytes] - 00000000"),
                Game.parseLine("file3 [0 bytes] - 00000000")
        ).collect(Collectors.toMap(Game::title, Function.identity(), (v1, v2) -> v1, LinkedHashMap::new));

        Map<String, Game> games2abs1 = Stream.of(
                Game.parseLine("file2 [0 bytes] - 00000000"),
                Game.parseLine("file3 [0 bytes] - 00000000")
        ).collect(Collectors.toMap(Game::title, Function.identity(), (v1, v2) -> v1, LinkedHashMap::new));

        Map<String, Game> games2abs2 = Stream.of(
                Game.parseLine("file1 [0 bytes] - 00000000"),
                Game.parseLine("file3 [0 bytes] - 00000000")
        ).collect(Collectors.toMap(Game::title, Function.identity(), (v1, v2) -> v1, LinkedHashMap::new));

        Map<String, Game> games2abs3 = Stream.of(
                Game.parseLine("file1 [0 bytes] - 00000000"),
                Game.parseLine("file2 [0 bytes] - 00000000")
        ).collect(Collectors.toMap(Game::title, Function.identity(), (v1, v2) -> v1, LinkedHashMap::new));

        // missing 1
        assertEquals("""
                !file1 [0 bytes] - 00000000
                null
                null""", String.join("\n", ComparePaneController.mapGamesListFull(games1, games2abs1, true)));
        assertEquals("""
                !file1 [0 bytes] - 00000000
                file2 [0 bytes] - 00000000
                file3 [0 bytes] - 00000000""", String.join("\n", ComparePaneController.mapGamesListFull(games1, games2abs1, false)));

        // missing 2
        assertEquals("""
                null
                !file2 [0 bytes] - 00000000
                null""", String.join("\n", ComparePaneController.mapGamesListFull(games1, games2abs2, true)));
        assertEquals("""
                file1 [0 bytes] - 00000000
                !file2 [0 bytes] - 00000000
                file3 [0 bytes] - 00000000""", String.join("\n", ComparePaneController.mapGamesListFull(games1, games2abs2, false)));

        // missing3
        assertEquals("""
                null
                null
                !file3 [0 bytes] - 00000000""", String.join("\n", ComparePaneController.mapGamesListFull(games1, games2abs3, true)));
        assertEquals("""
                file1 [0 bytes] - 00000000
                file2 [0 bytes] - 00000000
                !file3 [0 bytes] - 00000000""", String.join("\n", ComparePaneController.mapGamesListFull(games1, games2abs3, false)));

        //reverse

        // missing 1
        assertEquals("""
                ~file1 [0 bytes] - 00000000
                null
                null""", String.join("\n", ComparePaneController.mapGamesListFull(games2abs1, games1, true)));
        assertEquals("""
                ~file1 [0 bytes] - 00000000
                file2 [0 bytes] - 00000000
                file3 [0 bytes] - 00000000""", String.join("\n", ComparePaneController.mapGamesListFull(games2abs1, games1, false)));

        // missing 2
        assertEquals("""
                null
                ~file2 [0 bytes] - 00000000
                null""", String.join("\n", ComparePaneController.mapGamesListFull(games2abs2, games1, true)));
        assertEquals("""
                file1 [0 bytes] - 00000000
                ~file2 [0 bytes] - 00000000
                file3 [0 bytes] - 00000000""", String.join("\n", ComparePaneController.mapGamesListFull(games2abs2, games1, false)));

        // missing3
        assertEquals("""
                null
                null
                ~file3 [0 bytes] - 00000000""", String.join("\n", ComparePaneController.mapGamesListFull(games2abs3, games1, true)));
        assertEquals("""
                file1 [0 bytes] - 00000000
                file2 [0 bytes] - 00000000
                ~file3 [0 bytes] - 00000000""", String.join("\n", ComparePaneController.mapGamesListFull(games2abs3, games1, false)));
    }

    @Test
    void mapGamesListFull3() {
        Map<String, Game> games1 = Stream.of(
                Game.parseLine("file1 [0 bytes] - 00000000"),
                Game.parseLine("file2 [0 bytes] - 00000000"),
                Game.parseLine("file3 [0 bytes] - 00000000")
        ).collect(Collectors.toMap(Game::title, Function.identity(), (v1, v2) -> v1, LinkedHashMap::new));

        Map<String, Game> games2 = Stream.of(
                Game.parseLine("file1 [0 bytes] - 00000001"),
                Game.parseLine("file2 [1 bytes] - 00000000"),
                Game.parseLine("file3 [1 bytes] - 00000001")
        ).collect(Collectors.toMap(Game::title, Function.identity(), (v1, v2) -> v1, LinkedHashMap::new));

        //diff
        assertEquals("""
                #file1 [0 bytes] - 00000000
                #file2 [0 bytes] - 00000000
                ?file3 [0 bytes] - 00000000""", String.join("\n", ComparePaneController.mapGamesListFull(games1, games2, true)));
        assertEquals("""
                #file1 [0 bytes] - 00000000
                #file2 [0 bytes] - 00000000
                ?file3 [0 bytes] - 00000000""", String.join("\n", ComparePaneController.mapGamesListFull(games1, games2, false)));

        //reverse
        assertEquals("""
                #file1 [0 bytes] - 00000001
                #file2 [1 bytes] - 00000000
                ?file3 [1 bytes] - 00000001""", String.join("\n", ComparePaneController.mapGamesListFull(games2, games1, true)));
        assertEquals("""
                #file1 [0 bytes] - 00000001
                #file2 [1 bytes] - 00000000
                ?file3 [1 bytes] - 00000001""", String.join("\n", ComparePaneController.mapGamesListFull(games2, games1, false)));
    }

    @Test
    void withNullsList() {
        Map<String, Game> games1 = Stream.of(
                Game.parseLine("file1 [0 bytes] - 00000000"),
                Game.parseLine("file2 [0 bytes] - 00000000")
        ).collect(Collectors.toMap(Game::title, Function.identity(), (v1, v2) -> v1, LinkedHashMap::new));

        // empty
        assertEquals("""
                file1 [0 bytes] - 00000000
                file2 [0 bytes] - 00000000""", String.join("\n",
                ComparePaneController.withNullsList(games1, new ArrayList<>(games1.values())).stream().map(g -> g == null ? "null" : g.fullTitle()).toList()));

        assertEquals("""
                file1 [0 bytes] - 00000000
                file2 [0 bytes] - 00000000""", String.join("\n",
                ComparePaneController.withNullsList(games1, new ArrayList<>()).stream().map(g -> g == null ? "null" : g.fullTitle()).toList()));

        assertEquals("""
                null
                null""", String.join("\n",
                ComparePaneController.withNullsList(Map.of(), new ArrayList<>(games1.values())).stream().map(g -> g == null ? "null" : g.fullTitle()).toList()));
    }

    @Test
    void withNullsList2() {
        Map<String, Game> games1 = Stream.of(
                Game.parseLine("file1 [0 bytes] - 00000000"),
                Game.parseLine("file2 [0 bytes] - 00000000"),
                Game.parseLine("file3 [0 bytes] - 00000000")
        ).collect(Collectors.toMap(Game::title, Function.identity(), (v1, v2) -> v1, LinkedHashMap::new));

        Map<String, Game> games2abs1 = Stream.of(
                Game.parseLine("file2 [0 bytes] - 00000000"),
                Game.parseLine("file3 [0 bytes] - 00000000")
        ).collect(Collectors.toMap(Game::title, Function.identity(), (v1, v2) -> v1, LinkedHashMap::new));

        Map<String, Game> games2abs2 = Stream.of(
                Game.parseLine("file1 [0 bytes] - 00000000"),
                Game.parseLine("file3 [0 bytes] - 00000000")
        ).collect(Collectors.toMap(Game::title, Function.identity(), (v1, v2) -> v1, LinkedHashMap::new));

        Map<String, Game> games2abs3 = Stream.of(
                Game.parseLine("file1 [0 bytes] - 00000000"),
                Game.parseLine("file2 [0 bytes] - 00000000")
        ).collect(Collectors.toMap(Game::title, Function.identity(), (v1, v2) -> v1, LinkedHashMap::new));

        // missing 1
        assertEquals("""
                file1 [0 bytes] - 00000000
                file2 [0 bytes] - 00000000
                file3 [0 bytes] - 00000000""", String.join("\n",
                ComparePaneController.withNullsList(games1, new ArrayList<>(games2abs1.values())).stream().map(g -> g == null ? "null" : g.fullTitle()).toList()));

        // missing 2
        assertEquals("""
                file1 [0 bytes] - 00000000
                file2 [0 bytes] - 00000000
                file3 [0 bytes] - 00000000""", String.join("\n",
                ComparePaneController.withNullsList(games1, new ArrayList<>(games2abs2.values())).stream().map(g -> g == null ? "null" : g.fullTitle()).toList()));

        // missing3
        assertEquals("""
                file1 [0 bytes] - 00000000
                file2 [0 bytes] - 00000000
                file3 [0 bytes] - 00000000""", String.join("\n",
                ComparePaneController.withNullsList(games1, new ArrayList<>(games2abs3.values())).stream().map(g -> g == null ? "null" : g.fullTitle()).toList()));

        //reverse

        // missing 1
        assertEquals("""
                null
                file2 [0 bytes] - 00000000
                file3 [0 bytes] - 00000000""", String.join("\n",
                ComparePaneController.withNullsList(games2abs1, new ArrayList<>(games1.values())).stream().map(g -> g == null ? "null" : g.fullTitle()).toList()));

        // missing 2
        assertEquals("""
                file1 [0 bytes] - 00000000
                null
                file3 [0 bytes] - 00000000""", String.join("\n",
                ComparePaneController.withNullsList(games2abs2, new ArrayList<>(games1.values())).stream().map(g -> g == null ? "null" : g.fullTitle()).toList()));

        // missing3
        assertEquals("""
                file1 [0 bytes] - 00000000
                file2 [0 bytes] - 00000000
                null""", String.join("\n",
                ComparePaneController.withNullsList(games2abs3, new ArrayList<>(games1.values())).stream().map(g -> g == null ? "null" : g.fullTitle()).toList()));

    }
}
