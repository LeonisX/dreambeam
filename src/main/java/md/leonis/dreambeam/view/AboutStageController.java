package md.leonis.dreambeam.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import md.leonis.dreambeam.statik.VersionConfig;
import md.leonis.dreambeam.utils.JavaFxUtils;

import java.io.Closeable;
import java.io.IOException;

public class AboutStageController implements Closeable {

    public Label versionLabel;
    public Label timeLabel;
    public Button closeButton;

    @FXML
    private void initialize() {
        versionLabel.setText(VersionConfig.getVersion());
        timeLabel.setText(VersionConfig.getAboutDate());
    }

    public void closeButtonClick(ActionEvent actionEvent) {
        JavaFxUtils.closeStage(actionEvent);
    }

    @Override
    public void close() throws IOException {
    }
}
