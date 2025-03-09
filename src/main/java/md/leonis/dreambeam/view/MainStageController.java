package md.leonis.dreambeam.view;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import md.leonis.dreambeam.statik.Config;
import md.leonis.dreambeam.statik.Storage;
import md.leonis.dreambeam.utils.FileUtils;
import md.leonis.dreambeam.utils.JavaFxUtils;
import md.leonis.dreambeam.utils.Utils;

import java.io.Closeable;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.stream.Collectors;

import static md.leonis.dreambeam.statik.Config.str;
import static md.leonis.dreambeam.statik.Config.strError;

public class MainStageController implements Closeable {

    public ListView<String> logListView;
    public MenuItem exitMenuItem;
    public MenuItem settingsMenuItem;

    @FXML
    private void initialize() {
        logListView.setCellFactory(Utils::colorLines);

        if (Config.user != null) {
            try {
                logListView.setItems(FXCollections.observableList(FileUtils.readFromFile(FileUtils.getRootDir().resolve(FileUtils.getUserLogFile()))));
            } catch (IOException ignored) {
            }
        }

        log("");
        log(Storage.HR);
        log(String.format("%s %s", str("main.log.start"), Instant.now()));
        log(Storage.HR);
    }

    public void settingsMenuItemClick() {
        JavaFxUtils.showSettingsWindow();
    }

    public void exitMenuItemClick() {
        Platform.exit();
    }

    public void saveLogsAndClose() {
        log("");
        log(Storage.HR);
        log(String.format("%s %s", str("main.log.finish"), Instant.now()));
        log(Storage.HR);

        try {
            FileUtils.writeToFile(FileUtils.getRootDir().resolve(Config.user + ".log"), logListView.getItems());
        } catch (IOException ignored) {
        }
    }

    public void log(String message) {
        Platform.runLater(() -> {
            logListView.getItems().add(message);
            logListView.scrollTo(logListView.getItems().size());
        });
    }

    public void showDiskNameWizard() {
        JavaFxUtils.showWizardWindow();
    }

    public void compareDisks() {
        JavaFxUtils.showCompareWindow();
    }

    public void showStatistics() {
        JavaFxUtils.showStatsWindow();
    }

    public void showAudit() {
        JavaFxUtils.showAuditWindow();
    }

    public void showAbout() {
        JavaFxUtils.showAboutWindow();
    }

    public void recalculateShortDb() {
        try {
            Instant start = Instant.now();
            Storage.calculateBaseHashesAndSave();
            String time = Utils.formatSeconds(Duration.between(start, Instant.now()).toMillis());
            JavaFxUtils.showAlert("DreamBeam", str("main.short.list.created"), String.format("%s: %s s", str("main.run.time"), time), Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            JavaFxUtils.showAlert(strError(), str("main.short.list.create.error"), e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public static void calculateBaseHashesAndSave() throws Exception {
        Storage.calculateBaseHashesAndSave();
        reportBaseDuplicates();
    }

    public static void calculateBaseHashes(boolean reportDuplicates, boolean force) {
        try {
            Storage.loadBaseFiles(force);
            if (reportDuplicates) {
                reportBaseDuplicates();
            }
        } catch (Exception e) {
            JavaFxUtils.showAlert(strError(), str("main.base.files.read.error"), e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    public static void reportBaseDuplicates() {
        if (Storage.baseDuplicates != null && !Storage.baseDuplicates.isEmpty()) {
            JavaFxUtils.showAlert(strError(), str("main.base.duplicates.error"),
                    Storage.baseDuplicates.entrySet().stream().map(e -> e.getKey() + " == " + e.getValue()).collect(Collectors.joining("\n")), Alert.AlertType.WARNING);
        }
    }

    public static void calculateUserHashes(boolean reportDuplicates, boolean force) {
        try {
            Storage.loadUserFiles(force);
            if (reportDuplicates) {
                reportUserDuplicates();
            }
        } catch (Exception e) {
            JavaFxUtils.showAlert(strError(), str("main.user.files.read.error"), e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private static void reportUserDuplicates() {
        if (!Storage.userDuplicates.isEmpty()) {
            JavaFxUtils.showAlert(strError(), str("main.user.duplicates.error"),
                    Storage.userDuplicates.entrySet().stream().map(e -> e.getKey() + " == " + e.getValue()).collect(Collectors.joining("\n")), Alert.AlertType.WARNING);
        }
    }

    public static void loadTexts() {
        try {
            Storage.loadTexts();
        } catch (Exception e) {
            JavaFxUtils.showAlert(strError(), str("main.texts.read.error"), e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @Override
    public void close() throws IOException {
    }
}
