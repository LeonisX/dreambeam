package md.leonis.dreambeam.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.Closeable;
import java.io.IOException;

public class ComparePaneController implements Closeable {

    public Button closeButton;
    public Button compareButton;
    public Button backButton;

    public ToggleGroup leftToggleGroup;
    public ToggleGroup rightToggleGroup;

    public RadioButton leftMyRadioButton;
    public RadioButton leftBaseRadioButton;
    public RadioButton rightMyRadioButton;
    public RadioButton rightBaseRadioButton;

    public CheckBox differenceCheckBox;
    public CheckBox compactCheckBox;

    public ListView<String> leftListView;
    public ListView<String> rightListView;

    @FXML
    private void initialize() {
        leftMyRadioButton.setUserData("my");
        rightMyRadioButton.setUserData("my");

        //todo load user data
        //todo в перспективе пользовательскую базу надо загружать фоном после старта и работать с ней.
    }

    public void closeButtonClick(ActionEvent actionEvent) {
    }

    public void compareButtonClick(ActionEvent actionEvent) {
    }

    public void backButtonClick(ActionEvent actionEvent) {
    }

    @Override
    public void close() throws IOException {
    }
}
