package md.leonis.dreambeam.model.enums;

public enum CompareStatus {

    EQUALS(""),
    CRC32("#"),
    BOTH("?"),
    ABSENT("!"),
    ERROR("!");

    CompareStatus(String marker) {
        this.marker = marker;
    }

    private final String marker;

    public String getMarker() {
        return marker;
    }
}
