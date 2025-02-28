package md.leonis.dreambeam.view;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import md.leonis.dreambeam.utils.Config;
import md.leonis.dreambeam.utils.JavaFxUtils;
import md.leonis.dreambeam.utils.Utils;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ComparePaneController implements Closeable {

    public Button closeButton;
    public Button compareButton;
    public Button backButton;

    public ToggleGroup leftToggleGroup;
    public ToggleGroup rightToggleGroup;

    public RadioButton leftUserRadioButton;
    public RadioButton leftBaseRadioButton;
    public RadioButton rightUserRadioButton;
    public RadioButton rightBaseRadioButton;

    public CheckBox differenceCheckBox;
    public CheckBox compactCheckBox;

    public ListView<String> leftListView;
    public ListView<String> rightListView;

    private List<String> userGames;
    private List<String> baseGames;

    @FXML
    private void initialize() {
        leftUserRadioButton.setUserData("v");
        rightUserRadioButton.setUserData("v");

        //todo в перспективе пользовательскую базу надо загружать фоном после старта и работать с ней.
        calculateUserHashes();

        baseGames = Utils.cleanAndSortGameNames(Config.baseHashes.values());
        userGames = Utils.cleanAndSortGameNames(Config.userHashes.values());


        //todo
        //реагировать на смену чеков даже во время сравнения

        showLists();
    }

    private void calculateUserHashes() {
        try {
            var pair = Utils.calculateHashes(Config.getUserDir());
            Config.userHashes = pair.getLeft();
            reportDuplicates(pair.getRight());
        } catch (IOException e) {
            JavaFxUtils.showAlert("Ошибка!", "Не удалось прочитать файлы пользовательской базы!", e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void reportDuplicates(Map<String, String> duplicates) {
        if (!duplicates.isEmpty()) {
            duplicates.entrySet().stream().map(e -> e.getKey() + " == " + e.getValue()).forEach(System.out::println);
            JavaFxUtils.showAlert("Ошибка!", "В базе данных есть дубликаты!",
                    duplicates.entrySet().stream().map(e -> e.getKey() + " == " + e.getValue()).collect(Collectors.joining("\n")), Alert.AlertType.WARNING);
        }
    }

    private void showLists() {
        showList(leftToggleGroup, leftListView);
        showList(rightToggleGroup, rightListView);
        leftListView.getSelectionModel().selectFirst();
        rightListView.getSelectionModel().selectFirst();
    }

    private void showList(ToggleGroup toggleGroup, ListView<String> listView) {
        RadioButton button = (RadioButton) toggleGroup.getSelectedToggle();
        if (button.getUserData() != null) {
            showMyGames(listView);
        } else {
            showBaseGames(listView);
        }
    }

    private void showMyGames(ListView<String> leftListView) {
        leftListView.setItems(FXCollections.observableList(userGames));
    }

    private void showBaseGames(ListView<String> leftListView) {
        leftListView.setItems(FXCollections.observableList(baseGames));
    }

    //todo скрывать кнопки при переключении режима

    public void compareButtonClick() {
        List<String> //todo считать, выводить
    }

    public void backButtonClick() {
        showLists();
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
