package md.leonis.dreambeam.view;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import md.leonis.dreambeam.utils.Config;
import md.leonis.dreambeam.utils.FileUtils;

import java.io.IOException;
import java.time.Instant;

import static md.leonis.dreambeam.utils.Config.HR;

public class MainStageController {

    public ListView<String> logListView;
    public MenuItem exitMenuItem;

    @FXML
    private void initialize() {

        logListView.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(String message, boolean empty) {
                super.updateItem(message, empty);

                if (empty || message == null) {
                    setText(null);

                } else {
                    char firstSymbol = message.isEmpty() ? '-' : message.charAt(0);

                    switch (firstSymbol) {
                        case '#' -> {
                            this.setTextFill(Color.BLUE);   // #
                            setText(message.substring(1));
                        }
                        case '@' -> {
                            this.setTextFill(Color.GREEN);  // @
                            setText(message.substring(1));
                        }
                        case '!' -> {
                            this.setTextFill(Color.RED);    // !
                            setText(message.substring(1));
                        }
                        default -> setText(message);
                    }
                }
            }
        });

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
}
