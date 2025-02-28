package md.leonis.dreambeam.view;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import md.leonis.dreambeam.utils.Config;
import md.leonis.dreambeam.utils.FileUtils;
import md.leonis.dreambeam.utils.JavaFxUtils;
import md.leonis.dreambeam.utils.Utils;

import java.io.Closeable;
import java.io.IOException;
import java.time.Instant;

import static md.leonis.dreambeam.utils.Config.HR;

public class MainStageController implements Closeable {

    public ListView<String> logListView;
    public MenuItem exitMenuItem;

    @FXML
    private void initialize() {
        logListView.setCellFactory(Utils::colorLines);

        if (Config.user != null) {
            try {
                logListView.setItems(FXCollections.observableList(FileUtils.readFromFile(Config.getRootDir().resolve(Config.user + ".log"))));
            } catch (IOException ignored) {
            }
        }

        log("");
        log(HR);
        log("Начало работы. " + Instant.now());
        log(HR);
    }

    public void exitMenuItemClick() {
        Platform.exit();
    }

    public void saveLogsAndClose() {
        log("");
        log(HR);
        log("Завершение работы. " + Instant.now());
        log(HR);

        try {
            FileUtils.writeToFile(Config.getRootDir().resolve(Config.user + ".log"), logListView.getItems());
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

    @Override
    public void close() throws IOException {
    }
}
