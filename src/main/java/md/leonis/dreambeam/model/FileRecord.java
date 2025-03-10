package md.leonis.dreambeam.model;

import md.leonis.dreambeam.utils.StringUtils;

import java.nio.file.Path;

public record FileRecord(String title, long size, String hash, boolean isError) {

    public static final String ERROR = "Error!!!";

    public FileRecord(Path file) {
        this(file.toString(), 0, null, false);
    }

    public String formatRecord() {
        if (size < 0) {
            return String.format("%s: %s", title, hash);
        } else if (StringUtils.isBlank(hash)) {
            return title;
        } else {
            return String.format("%s [%s bytes] - %s", title, size, isError ? ERROR : hash);
        }
    }

    public int getDiffPoints(FileRecord newRecord) {
        if (size == newRecord.size() && hash.equals(newRecord.hash())) {
            return 4;
        } else if (size == newRecord.size() || hash.equals(newRecord.hash())) {
            return 2;
        } else {
            return 1;
        }
    }

    public static FileRecord parseLine(String fullTitle) {
        try {
            //movies\movie.res [696724 bytes] - 98F6B2D7
            //movies\tennis.sfd [53248000 bytes] - Error!!!
            int index1 = fullTitle.lastIndexOf('[');
            int index2 = fullTitle.lastIndexOf("bytes]");
            int index3 = fullTitle.lastIndexOf('-');
            String title = fullTitle.substring(0, index1 - 1).trim();
            int size = Integer.parseInt(fullTitle.substring(index1 + 1, index2 - 1));
            String hash = fullTitle.substring(index3 + 2);
            boolean isError = hash.contains("!!!");
            //hash = isError ? "" : hash;

            return new FileRecord(title, size, hash, isError);
        } catch (Exception e) {
            System.out.println(fullTitle);
            return new FileRecord(fullTitle, -1, ERROR, true);
        }
    }

    public String fullTitle() {
        return formatRecord();
    }

    @Override
    public String toString() {
        return formatRecord();
    }
}
