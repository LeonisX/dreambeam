package md.leonis.dreambeam.view;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatsPaneControllerTest {

    //todo понять почему падают
    /*@Test
    void mapGamesListFull() {
        List<String> games1 = List.of("Game1", "Game2");

        // equals
        assertEquals("", String.join("\n", StatsPaneController.mapGamesListFull(games1, games1, true)));
        assertEquals("""
                Game1
                Game2""", String.join("\n", StatsPaneController.mapGamesListFull(games1, games1, false)));

        // empty2
        assertEquals("""
                !Game1
                !Game2""", String.join("\n", StatsPaneController.mapGamesListFull(games1, List.of(), true)));
        assertEquals("""
                !Game1
                !Game2""", String.join("\n", StatsPaneController.mapGamesListFull(games1, List.of(), false)));

        // empty1
        assertEquals("""
                ~Game1
                ~Game2""", String.join("\n", StatsPaneController.mapGamesListFull(List.of(), games1, true)));
        assertEquals("""
                ~Game1
                ~Game2""", String.join("\n", StatsPaneController.mapGamesListFull(List.of(), games1, false)));
    }

    //todo тут были нуллы но сейчас их нет:(
    @Test
    void mapGamesListFull2() {
        List<String> games1 = List.of("Game1", "Game2", "Game3");
        List<String> games2abs1 = List.of("Game2", "Game3");
        List<String> games2abs2 = List.of("Game1", "Game3");
        List<String> games2abs3 = List.of("Game1", "Game2");

        // missing 1
        assertEquals("""
                !Game1""", String.join("\n", StatsPaneController.mapGamesListFull(games1, games2abs1, true)));
        assertEquals("""
                !Game1
                Game2
                Game3""", String.join("\n", StatsPaneController.mapGamesListFull(games1, games2abs1, false)));

        // missing 2
        assertEquals("""
                !Game2""", String.join("\n", StatsPaneController.mapGamesListFull(games1, games2abs2, true)));
        assertEquals("""
                Game1
                !Game2
                Game3""", String.join("\n", StatsPaneController.mapGamesListFull(games1, games2abs2, false)));

        // missing3
        assertEquals("""
                !Game3""", String.join("\n", StatsPaneController.mapGamesListFull(games1, games2abs3, true)));
        assertEquals("""
                Game1
                Game2
                !Game3""", String.join("\n", StatsPaneController.mapGamesListFull(games1, games2abs3, false)));

        //reverse

        // missing 1
        assertEquals("""
                ~Game1""", String.join("\n", StatsPaneController.mapGamesListFull(games2abs1, games1, true)));
        assertEquals("""
                ~Game1
                Game2
                Game3""", String.join("\n", StatsPaneController.mapGamesListFull(games2abs1, games1, false)));

        // missing 2
        assertEquals("""
                ~Game2""", String.join("\n", StatsPaneController.mapGamesListFull(games2abs2, games1, true)));
        assertEquals("""
                Game1
                ~Game2
                Game3""", String.join("\n", StatsPaneController.mapGamesListFull(games2abs2, games1, false)));

        // missing3
        assertEquals("""
                ~Game3""", String.join("\n", StatsPaneController.mapGamesListFull(games2abs3, games1, true)));
        assertEquals("""
                Game1
                Game2
                ~Game3""", String.join("\n", StatsPaneController.mapGamesListFull(games2abs3, games1, false)));
    }

    @Test
    void withNullsList() {
        List<String> games1 = List.of("Game1", "Game2");

        // empty
        assertEquals("""
                Game1
                Game2""", String.join("\n", StatsPaneController.withNullsList(games1, games1)));

        assertEquals("""
                Game1
                Game2""", String.join("\n",
                StatsPaneController.withNullsList(games1, List.of())));

        assertEquals("""
                null
                null""", String.join("\n",
                StatsPaneController.withNullsList(List.of(), games1)));
    }

    @Test
    void withNullsList2() {
        List<String> games1 = List.of("Game1", "Game2", "Game3");
        List<String> games2abs1 = List.of("Game2", "Game3");
        List<String> games2abs2 = List.of("Game1", "Game3");
        List<String> games2abs3 = List.of("Game1", "Game2");

        // missing 1
        assertEquals("""
                Game1
                Game2
                Game3""", String.join("\n", StatsPaneController.withNullsList(games1, games2abs1)));

        // missing 2
        assertEquals("""
                Game1
                Game2
                Game3""", String.join("\n", StatsPaneController.withNullsList(games1, games2abs2)));

        // missing3
        assertEquals("""
                Game1
                Game2
                Game3""", String.join("\n", StatsPaneController.withNullsList(games1, games2abs3)));

        //reverse

        // missing 1
        assertEquals("""
                null
                Game2
                Game3""", String.join("\n", StatsPaneController.withNullsList(games2abs1, games1)));

        // missing 2
        assertEquals("""
                Game1
                null
                Game3""", String.join("\n", StatsPaneController.withNullsList(games2abs2, games1)));

        // missing3
        assertEquals("""
                Game1
                Game2
                null""", String.join("\n", StatsPaneController.withNullsList(games2abs3, games1)));
    }*/
}
