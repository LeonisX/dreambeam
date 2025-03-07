package md.leonis.dreambeam.utils;

import javafx.scene.control.Alert;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static md.leonis.dreambeam.utils.Config.str;
import static md.leonis.dreambeam.utils.Config.strError;

public class ServiceUtils {

    public static void calculateBaseHashes(boolean reportDuplicates) {
        try {
            var pair = Utils.calculateHashes(Config.getBaseGamesDir());
            Config.baseHashes = pair.getLeft();
            Config.baseDuplicates = pair.getRight();
            if (reportDuplicates) {
                reportBaseDuplicates();
            }
        } catch (IOException e) {
            JavaFxUtils.showAlert(strError(), str("service.base.files.read.error"), e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public static void reportBaseDuplicates() {
        if (Config.baseDuplicates != null && !Config.baseDuplicates.isEmpty()) {
            JavaFxUtils.showAlert(strError(), str("service.base.duplicates.error"),
                    Config.baseDuplicates.entrySet().stream().map(e -> e.getKey() + " == " + e.getValue()).collect(Collectors.joining("\n")), Alert.AlertType.WARNING);
        }
    }

    public static void calculateUserHashes(boolean reportDuplicates, boolean force) {
        if (Config.userHashesLoaded && !force) {
            return;
        }
        try {
            Config.userHashesLoaded = false;
            var pair = Utils.calculateHashes(Config.getUserDir());
            Config.userHashes = pair.getLeft();
            Config.userDuplicates = pair.getRight();
            if (reportDuplicates) {
                reportUserDuplicates();
            }
            Config.userHashesLoaded = true;
        } catch (IOException e) {
            JavaFxUtils.showAlert(strError(), str("service.user.files.read.error"), e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public static void reportUserDuplicates() {
        if (!Config.userDuplicates.isEmpty()) {
            JavaFxUtils.showAlert(strError(), str("service.user.duplicates.error"),
                    Config.userDuplicates.entrySet().stream().map(e -> e.getKey() + " == " + e.getValue()).collect(Collectors.joining("\n")), Alert.AlertType.WARNING);
        }
    }

    public static void recalculateHashes() {
        try {
            Instant start = Instant.now();
            calculateShortList();
            FileUtils.writeToFile(Config.getBaseGamesDatFile(), Config.baseHashes.entrySet().stream().sorted(Map.Entry.comparingByValue()).map(e -> e.getValue() + " - " + e.getKey()).toList());
            String time = Utils.formatSeconds(Duration.between(start, Instant.now()).toMillis());
            JavaFxUtils.showAlert("DreamBeam", str("service.short.list.created"), String.format("%s: %s s", str("service.run.time"), time), Alert.AlertType.INFORMATION);

        } catch (IOException e) {
            JavaFxUtils.showAlert(strError(), str("service.short.list.create.error"), e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public static void readGamesDat() {
        Config.baseHashes = new HashMap<>();

        Path path = Config.getBaseGamesDatFile().normalize().toAbsolutePath();
        if (FileUtils.exists(path)) {
            try {
                var pair = Utils.loadShortListHashes(path);
                Config.baseHashes = pair.getLeft();
                Config.baseDuplicates = pair.getRight();
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
        var pair = Utils.calculateHashes(Config.getBaseGamesDir());
        Config.baseHashes = pair.getLeft();
        reportBaseDuplicates();
    }

    public static void loadTexts() {
        try {
            Config.textMap = Utils.loadTexts(Config.getTextsDir());
        } catch (IOException e) {
            JavaFxUtils.showAlert(strError(), str("service.texts.read.error"), e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
