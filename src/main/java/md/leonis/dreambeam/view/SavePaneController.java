package md.leonis.dreambeam.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import md.leonis.dreambeam.utils.Config;
import md.leonis.dreambeam.utils.FileUtils;
import md.leonis.dreambeam.utils.JavaFxUtils;
import md.leonis.dreambeam.utils.StringUtils;

import java.io.Closeable;
import java.io.IOException;

import static md.leonis.dreambeam.utils.Config.*;

public class SavePaneController implements Closeable {

    public Button closeButton;
    public HBox okHBox;
    public HBox nokHBox;
    public Button saveButton;
    public TextField titleTextField;
    public Button runWizardButton;

    private String name;
    private boolean recognized;

    @FXML
    private void initialize() {
        name = Config.baseHashes.get(Config.crc32);
        recognized = (name != null);

        if (recognized) {
            titleTextField.setText(name);
        }

        saveButton.setDisable(!recognized);
        nokHBox.setVisible(!recognized);
        okHBox.setVisible(recognized);

        titleTextField.textProperty().addListener((obs, old, nev) -> {
            name = nev;
            saveButton.setDisable(StringUtils.isBlank(titleTextField.getText()));
        });
    }

    public void closeButtonClick() {
        JavaFxUtils.showPrimaryPanel();
    }

    public void saveButtonClick() {
        if (FileUtils.exists(Config.getBaseGamesFile(name)) && !recognized) {
            JavaFxUtils.showAlert(str("save.base.image.exists"), str("save.alt.version"), String.format("%s, %s (Alt)", str("save.for.example"), name), Alert.AlertType.WARNING);

        } else if (FileUtils.exists(Config.getUserFile(name))) {
            var buttonType = JavaFxUtils.showConfirmation(str("save.user.image.exists"), str("save.overwrite"), name);
            if (buttonType.isPresent() && buttonType.get().equals(ButtonType.OK)) {
                logSave();
                saveAndClose();
            }

        } else {
            saveToBase();
            logSave();
            saveAndClose();
        }
    }

    private void logSave() {
        JavaFxUtils.log(str("save.image.saved.log"));
        JavaFxUtils.log(HR);
    }

    private void saveToBase() {
        try {
            if (Config.admin) {
                FileUtils.writeToFile(Config.getBaseGamesFile(name), Config.saveFiles);
            }
        } catch (IOException e) {
            showFileAlert(e, name);
        }
    }

    private void saveAndClose() {
        try {
            FileUtils.writeToFile(Config.getUserFile(name), Config.saveFiles);
            JavaFxUtils.showPrimaryPanel();
        } catch (IOException e) {
            showFileAlert(e, name);
        }
    }

    private void showFileAlert(Exception e, String name) {
        JavaFxUtils.showAlert(strError(), String.format("%s: %s", str("save.file.save.error"), name), e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
    }

    public void runWizardButtonClick() {
        Config.wizardName = null;
        JavaFxUtils.showWizardWindow();
        Thread thread = new Thread(() -> {
            while (Config.wizardName == null) {
                sleep();
            }
            if (StringUtils.isNotBlank(Config.wizardName)) {
                titleTextField.setText(Config.wizardName);
                JavaFxUtils.log(str("save.image.name.log"));
                JavaFxUtils.log(Config.wizardName);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private void sleep() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public void close() throws IOException {
    }
}
