package md.leonis.dreambeam.utils;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import md.leonis.dreambeam.MainApp;
import md.leonis.dreambeam.view.MainStageController;
import org.apache.commons.lang3.StringUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;

public class JavaFxUtils {

    private static BorderPane rootLayout;
    private static MainStageController controller;

    private static final int sceneWidth = 900;
    private static final int sceneHeight = 700;

    public static Closeable currentController;

    @SuppressWarnings("all")
    public static void showMainPane(Stage primaryStage) {
        primaryStage.setTitle("DreamBeam");
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource(Config.resourcePath + "MainStage.fxml"));
            rootLayout = loader.load();
            controller = loader.getController();
            Scene scene = new Scene(rootLayout, sceneWidth, sceneHeight);
            primaryStage.setScene(scene);
            primaryStage.setOnHiding(event -> {
                controller.saveLogsAndClose();
                try {
                    currentController.close();
                } catch (Exception ignored) {
                }
            });

            showPrimaryPanel();

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showPrimaryPanel() {
        showPane("PrimaryPane.fxml");
    }

    public static void showPane(String resource) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(MainApp.class.getResource(Config.resourcePath + resource));
                Region innerPane = loader.load();
                currentController = loader.getController();
                //if (controller instanceof SubPane) ((SubPane) controller).init();
                ((BorderPane) rootLayout.getCenter()).setCenter(((BorderPane) innerPane).getCenter());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void showWizardWindow() {
        showWindow("WizardStage.fxml", "Мастер названия диска");
    }

    public static void showCompareWindow() {
        showWindow("CompareStage.fxml", "Сравнение дисков");
    }

    public static void showStatsWindow() {
        showWindow("StatsStage.fxml", "Статистика");
    }

    public static void showWindow(String resource, String title) {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource(Config.resourcePath + resource));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root, 900, 600));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void showAlert(String title, String header, String text, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);

        //TextArea textArea = new TextArea(text);
        //textArea.setEditable(false);
        //textArea.setWrapText(true);

        /*textArea.setMinWidth(720);
        textArea.setMinHeight(600);
        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);*/

        //alert.getDialogPane().setContent(textArea);

        //alert.setResizable(true);
        alert.setContentText(text);
        //alert.setWidth(800);
        //alert.getDialogPane().setPrefSize(880, 320);
        //alert.getDialogPane().getChildren().stream().filter(node -> node instanceof Label).forEach(node -> ((Label) node).setMinHeight(Region.USE_PREF_SIZE));
        alert.showAndWait();
    }

    public static void showAlert(String title, String header, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.showAndWait();
    }

    public static Optional<ButtonType> showConfirmation(String title, String header, String text) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);

        return alert.showAndWait();
    }

    public static Optional<String> showInputDialog(String title, String header, String content) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(header);
        dialog.setContentText(content);
        dialog.setOnCloseRequest(dialogEvent -> {
            if (StringUtils.isBlank(dialog.getEditor().getText())) {
                dialogEvent.consume();
            }
        });

        return dialog.showAndWait();
    }

    public static void log(String message) {
        controller.log(message);
    }
}
