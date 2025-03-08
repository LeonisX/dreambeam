package md.leonis.dreambeam;

import javafx.application.Application;
import javafx.stage.Stage;
import md.leonis.dreambeam.statik.Config;
import md.leonis.dreambeam.statik.VersionConfig;
import md.leonis.dreambeam.utils.JavaFxUtils;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        VersionConfig.load();
        Config.loadProperties();
        Config.loadLanguages();
        JavaFxUtils.showMainPane(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
