package md.leonis.dreambeam.utils;

// Копия кода из org.apache.commons.lang3.StringUtils
// Нужна для уменьшения размера исполняемого файла.
public class StringUtils {

    public static String formatHex(int value) {
        return String.format("%08X", value);
    }

    public static String formatSerialNumber(int value) {
        String sn = formatHex(value);
        return String.format("%s-%s", sn.substring(0, 4), sn.substring(4));
    }

    public static boolean isBlank(final CharSequence cs) {
        final int strLen = length(cs);
        if (strLen == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    public static int length(final CharSequence cs) {
        return cs == null ? 0 : cs.length();
    }
}
