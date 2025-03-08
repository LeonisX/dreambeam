package md.leonis.dreambeam.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import md.leonis.dreambeam.statik.Config;
import md.leonis.dreambeam.statik.Storage;
import md.leonis.dreambeam.utils.FileUtils;
import md.leonis.dreambeam.utils.JavaFxUtils;
import md.leonis.dreambeam.utils.StringUtils;

import java.io.Closeable;
import java.io.IOException;

import static md.leonis.dreambeam.statik.Config.str;
import static md.leonis.dreambeam.statik.Config.strError;

public class SavePaneController implements Closeable {

    public Button closeButton;
    public HBox okHBox;
    public HBox nokHBox;
    public Button saveButton;
    public TextField titleTextField;
    public Button runWizardButton;
    public Label okLabel;

    private String name;
    private boolean recognized;

    @FXML
    private void initialize() {
        name = Storage.baseHashes.get(Storage.diskImage.getCrc32());
        recognized = (name != null);
        boolean recognizedUser = false;

        if (!recognized) {
            name = Storage.userHashes.get(Storage.diskImage.getCrc32());
            recognized = (name != null);
            recognizedUser = (name != null);
        }

        if (recognized) {
            titleTextField.setText(name);
        }

        if (recognizedUser) {
            JavaFxUtils.log(String.format("@%s %s %s", str("save.disk.recognized.as"), name, str("save.user.database")));
        } else if (recognized) {
            JavaFxUtils.log(String.format("@%s %s", str("save.disk.recognized.as"), name));
        } else {
            JavaFxUtils.log(String.format("#%s", str("save.unknown.disk")));
        }

        saveButton.setDisable(!recognized);
        nokHBox.setVisible(!recognized);
        okLabel.setText(recognizedUser ? str("save.user.disk.recognized") : str("save.base.disk.recognized"));
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
        if (FileUtils.exists(FileUtils.getBaseGamesFile(name)) && !recognized) {
            JavaFxUtils.showAlert(str("save.base.image.exists"), str("save.alt.version"), String.format("%s, %s (Alt)", str("save.for.example"), name), Alert.AlertType.WARNING);

        } else if (FileUtils.exists(FileUtils.getUserFile(name))) {
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
        JavaFxUtils.log(Storage.HR);
    }

    private void saveToBase() {
        try {
            if (Config.admin) {
                Storage.saveUserFileToBase(name);
            }
        } catch (Exception e) {
            showFileAlert(e, name);
        }
    }

    private void saveAndClose() {
        try {
            Storage.saveUserFile(name);
            JavaFxUtils.showPrimaryPanel();
        } catch (Exception e) {
            showFileAlert(e, name);
        }
    }

    private void showFileAlert(Exception e, String name) {
        JavaFxUtils.showAlert(strError(), String.format("%s: %s", str("save.file.save.error"), name), e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
    }

    public void runWizardButtonClick() {
        Storage.wizardName = null;
        JavaFxUtils.showWizardWindow();
        Thread thread = new Thread(() -> {
            while (Storage.wizardName == null) {
                sleep();
            }
            if (StringUtils.isNotBlank(Storage.wizardName)) {
                titleTextField.setText(Storage.wizardName);
                JavaFxUtils.log(str("save.image.name.log"));
                JavaFxUtils.log(Storage.wizardName);
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
