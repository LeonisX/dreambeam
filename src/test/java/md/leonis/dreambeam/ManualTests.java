package md.leonis.dreambeam;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.List;
import java.util.stream.Stream;

public class ManualTests {

    @Test
    void setDates() throws IOException {

        String path = "L:\\_dc_need-to-process\\parovoz\\Dreamcast Scene - PAROVOZ\\";

        List<Path> files;
        try (Stream<Path> stream = Files.walk(Paths.get(path))) {
            files = stream.toList();
        }

        List<Path> dirs;
        try (Stream<Path> stream = Files.walk(Paths.get(path))) {
            dirs = stream.filter(Files::isDirectory).toList();
        }

        for (Path f : files) {
            setAttributes(f, Files.readAttributes(f, BasicFileAttributes.class).lastModifiedTime());
        }

        for (int i = dirs.size() - 1; i >= 0; i--) {
            Path f = dirs.get(i);
            setAttributes(f, Files.readAttributes(f, BasicFileAttributes.class).lastModifiedTime());

            System.out.println(f + ": " + Files.isDirectory(f));
            BasicFileAttributes attr = Files.readAttributes(f, BasicFileAttributes.class);

            var ct = attr.creationTime();
            var at = attr.lastAccessTime();
            var mt = attr.lastModifiedTime();

            System.out.println("creationTime: " + ct);
            System.out.println("lastAccessTime: " + at);
            System.out.println("lastModifiedTime: " + mt);
        }
    }

    private void setAttributes(Path path, FileTime fileTime) {
        try {
            Files.setAttribute(path, "creationTime", fileTime);
            Files.setAttribute(path, "lastAccessTime", fileTime);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
