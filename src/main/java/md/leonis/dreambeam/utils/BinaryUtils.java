package md.leonis.dreambeam.utils;

import java.util.zip.CRC32;

public class BinaryUtils {

    public static String crc32String(byte[] bytes) {
        return String.format("%08X", crc32(bytes));
    }

    public static int crc32(byte[] bytes) {
        CRC32 crc = new CRC32();
        crc.update(bytes);
        return (int) crc.getValue();
    }
}
