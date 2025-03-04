package md.leonis.dreambeam.view;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import md.leonis.dreambeam.utils.*;

import java.io.Closeable;
import java.io.IOException;
import java.time.Instant;

import static md.leonis.dreambeam.utils.Config.*;

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
        log(String.format("%s %s", str("main.log.start"), Instant.now()));
        log(HR);


        inputUserName();
    }

    private void inputUserName() {
        if (!Config.isUser()) {
            JavaFxUtils.showInputDialog(str("primary.new.user"), str("primary.enter.your.name"), null).ifPresentOrElse(this::setAndSaveUser, this::inputUserName);
        }
    }

    private void setAndSaveUser(String user) {
        Config.setUser(user);
        try {
            Config.saveProperties();
            FileUtils.createDirectories(Config.getUserDir());
        } catch (IOException e) {
            JavaFxUtils.showAlert(strError(), str("primary.config.file.save.error"), e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
            Config.setUser("Anonymous");
        }
    }

    public void exitMenuItemClick() {
        Platform.exit();
    }

    public void saveLogsAndClose() {
        log("");
        log(HR);
        log(String.format("%s %s", str("main.log.finish"), Instant.now()));
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
