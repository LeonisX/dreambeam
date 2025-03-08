package md.leonis.dreambeam.view;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import md.leonis.dreambeam.statik.Config;
import md.leonis.dreambeam.statik.Storage;
import md.leonis.dreambeam.utils.FileUtils;
import md.leonis.dreambeam.utils.JavaFxUtils;
import md.leonis.dreambeam.utils.ServiceUtils;
import md.leonis.dreambeam.utils.Utils;

import java.io.Closeable;
import java.io.IOException;
import java.time.Instant;

import static md.leonis.dreambeam.statik.Config.str;

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
        Platform.runLater( () -> {
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
        ServiceUtils.recalculateHashes();
    }

    @Override
    public void close() throws IOException {
    }
}
