package md.leonis.dreambeam.model;

public record Game(String fullTitle, String title, int size, String hash, boolean isError) {

    public static Game parseLine(String fullTitle) {
        //movies\movie.res [696724 bytes] - 98F6B2D7
        //movies\tennis.sfd [53248000 bytes] - Error!!!
        int index1 = fullTitle.lastIndexOf('[');
        int index2 = fullTitle.lastIndexOf("bytes]");
        int index3 = fullTitle.lastIndexOf('-');
        String title = fullTitle.substring(0, index1 - 1).trim();
        int size = Integer.parseInt(fullTitle.substring(index1 + 1, index2 - 1));
        String hash = fullTitle.substring(index3 + 2);

        return new Game(fullTitle, title, size, hash, hash.contains("!!!"));
    }
}
