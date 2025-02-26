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
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

public class SavePaneController {

    public Button backButton;
    public HBox okHBox;
    public HBox nokHBox;
    public Button saveButton;
    public TextField titleTextField;

    private String name;
    private boolean recognized;

    @FXML
    private void initialize() {
        name = Config.hashes.get(Config.crc32);
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

    public void backButtonClick() {
        JavaFxUtils.showPane("ViewPane.fxml");
    }

    public void saveButtonClick() {
        if (FileUtils.exists(Config.getBaseGamesPath(name)) && !recognized) {
            JavaFxUtils.showAlert("Образ уже есть в основной базе!", "Возможно стоит указать, что это альтернативная версия.", "Например, " + name + " (Alt)", Alert.AlertType.WARNING);

        } else if (FileUtils.exists(Config.getUserPath(name))) {
            var buttonType = JavaFxUtils.showConfirmation("Образ уже есть в вашей базе!", "Перезаписать?", name);
            if (buttonType.isPresent() && buttonType.get().equals(ButtonType.OK)) {
                saveAndClose();
            }

        } else {
            saveAndClose();
        }
    }

    private void saveAndClose() {
        try {
            FileUtils.saveToFile(Config.getUserPath(name), Config.saveFiles);
            JavaFxUtils.showPane("PrimaryPane.fxml");
        } catch (IOException e) {
            JavaFxUtils.showAlert("Ошибка!", "Не удалось сохранить файл " + name, e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
