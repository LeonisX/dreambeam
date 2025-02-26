package md.leonis.dreambeam.utils;

import org.apache.commons.lang3.time.DurationFormatUtils;

public class Utils {

    public static String formatSeconds(long millis) {
        return DurationFormatUtils.formatDuration(millis, "mm:ss", true);
    }
}
