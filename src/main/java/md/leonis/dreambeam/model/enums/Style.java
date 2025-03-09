package md.leonis.dreambeam.model.enums;

import java.util.Arrays;
import java.util.List;

public enum Style {

    GREEN("@", "green"),            // @    ok
    BLUE("#", "blue"),              // #    crc32
    FUCHSIA("?", "fuchsia"),        // ?    both
    RED("!", "red"),                // !    error, absent
    LIGHT_GRAY("~", "lightgray"),   // ~    missing
    BOLD("+", "bold"),              // +  bold text
    DEFAULT("", "");

    private final String prefix;
    private final String style;

    Style(String prefix, String style) {
        this.prefix = prefix;
        this.style = style;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getStyle() {
        return style;
    }

    public static Style getByPrefix(String prefix) {
        return Arrays.stream(Style.values()).filter(s -> s.getPrefix().equals(prefix)).findFirst().orElse(Style.DEFAULT);
    }

    public static final List<String> prefs = Arrays.stream(Style.values()).filter(s -> !s.equals(Style.DEFAULT)).map(s -> s.prefix).toList();
}
