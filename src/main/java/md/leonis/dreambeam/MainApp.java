package md.leonis.dreambeam;

import javafx.application.Application;
import javafx.stage.Stage;
import md.leonis.dreambeam.utils.Config;
import md.leonis.dreambeam.utils.JavaFxUtils;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Config.loadProperties();
        JavaFxUtils.showMainPane(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
