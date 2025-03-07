package md.leonis.dreambeam.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import md.leonis.dreambeam.utils.Config;
import md.leonis.dreambeam.utils.JavaFxUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static md.leonis.dreambeam.utils.Config.str;
import static md.leonis.dreambeam.utils.Config.strError;

public class SettingsStageController implements Closeable {
    public ComboBox<String> languageComboBox;
    public CheckBox updateNotificationCheckBox;
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
    }

    public void closeButtonClick(ActionEvent actionEvent) {
        JavaFxUtils.closeStage(actionEvent);
    }

    public void okButtonClick(ActionEvent actionEvent) {
        Config.locale = locales.get(languageComboBox.getSelectionModel().getSelectedIndex());
        Config.updateNotification = updateNotificationCheckBox.isSelected();
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
