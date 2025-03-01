package md.leonis.dreambeam.view;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import md.leonis.dreambeam.model.ListViewHandler;
import md.leonis.dreambeam.utils.Config;
import md.leonis.dreambeam.utils.JavaFxUtils;
import md.leonis.dreambeam.utils.Utils;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

public class BaseStageController implements Closeable {

    public RadioButton baseRadioButton;
    public RadioButton userRadioButton;
    public Button closeButton;
    public ListView<String> gamesListView;
    public TextArea textArea; //todo //После редактирования автоматическое сохранение. Удалил текст - удалился файл.
    public ToggleGroup toggleGroup;

    @FXML
    private void initialize() {
        //todo setup
        userRadioButton.setUserData("v");

        gamesListView.setCellFactory(Utils::colorLines);
        gamesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> showText(newValue));

        //навигация набором букв
        var handler = new ListViewHandler<>(gamesListView);
        gamesListView.setOnKeyPressed(handler::handle);

        toggleGroup.selectedToggleProperty().addListener((group, oldToggle, newToggle) -> showGames());

        calculateUserHashes();
        loadTexts();

        showGames();
    }

    private void showText(String line) {
        if (line != null && line.startsWith("+")) {
            String hash = Config.baseHashes.entrySet().stream().filter(e -> e.getValue().equals(line.substring(1))).findFirst().get().getKey();
            textArea.setText(Config.textMap.get(hash));
        } else {
            textArea.clear();
        }
    }

    //todo duplicate
    private void calculateUserHashes() {
        try {
            var pair = Utils.calculateHashes(Config.getUserDir());
            Config.userHashes = pair.getLeft();
            reportDuplicates(pair.getRight());
        } catch (IOException e) {
            JavaFxUtils.showAlert("Ошибка!", "Не удалось прочитать файлы пользовательской базы!", e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    //todo duplicate
    private void reportDuplicates(Map<String, String> duplicates) {
        if (!duplicates.isEmpty()) {
            duplicates.entrySet().stream().map(e -> e.getKey() + " == " + e.getValue()).forEach(System.out::println);
            JavaFxUtils.showAlert("Ошибка!", "В вашей базе данных есть дубликаты!",
                    duplicates.entrySet().stream().map(e -> e.getKey() + " == " + e.getValue()).collect(Collectors.joining("\n")), Alert.AlertType.WARNING);
        }
    }

    private void loadTexts() {
        try {
            //todo стоит создать директорию сначала
            Config.textMap = Utils.loadTexts(Config.getTextsDir());
        } catch (IOException e) {
            JavaFxUtils.showAlert("Ошибка!", "Не удалось прочитать описания!", e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showGames() {
        textArea.clear();
        //todo source
        var list = Config.baseHashes.entrySet().stream().sorted(Map.Entry.comparingByValue()).map(this::addTextMark).toList();
        gamesListView.setItems(FXCollections.observableList(list));
    }

    private String addTextMark(Map.Entry<String, String> entry) {
        return hasText(entry.getKey()) ? '+' + entry.getValue() : entry.getValue();
    }

    private boolean hasText(String line) {
        return Config.textMap.containsKey(line);
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
