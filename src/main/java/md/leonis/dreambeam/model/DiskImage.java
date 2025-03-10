package md.leonis.dreambeam.model;

import md.leonis.dreambeam.statik.Storage;
import md.leonis.dreambeam.utils.BinaryUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DiskImage {

    private long totalSpace;                // UNUSED, Total disk space
    private long usableSpace;               // UNUSED, Total disk space
    private long unallocatedSpace;          // UNUSED, Total disk space
    private String fileSystem;              // UNUSED, File System, CDFS, NTFS, FAT32, exFAT
    private String volumeLabel;             // UNUSED, Volume Label
    private String serialNumber;            // UNUSED, Serial Number (HEX)

    private long calculatedSize;            // Calculated size
    private List<Path> files;               // Paths to every file/directory on disk
    private final boolean isDirectory;      // CD or FS directory
    private File lastDirectory;             // Last directory for DirectoryChooser.
    private final List<FileRecord> records; // File records
    private Map<String, FileRecord> searchMap; // File records (search only) title/FileRecord
    private String crc32;                   // Level-1 CRC32
    private boolean error;                  // Broken files on CD

    public DiskImage(long totalSpace, long usableSpace, long unallocatedSpace, String fileSystem, String volumeLabel, String serialNumber, List<Path> files, boolean isDirectory, File lastDirectory) {
        this.totalSpace = totalSpace;
        this.usableSpace = usableSpace;
        this.unallocatedSpace = unallocatedSpace;
        this.fileSystem = fileSystem;
        this.volumeLabel = volumeLabel;
        this.serialNumber = serialNumber;
        this.files = files;
        this.isDirectory = isDirectory;
        this.lastDirectory = lastDirectory;
        this.records = files.stream().map(FileRecord::new).collect(Collectors.toList());
        calculateSearchMap();
    }

    public DiskImage(List<String> lines) {
        this.calculatedSize = Long.parseLong(lines.remove(0).split(" ")[2]); // Total size: 962839979 bytes.
        this.records = lines.stream().map(FileRecord::parseLine).collect(Collectors.toList());
        calculateSearchMap();
        this.isDirectory = false;
        calculateCrc32();
    }

    private void calculateSearchMap() {
        searchMap = records.stream().collect(Collectors.toMap(FileRecord::title, Function.identity(), (first, second) -> first));
    }

    public List<String> getSaveLines() {
        List<String> lines = records.stream().map(FileRecord::formatRecord).collect(Collectors.toList());
        lines.add(0, String.format("Total size: %s bytes.", calculatedSize));
        return lines;
    }

    public List<FileRecord> getCompareRecords() {
        List<FileRecord> lines = new ArrayList<>(records);
        lines.add(0, new FileRecord(" size", -1, Long.toString(calculatedSize), false));
        return lines;
    }

    public String getViewFileName(Path file) {
        return isDirectory
                ? file.toString().replace(lastDirectory.getAbsolutePath(), "").substring(1).toLowerCase()
                : file.subpath(0, file.getNameCount()).toString().toLowerCase();
    }

    public double calculateDiffPoints(DiskImage newDiskImage, DiskImage diskImage) {
        int count = newDiskImage.getRecords().size();
        int sum = -10;

        for (FileRecord record : newDiskImage.getRecords()) {
            sum += diskImage.getDiffPoints(record);
        }

        if (diskImage.getCalculatedSize() == newDiskImage.getCalculatedSize()) {
            sum += 10;
        }

        return sum * 100.0 / (count * 4.0);
    }

    private int getDiffPoints(FileRecord newRecord) {
        FileRecord record = searchMap.get(newRecord.title());
        if (record == null) {
            return -1;
        } else {
            return record.getDiffPoints(newRecord);
        }
    }

    public long getTotalSpace() {
        return totalSpace;
    }

    public long getCalculatedSize() {
        return calculatedSize;
    }

    public void setCalculatedSize(long calculatedSize) {
        this.calculatedSize = calculatedSize;
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
