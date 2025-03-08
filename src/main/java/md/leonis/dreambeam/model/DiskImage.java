package md.leonis.dreambeam.model;

import md.leonis.dreambeam.utils.BinaryUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class DiskImage {

    private long size;                      // Calculated size
    //todo label, serial, actual size, something else
    private final List<Path> files;
    private final boolean isDirectory;      // CD or FS directory
    private final File lastDirectory;       // Last directory for DirectoryChooser.
    private final List<FileRecord> records;
    private String crc32;
    private boolean error;                  // Broken files on CD

    public DiskImage(List<Path> files, boolean isDirectory, File lastDirectory) {
        this.files = files;
        this.isDirectory = isDirectory;
        this.lastDirectory = lastDirectory;
        this.records = files.stream().map(FileRecord::new).collect(Collectors.toList());
    }

    public List<String> getSaveLines() {
        List<String> lines = records.stream().map(FileRecord::formatRecord).collect(Collectors.toList());
        lines.add(0, String.format("Total size: %s bytes.", size));
        return lines;
    }

    public String getViewFileName(Path file) {
        return isDirectory
                ? file.toString().replace(lastDirectory.getAbsolutePath(), "").substring(1).toLowerCase()
                : file.subpath(0, file.getNameCount()).toString().toLowerCase();
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void calculateCrc32() {
        crc32 = BinaryUtils.crc32String((String.join("\r\n", getSaveLines()) + "\r\n").getBytes()); // костыль конечно, но так работал код на Delphi :(
    }

    public String getCrc32() {
        return crc32;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<FileRecord> getRecords() {
        return records;
    }

    public List<Path> getFiles() {
        return files;
    }
}
