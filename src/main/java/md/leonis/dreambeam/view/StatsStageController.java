package md.leonis.dreambeam.view;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import md.leonis.dreambeam.model.Pair;
import md.leonis.dreambeam.model.enums.CompareStatus;
import md.leonis.dreambeam.utils.Config;
import md.leonis.dreambeam.utils.ServiceUtils;
import md.leonis.dreambeam.utils.Utils;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StatsStageController implements Closeable {

    public Button closeButton;

    public CheckBox differenceCheckBox;

    public ListView<String> leftListView;
    public ListView<String> rightListView;
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
    private volatile List<String> userFilteredGames;
    private volatile List<String> baseFilteredGames;

    @FXML
    private void initialize() {
        allRadioButton.setUserData("all");
        rusRadioButton.setUserData("rus");
        usaRadioButton.setUserData("usa");
        eurRadioButton.setUserData("eur");
        japRadioButton.setUserData("jap");
        homeRadioButton.setUserData("home");
        gdiRadioButton.setUserData("gdi");
        ServiceUtils.calculateUserHashes(true);

        baseGames = Config.baseHashes.values().stream().sorted().toList();
        userGames = Config.userHashes.values().stream().sorted().toList();

        leftListView.setCellFactory(Utils::colorLines);
        rightListView.setCellFactory(Utils::colorLines);

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
                text = " русских";
                filter = "(Rus)";
            }
            case "usa" -> {
                text = " американских";
                filter = "NTSC-U";
            }
            case "eur" -> {
                text = " европейских";
                filter = "PAL-E";
            }
            case "jap" -> {
                text = " японских";
                filter = "NTSC-J";
            }
            case "home" -> {
                text = " homebrew";
                filter = "(Homebrew)";
            }
            case "gdi" -> {
                text = " GDI";
                filter = "(GDI)";
            }
            default -> {
                text = "Почини это!";
                filter = "";
            }
        }

        userFilteredGames = userGames.stream().filter(g -> g.contains(filter)).toList();
        baseFilteredGames = baseGames.stream().filter(g -> g.contains(filter)).toList();

        List<String> list1nulls = withNullsList(userFilteredGames, baseFilteredGames);
        List<String> list2nulls = withNullsList(baseFilteredGames, userFilteredGames);

        List<String> leftLines = mapGamesListFull(list1nulls, list2nulls, differenceCheckBox.isSelected());
        List<String> rightLines = mapGamesListFull(list2nulls, list1nulls, differenceCheckBox.isSelected()).stream().filter(g -> g.contains(filter)).toList();

        leftListView.setItems(FXCollections.observableList(Objects.requireNonNull(leftLines)));
        rightListView.setItems(FXCollections.observableList(Objects.requireNonNull(rightLines)));

        long baseBest = baseFilteredGames.stream().filter(g -> g.contains("[!]")).count();
        int userUnique = gerUserUniqueGamesCount();

        baseLabel.setText(String.format("В базе данных: %s%s дисков, %s проверены на 100%%", baseFilteredGames.size(), text, baseBest));
        userLabel.setText(String.format("У вас %s%s дисков, %s уникальных, %s входят в базу данных.",
                userFilteredGames.size(), text, userUnique, userFilteredGames.size() - userUnique));
    }

    private int gerUserUniqueGamesCount() {
        var list = new ArrayList<>(userFilteredGames);
        list.removeAll(baseFilteredGames);
        return list.size();
    }

    public static List<String> mapGamesListFull(List<String> games1, List<String> games2, boolean diffOnly) {
        List<String> result = new ArrayList<>();

        for (int i = 0; i < games1.size(); i++) {
            String game1 = games1.get(i);
            String game2 = games2.get(i);
            if (game1 == null) {
                if (!diffOnly) {
                    result.add("~" + game2);
                }
            } else {
                if (!diffOnly || !game1.equals(game2)) {
                    result.add(checkStatus(game1));
                }
            }
        }

        return result.stream().filter(Objects::nonNull).toList();
    }

    public static List<String> withNullsList(List<String> games1, List<String> games2) {
        List<String> result = new ArrayList<>(games1);
        List<Pair<String, Boolean>> common2 = games2.stream().map(g2 -> Pair.of(g2, result.contains(g2))).toList();

        int count = 0;

        for (Pair<String, Boolean> pair : common2) {
            if (pair.getRight()) {
                int index1 = result.indexOf(pair.getLeft());
                for (int j = 0; j < count; j++) {
                    result.add(index1, null);
                }
                count = 0;
            } else {
                count++;
            }
        }

        if (count > 0) {
            for (int j = 0; j < count; j++) {
                result.add(null);
            }
        }

        return result;
    }

    public static String checkStatus(String game1) {
        CompareStatus status = (game1.contains("(Bad")) ? CompareStatus.ERROR : CompareStatus.EQUALS;
            return status.getMarker() + game1;
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
