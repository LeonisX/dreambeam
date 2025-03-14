package md.leonis.dreambeam.view;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import md.leonis.dreambeam.model.ListViewHandler;
import md.leonis.dreambeam.model.Pair;
import md.leonis.dreambeam.model.Triple;
import md.leonis.dreambeam.model.enums.Style;
import md.leonis.dreambeam.statik.Storage;
import md.leonis.dreambeam.utils.JavaFxUtils;
import md.leonis.dreambeam.utils.Utils;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static md.leonis.dreambeam.statik.Config.str;

public class StatsStageController implements Closeable {

    public Button closeButton;

    public CheckBox differenceCheckBox;

    public ListView<Triple<Style, String, Object>> leftListView;
    public ListView<Triple<Style, String, Object>> rightListView;
    public Label userLabel;
    public Label baseLabel;
    public RadioButton allRadioButton;
    public RadioButton rusRadioButton;
    public RadioButton usaRadioButton;
    public RadioButton eurRadioButton;
    public RadioButton japRadioButton;
    public RadioButton homeRadioButton;
    public RadioButton gdiRadioButton;
    public ToggleGroup filterToggleGroup;

    private volatile List<String> userGames;
    private volatile List<String> baseGames;
    private volatile Set<String> userFilteredGames;
    private volatile Set<String> baseFilteredGames;

    @FXML
    private void initialize() {
        allRadioButton.setUserData("all");
        rusRadioButton.setUserData("rus");
        usaRadioButton.setUserData("usa");
        eurRadioButton.setUserData("eur");
        japRadioButton.setUserData("jap");
        homeRadioButton.setUserData("home");
        gdiRadioButton.setUserData("gdi");

        MainStageController.calculateUserHashes(true, false);

        baseGames = Storage.baseHashes.values().stream().sorted().toList();
        userGames = Storage.userHashes.values().stream().sorted().toList();

        leftListView.setCellFactory(Utils::colorLines2);
        rightListView.setCellFactory(Utils::colorLines2);

        differenceCheckBox.selectedProperty().addListener((obs, oldValue, newValue) -> compare());

        filterToggleGroup.selectedToggleProperty().addListener((group, oldToggle, newToggle) -> compare());

        compare();
    }

    public void compare() {
        String text;
        String filter;

        switch (filterToggleGroup.getSelectedToggle().getUserData().toString()) {
            case "all" -> {
                text = "";
                filter = "";
            }
            case "rus" -> {
                text = String.format(" %s", str("stats.filter.russian"));
                filter = "(Rus)";
            }
            case "usa" -> {
                text = String.format(" %s", str("stats.filter.usa"));
                filter = "NTSC-U";
            }
            case "eur" -> {
                text = String.format(" %s", str("stats.filter.eur"));
                filter = "PAL-E";
            }
            case "jap" -> {
                text = String.format(" %s", str("stats.filter.japan"));
                filter = "NTSC-J";
            }
            case "home" -> {
                text = String.format(" %s", str("stats.filter.homebrew"));
                filter = "(Homebrew)";
            }
            case "gdi" -> {
                text = String.format(" %s", str("stats.filter.gdi"));
                filter = "(GDI)";
            }
            default -> {
                text = str("stats.filter.fix.it");
                filter = "";
            }
        }

        userFilteredGames = userGames.stream().filter(g -> g.contains(filter)).collect(Collectors.toSet());
        baseFilteredGames = baseGames.stream().filter(g -> g.contains(filter)).collect(Collectors.toSet());

        List<Pair<String, String>> table1 = withNullsList(userFilteredGames, baseFilteredGames);
        List<Pair<String, String>> table2 = withNullsList(baseFilteredGames, userFilteredGames);

        List<Triple<Style, String, Object>> leftLines = withNullsList(table1, differenceCheckBox.isSelected());
        List<Triple<Style, String, Object>> rightLines = withNullsList(table2, differenceCheckBox.isSelected());

        leftListView.setItems(FXCollections.observableList(Objects.requireNonNull(leftLines)));
        rightListView.setItems(FXCollections.observableList(Objects.requireNonNull(rightLines)));

        var leftHandler = new ListViewHandler(leftListView, rightListView);
        leftListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> leftHandler.sync(newValue));
        leftListView.setOnKeyPressed(leftHandler::handle);

        var rightHandler = new ListViewHandler(rightListView, leftListView);
        rightListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> rightHandler.sync(newValue));
        rightListView.setOnKeyPressed(rightHandler::handle);

        long baseBest = baseFilteredGames.stream().filter(g -> g.contains("[!]")).count();
        int userUnique = gerUserUniqueGamesCount();

        baseLabel.setText(String.format(str("stats.base.disks.stats"), baseFilteredGames.size(), text, baseBest));
        userLabel.setText(String.format(str("stats.user.disks.stats"),
                userFilteredGames.size(), text, userUnique, userFilteredGames.size() - userUnique));
    }

    public static List<Pair<String, String>> withNullsList(Set<String> left, Set<String> right) {
        return Stream.concat(left.stream(), right.stream()).distinct().sorted()
                .map(t -> Pair.of(left.contains(t) ? t : null, right.contains(t) ? t : null)).toList();
    }

    private int gerUserUniqueGamesCount() {
        var list = new ArrayList<>(userFilteredGames);
        list.removeAll(baseFilteredGames);
        return list.size();
    }

    public static List<Triple<Style, String, Object>> withNullsList(List<Pair<String, String>> table, boolean diffOnly) {
        List<Triple<Style, String, Object>> result = new ArrayList<>();

        for (Pair<String, String> stringStringPair : table) {
            String left = stringStringPair.getLeft();
            String right = stringStringPair.getRight();
            if (left == null) {
                if (!diffOnly) {
                    result.add(Triple.of(Style.LIGHT_GRAY, right, null));
                }
            } else {
                if (!diffOnly || !left.equals(right)) {
                    result.add(checkStatus(left));
                }
            }
        }

        return result.stream().filter(Objects::nonNull).toList();
    }

    public static Triple<Style, String, Object> checkStatus(String title) {
        Style style = (title.contains("(Bad")) ? Style.RED : Style.DEFAULT;
        return Triple.of(style, title, null);
    }

    public void closeButtonClick(ActionEvent actionEvent) {
        JavaFxUtils.closeStage(actionEvent);
    }

    @Override
    public void close() throws IOException {
    }
}
