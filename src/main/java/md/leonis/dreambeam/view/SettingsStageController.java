package md.leonis.dreambeam.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import md.leonis.dreambeam.statik.Config;
import md.leonis.dreambeam.utils.JavaFxUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static md.leonis.dreambeam.statik.Config.str;
import static md.leonis.dreambeam.statik.Config.strError;

public class SettingsStageController implements Closeable {
    public ComboBox<String> languageComboBox;
    public CheckBox updateNotificationCheckBox;
    public TextField alcoholPathTextField;
    public TextField alcoholDriveLetterTextField;
    public Button okButton;

    private final List<Locale> locales = List.of(Locale.of("en", "US"), Locale.of("ru", "RU"));

    //нотиф - 1 раз
    @FXML
    private void initialize() {
        for (Locale locale: locales) {
            languageComboBox.getItems().add(locale.getDisplayLanguage());
        }
        languageComboBox.getSelectionModel().select(Math.max(0, locales.indexOf(Config.locale)));

        updateNotificationCheckBox.setSelected(Config.updateNotification);
        alcoholPathTextField.setText(Config.alcoholPath);
        alcoholDriveLetterTextField.setText(Config.alcoholDriveLetter);
    }

    public void closeButtonClick(ActionEvent actionEvent) {
        JavaFxUtils.closeStage(actionEvent);
    }

    public void okButtonClick(ActionEvent actionEvent) {
        Config.locale = locales.get(languageComboBox.getSelectionModel().getSelectedIndex());
        Config.updateNotification = updateNotificationCheckBox.isSelected();
        Config.alcoholPath = alcoholPathTextField.getText();
        Config.alcoholDriveLetter = alcoholDriveLetterTextField.getText().toUpperCase();
        try {
            Config.saveProperties();
            //FileUtils.createDirectories(Config.getUserDir());
        } catch (IOException e) {
            JavaFxUtils.showAlert(strError(), str("primary.config.file.save.error"), e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
            //Config.setUser(Config.DEFAULT_USER);
        }
        JavaFxUtils.closeStage(actionEvent);
    }

    @Override
    public void close() throws IOException {
    }
}
