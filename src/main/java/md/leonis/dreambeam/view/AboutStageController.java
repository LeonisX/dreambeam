package md.leonis.dreambeam.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import md.leonis.dreambeam.utils.Config;

import java.io.Closeable;
import java.io.IOException;

public class AboutStageController implements Closeable {

    public Label versionLabel;
    public Label timeLabel;
    public Button closeButton;

    @FXML
    private void initialize() {
        versionLabel.setText(Config.projectVersion);
        timeLabel.setText(Config.projectTime);
    }

    public void closeButtonClick(ActionEvent actionEvent) {
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @Override
    public void close() throws IOException {
    }
}
