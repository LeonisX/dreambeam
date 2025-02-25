package md.leonis.dreambeam.view;

import javafx.application.Platform;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;

public class MainStageController {

    public TextArea logTextArea;
    public MenuItem exitMenuItem;

    public void exitMenuItemClick() {
        Platform.exit();
    }
}
