package md.leonis.dreambeam.view;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import md.leonis.dreambeam.model.FileRecord;
import md.leonis.dreambeam.model.ListViewHandler;
import md.leonis.dreambeam.model.Pair;
import md.leonis.dreambeam.model.enums.CompareStatus;
import md.leonis.dreambeam.utils.Config;
import md.leonis.dreambeam.utils.FileUtils;
import md.leonis.dreambeam.utils.JavaFxUtils;
import md.leonis.dreambeam.utils.Utils;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static md.leonis.dreambeam.utils.Config.str;
import static md.leonis.dreambeam.utils.Config.strError;

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

    public ListView<String> leftListView;
    public ListView<String> rightListView;

    private boolean leftUser = true;
    private boolean rightUser = false;

    private String leftFile;
    private String rightFile;

    private volatile List<String> userGames;
    private volatile List<String> baseGames;

    @FXML
    private void initialize() {
        leftUserRadioButton.setUserData("v");
        rightUserRadioButton.setUserData("v");

        calculateUserHashes();

        baseGames = Config.baseHashes.values().stream().sorted().toList();
        userGames = Config.userHashes.values().stream().sorted().toList();

        leftToggleGroup.selectedToggleProperty().addListener((group, oldToggle, newToggle) -> {
            leftUser = newToggle.getUserData() != null;
            showLists();
        });
        rightToggleGroup.selectedToggleProperty().addListener((group, oldToggle, newToggle) -> {
            rightUser = newToggle.getUserData() != null;
            showLists();
        });

        leftListView.setCellFactory(Utils::colorLines);
        rightListView.setCellFactory(Utils::colorLines);

        var leftHandler = new ListViewHandler<>(leftListView, rightListView);
        leftListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue)
                -> leftHandler.sync(newValue));
        leftListView.setOnKeyPressed(leftHandler::handle);

        var rightHandler = new ListViewHandler<>(rightListView, leftListView);
        rightListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue)
                -> rightHandler.sync(newValue));
        rightListView.setOnKeyPressed(rightHandler::handle);

        differenceCheckBox.selectedProperty().addListener((obs, oldValue, newValue) -> reactOnCheckBoxes());

        showLists();
    }

    private void reactOnCheckBoxes() {
        if (compareButton.isDisabled()) {
            compare();
        }
    }

    private void calculateUserHashes() {
        try {
            var pair = Utils.calculateHashes(Config.getUserDir());
            Config.userHashes = pair.getLeft();
            reportDuplicates(pair.getRight());
        } catch (IOException e) {
            JavaFxUtils.showAlert(strError(), str("compare.user.files.read.error"), e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void reportDuplicates(Map<String, String> duplicates) {
        if (!duplicates.isEmpty()) {
            JavaFxUtils.showAlert(strError(), str("compare.user.duplicates.error"),
                    duplicates.entrySet().stream().map(e -> e.getKey() + " == " + e.getValue()).collect(Collectors.joining("\n")), Alert.AlertType.WARNING);
        }
    }

    private void showLists() {
        showList(leftListView, leftUser);
        showList(rightListView, rightUser);
        leftListView.getSelectionModel().selectFirst();
        rightListView.getSelectionModel().selectFirst();
    }

    private void showList(ListView<String> listView, boolean isUser) {
        if (isUser) {
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

    public void compareButtonClick() {
        backButton.setDisable(false);
        compareButton.setDisable(true);

        leftFile = leftListView.getSelectionModel().getSelectedItem();
        rightFile = rightListView.getSelectionModel().getSelectedItem();

        compare();
    }

    public void compare() {
        try {
            List<String> leftLines = loadRecords(leftFile, leftUser);
            List<String> rightLines = loadRecords(rightFile, rightUser);
            Map<String, FileRecord> leftRecords = mapGamesList(leftLines);
            Map<String, FileRecord> rightRecords = mapGamesList(rightLines);

            leftLines = mapGamesListFull(leftRecords, rightRecords, differenceCheckBox.isSelected());
            rightLines = mapGamesListFull(rightRecords, leftRecords, differenceCheckBox.isSelected());

            leftListView.setItems(FXCollections.observableList(Objects.requireNonNull(leftLines)));
            rightListView.setItems(FXCollections.observableList(Objects.requireNonNull(rightLines)));

        } catch (IOException e) {
            JavaFxUtils.showAlert(strError(), str("compare.compare.disks.error"), e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private List<String> loadRecords(String file, boolean isUser) throws IOException {
        return FileUtils.readFromFile(getGamePath(file, isUser));
    }

    private Map<String, FileRecord> mapGamesList(List<String> lines) {
        return lines.subList(1, lines.size() - 1).stream().map(FileRecord::parseLine)
                .collect(Collectors.toMap(FileRecord::title, Function.identity(), (v1, v2) -> v1, LinkedHashMap::new));
    }

    public static List<String> mapGamesListFull(Map<String, FileRecord> records1, Map<String, FileRecord> records2, boolean diffOnly) {
        //1. найти общие
        List<Pair<FileRecord, FileRecord>> table = withNullsList(records1, records2);

        List<String> result = new ArrayList<>();

        for (Pair<FileRecord, FileRecord> pair : table) {
            FileRecord left = pair.getLeft();
            FileRecord right = pair.getRight();
            if (left == null) {
                result.add("~" + right.fullTitle());
            } else {
                result.add(compare(left, right, diffOnly));
            }
        }

        return result.stream().filter(Objects::nonNull).toList();
    }

    public static List<Pair<FileRecord, FileRecord>> withNullsList(Map<String, FileRecord> left, Map<String, FileRecord> right) {
        return Stream.concat(left.entrySet().stream(), right.entrySet().stream()).map(Map.Entry::getKey)
                .distinct().sorted().map(t -> Pair.of(left.get(t), right.get(t))).toList();
    }

    public static int index(List<FileRecord> list, String title, int index) {
        for (int i = index; i < list.size(); i++) {
            if (list.get(i).title().equals(title)) {
                return i;
            }
        }
        throw new IllegalStateException();
    }

    public static String compare(FileRecord left, FileRecord right, boolean diffOnly) {
        CompareStatus status = compare(left, right);
        if (diffOnly && status.equals(CompareStatus.EQUALS)) {
            return null;
        } else {
            return status.getMarker() + left.fullTitle();
        }
    }

    public static CompareStatus compare(FileRecord left, FileRecord right) {
        if (right == null) {
            return CompareStatus.ABSENT;
        }
        if (left.isError()) {
            return CompareStatus.ERROR;
        }
        int code = 0;
        if (!left.hash().equals(right.hash())) {
            code++;
        }
        if (left.size() != right.size()) {
            code++;
        }
        return CompareStatus.values()[code];
    }

    private Path getGamePath(String fileName, boolean isUser) {
        return isUser ? Config.getUserFile(fileName) : Config.getBaseGamesFile(fileName);
    }

    public void backButtonClick() {
        showLists();
        backButton.setDisable(true);
        compareButton.setDisable(false);
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
