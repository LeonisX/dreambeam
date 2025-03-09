package md.leonis.dreambeam.view;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import md.leonis.dreambeam.model.*;
import md.leonis.dreambeam.model.enums.Style;
import md.leonis.dreambeam.statik.Storage;
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

import static md.leonis.dreambeam.statik.Config.str;
import static md.leonis.dreambeam.statik.Config.strError;

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

    public ListView<Triple<Style, String, Object>> leftListView;
    public ListView<Triple<Style, String, Object>> rightListView;
    public Button similarButton;

    private boolean leftUser = true;
    private boolean rightUser = false;

    private Triple<Style, String, Object> leftFile;
    private Triple<Style, String, Object> rightFile;

    private volatile List<Triple<Style, String, Object>> userGames;
    private volatile List<Triple<Style, String, Object>> baseGames;

    @FXML
    private void initialize() {
        leftUserRadioButton.setUserData("v");
        rightUserRadioButton.setUserData("v");

        MainStageController.calculateUserHashes(true, false);

        baseGames = Storage.baseHashes.values().stream().sorted().map(s -> Triple.of(Style.DEFAULT, s, null)).toList();
        userGames = Storage.userHashes.values().stream().sorted().map(s -> Triple.of(Style.DEFAULT, s, null)).toList();

        leftToggleGroup.selectedToggleProperty().addListener((group, oldToggle, newToggle) -> {
            leftUser = newToggle.getUserData() != null;
            showLists();
        });
        rightToggleGroup.selectedToggleProperty().addListener((group, oldToggle, newToggle) -> {
            rightUser = newToggle.getUserData() != null;
            showLists();
        });

        leftListView.setCellFactory(Utils::colorLines2);
        rightListView.setCellFactory(Utils::colorLines2);

        var leftHandler = new ListViewHandler(leftListView, rightListView);
        leftListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue)
                -> leftHandler.sync(newValue));
        leftListView.setOnKeyPressed(leftHandler::handle);

        var rightHandler = new ListViewHandler(rightListView, leftListView);
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

    private void showLists() {
        showList(leftListView, leftUser);
        showList(rightListView, rightUser);
        leftListView.getSelectionModel().selectFirst();
        rightListView.getSelectionModel().selectFirst();
    }

    private void showList(ListView<Triple<Style, String, Object>> listView, boolean isUser) {
        if (isUser) {
            showMyGames(listView);
        } else {
            showBaseGames(listView);
        }
    }

    private void showMyGames(ListView<Triple<Style, String, Object>> leftListView) {
        leftListView.setItems(FXCollections.observableList(userGames));
    }

    private void showBaseGames(ListView<Triple<Style, String, Object>> leftListView) {
        leftListView.setItems(FXCollections.observableList(baseGames));
    }

    public void similarButtonClick() {
        backButton.setDisable(false);
        compareButton.setDisable(true);
        similarButton.setDisable(true);

        leftFile = leftListView.getSelectionModel().getSelectedItem();
        rightFile = rightListView.getSelectionModel().getSelectedItem();

        leftListView.scrollTo(0);
        rightListView.scrollTo(0);

        if (leftUser) {
            findSimilar(Storage.userFiles, rightUser ? Storage.userFiles : Storage.baseFiles, Storage.userHashes, rightUser ? Storage.userHashes : Storage.baseHashes);
        } else {
            findSimilar(Storage.baseFiles, rightUser ? Storage.userFiles : Storage.baseFiles, Storage.userHashes, rightUser ? Storage.userHashes : Storage.baseHashes);
        }
    }

    private void findSimilar(Map<String, DiskImage> sourceFiles, Map<String, DiskImage> destinationFiles,
                             Map<String, String> sourceHashes, Map<String, String> destinationHashes) {
        var selectedItem = leftListView.getSelectionModel().getSelectedItem();
        String hash = sourceHashes.entrySet().stream()
                .filter(e -> e.getValue().equals(selectedItem.getCenter())).map(Map.Entry::getKey).findFirst().orElse(null);
        DiskImage diskImage = sourceFiles.get(hash);
        var files = destinationFiles.values().parallelStream().map(di ->
                        new SavePaneController.Diff(di.getCrc32(), destinationHashes.get(di.getCrc32()), diskImage.calculateDiffPoints(diskImage, di), SavePaneController.Source.BASE))
                .filter(r -> r.diff() > 0).sorted((d1, d2) -> Double.compare(d2.diff(), d1.diff())).toList();

        List<Triple<Style, String, Object>> rightLines = files.stream().map(f -> Triple.of(Style.DEFAULT, String.format("%s (%.2f%%)", f.title(), f.diff()), null)).toList();

        leftListView.setItems(FXCollections.observableList(List.of(selectedItem)));
        rightListView.setItems(FXCollections.observableList(rightLines));
    }

    public void compareButtonClick() {
        backButton.setDisable(false);
        compareButton.setDisable(true);
        similarButton.setDisable(true);

        leftFile = leftListView.getSelectionModel().getSelectedItem();
        rightFile = rightListView.getSelectionModel().getSelectedItem();

        leftListView.scrollTo(0);
        rightListView.scrollTo(0);

        compare();
    }

    public void compare() {
        try {
            Map<String, FileRecord> leftRecords = loadRecords(leftFile, leftUser);
            Map<String, FileRecord> rightRecords = loadRecords(rightFile, rightUser);

            List<Triple<Style, String, Object>> leftLines = mapGamesListFull(leftRecords, rightRecords, differenceCheckBox.isSelected());
            List<Triple<Style, String, Object>> rightLines = mapGamesListFull(rightRecords, leftRecords, differenceCheckBox.isSelected());

            leftListView.setItems(FXCollections.observableList(Objects.requireNonNull(leftLines)));
            rightListView.setItems(FXCollections.observableList(Objects.requireNonNull(rightLines)));

        } catch (IOException e) {
            JavaFxUtils.showAlert(strError(), str("compare.compare.disks.error"), e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private Map<String, FileRecord> loadRecords(Triple<Style, String, Object> row, boolean isUser) throws IOException { //todo в перспективе брать из хранилища
        return new DiskImage(FileUtils.readFromFile(getGamePath(row.getCenter(), isUser))).getCompareRecords().stream()
                .collect(Collectors.toMap(FileRecord::title, Function.identity(), (v1, v2) -> v1, LinkedHashMap::new));
    }

    public static List<Triple<Style, String, Object>> mapGamesListFull(Map<String, FileRecord> records1, Map<String, FileRecord> records2, boolean diffOnly) {
        //1. найти общие
        List<Pair<FileRecord, FileRecord>> table = withNullsList(records1, records2);

        List<Triple<Style, String, Object>> result = new ArrayList<>();

        for (Pair<FileRecord, FileRecord> pair : table) {
            FileRecord left = pair.getLeft();
            FileRecord right = pair.getRight();
            if (left == null) {
                result.add(Triple.of(Style.LIGHT_GRAY, right.fullTitle(), null));
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

    public static Triple<Style, String, Object> compare(FileRecord left, FileRecord right, boolean diffOnly) {
        Style style = compare(left, right);
        if (diffOnly && style.equals(Style.DEFAULT)) {
            return null;
        } else {
            return Triple.of(style, left.fullTitle(), null);
        }
    }

    public static Style compare(FileRecord left, FileRecord right) {
        if (right == null || left.isError()) {
            return Style.RED;
        }
        int code = 0;
        if (!left.hash().equals(right.hash())) {
            code++;
        }
        if (left.size() != right.size()) {
            code++;
        }
        return switch (code) {
            case 1 -> Style.BLUE;
            case 2 -> Style.FUCHSIA;
            default -> Style.DEFAULT;
        };
    }

    private Path getGamePath(String fileName, boolean isUser) {
        return isUser ? FileUtils.getUserFile(fileName) : FileUtils.getBaseGamesFile(fileName);
    }

    public void backButtonClick() {
        showLists();

        leftListView.scrollTo(0);
        rightListView.scrollTo(0);
        backButton.setDisable(true);
        compareButton.setDisable(false);
        similarButton.setDisable(false);
    }

    public void closeButtonClick(ActionEvent actionEvent) {
        JavaFxUtils.closeStage(actionEvent);
    }

    @Override
    public void close() throws IOException {
    }
}
