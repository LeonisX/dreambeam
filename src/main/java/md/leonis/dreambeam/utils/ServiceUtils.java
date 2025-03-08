package md.leonis.dreambeam.utils;

import javafx.scene.control.Alert;
import md.leonis.dreambeam.statik.Storage;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static md.leonis.dreambeam.statik.Config.str;
import static md.leonis.dreambeam.statik.Config.strError;

public class ServiceUtils {

    public static void calculateBaseHashes(boolean reportDuplicates) {
        try {
            var pair = Utils.calculateHashes(FileUtils.getBaseGamesDir());
            Storage.baseHashes = pair.getLeft();
            Storage.baseDuplicates = pair.getRight();
            if (reportDuplicates) {
                reportBaseDuplicates();
            }
        } catch (IOException e) {
            JavaFxUtils.showAlert(strError(), str("service.base.files.read.error"), e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public static void reportBaseDuplicates() {
        if (Storage.baseDuplicates != null && !Storage.baseDuplicates.isEmpty()) {
            JavaFxUtils.showAlert(strError(), str("service.base.duplicates.error"),
                    Storage.baseDuplicates.entrySet().stream().map(e -> e.getKey() + " == " + e.getValue()).collect(Collectors.joining("\n")), Alert.AlertType.WARNING);
        }
    }

    public static void calculateUserHashes(boolean reportDuplicates, boolean force) {
        if (Storage.userHashesLoaded && !force) {
            return;
        }
        try {
            Storage.userHashesLoaded = false;
            var pair = Utils.calculateHashes(FileUtils.getUserDir());
            Storage.userHashes = pair.getLeft();
            Storage.userDuplicates = pair.getRight();
            if (reportDuplicates) {
                reportUserDuplicates();
            }
            Storage.userHashesLoaded = true;
        } catch (IOException e) {
            JavaFxUtils.showAlert(strError(), str("service.user.files.read.error"), e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public static void reportUserDuplicates() {
        if (!Storage.userDuplicates.isEmpty()) {
            JavaFxUtils.showAlert(strError(), str("service.user.duplicates.error"),
                    Storage.userDuplicates.entrySet().stream().map(e -> e.getKey() + " == " + e.getValue()).collect(Collectors.joining("\n")), Alert.AlertType.WARNING);
        }
    }

    public static void recalculateHashes() {
        try {
            Instant start = Instant.now();
            calculateShortList();
            FileUtils.writeToFile(FileUtils.getBaseGamesDatFile(), Storage.baseHashes.entrySet().stream().sorted(Map.Entry.comparingByValue()).map(e -> e.getValue() + " - " + e.getKey()).toList());
            String time = Utils.formatSeconds(Duration.between(start, Instant.now()).toMillis());
            JavaFxUtils.showAlert("DreamBeam", str("service.short.list.created"), String.format("%s: %s s", str("service.run.time"), time), Alert.AlertType.INFORMATION);

        } catch (IOException e) {
            JavaFxUtils.showAlert(strError(), str("service.short.list.create.error"), e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public static void readGamesDat() {
        Storage.baseHashes = new HashMap<>();

        Path path = FileUtils.getBaseGamesDatFile().normalize().toAbsolutePath();
        if (FileUtils.exists(path)) {
            try {
                var pair = Utils.loadShortListHashes(path);
                Storage.baseHashes = pair.getLeft();
                Storage.baseDuplicates = pair.getRight();
                reportBaseDuplicates();
            } catch (Exception e) {
                JavaFxUtils.showAlert(strError(), String.format(str("service.short.list.file.read.error"), path), e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
                FileUtils.deleteSilently(path);
                recalculateHashes();
            }
        } else {
            recalculateHashes();
        }
    }

    public static void calculateShortList() throws IOException {
        var pair = Utils.calculateHashes(FileUtils.getBaseGamesDir());
        Storage.baseHashes = pair.getLeft();
        reportBaseDuplicates();
    }

    public static void loadTexts() {
        try {
            Storage.textMap = Utils.loadTexts(FileUtils.getTextsDir());
        } catch (IOException e) {
            JavaFxUtils.showAlert(strError(), str("service.texts.read.error"), e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
