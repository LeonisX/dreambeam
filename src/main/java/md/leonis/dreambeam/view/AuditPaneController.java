package md.leonis.dreambeam.view;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.io.Closeable;
import java.io.IOException;

public class AuditPaneController implements Closeable {

    public ListView filesListView;

    @FXML
    private void initialize() {
    }

    @Override
    public void close() throws IOException {
    }
}
