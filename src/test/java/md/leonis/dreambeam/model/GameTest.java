package md.leonis.dreambeam.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    void parseLine() {
        assertEquals("Game[fullTitle=movies\\movie.res [696724 bytes] - 98F6B2D7, title=movies\\movie.res, size=696724, hash=98F6B2D7, isError=false]",
                Game.parseLine("movies\\movie.res [696724 bytes] - 98F6B2D7").toString());
        assertEquals("Game[fullTitle=movies\\tennis.sfd [53248000 bytes] - Error!!!, title=movies\\tennis.sfd, size=53248000, hash=Error!!!, isError=true]",
                Game.parseLine("movies\\tennis.sfd [53248000 bytes] - Error!!!").toString());
        assertEquals("Game[fullTitle=movie.res [0 bytes] - 00000000, title=movie.res, size=0, hash=00000000, isError=false]",
                Game.parseLine("movie.res [0 bytes] - 00000000").toString());
    }
}
