package md.leonis.dreambeam.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import md.leonis.dreambeam.utils.Config;
import md.leonis.dreambeam.utils.JavaFxUtils;

public class SavePaneController {

    public Button backButton;
    public TextArea tempTextArea;

    @FXML
    private void initialize() {
        String name = Config.hashes.get(Config.crc32);

        if (name != null) {
            tempTextArea.setText("Диск распознан как " + name);
        } else {
            tempTextArea.setText("Неизвестный образ диска!");
        }
    }

    public void backButtonClick() {
        JavaFxUtils.showPane("ViewPane.fxml");
    }

}
