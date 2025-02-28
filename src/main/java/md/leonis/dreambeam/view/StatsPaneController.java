package md.leonis.dreambeam.view;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import md.leonis.dreambeam.model.Game;
import md.leonis.dreambeam.model.enums.CompareStatus;
import md.leonis.dreambeam.utils.Config;
import md.leonis.dreambeam.utils.JavaFxUtils;
import md.leonis.dreambeam.utils.Utils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class StatsPaneController implements Closeable {

    public Button closeButton;

    public CheckBox differenceCheckBox;

    public ListView<String> leftListView;
    public ListView<String> rightListView;

    private volatile List<String> userGames;
    private volatile List<String> baseGames;

    @FXML
    private void initialize() {
        calculateUserHashes();

        baseGames = Config.baseHashes.values().stream().sorted().toList();
        userGames = Config.userHashes.values().stream().sorted().toList();

        leftListView.setCellFactory(Utils::colorLines);
        rightListView.setCellFactory(Utils::colorLines);

        differenceCheckBox.selectedProperty().addListener((obs, oldValue, newValue) -> compare());

        compare();

        //todo тут с самого начала надо добавлять фантомы
        //так же 2 режима - всё и только разница
        //плохие подсвечивать красным
        //фильтры по языкам (и регионам) (Rus), (NTSC-U), (PAL-E), (NTSC-J), (Homebrew), (GDI)
        //Для гди - наверно отдельный чекббокс
        //на перспективу надо помнить левые образы и иметь возможность убирать их из статистики.
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
            JavaFxUtils.showAlert("Ошибка!", "В вашей базе данных есть дубликаты!",
                    duplicates.entrySet().stream().map(e -> e.getKey() + " == " + e.getValue()).collect(Collectors.joining("\n")), Alert.AlertType.WARNING);
        }
    }

    public void compare() {
        List<String> leftLines = userGames;
        List<String> rightLines = baseGames;
        Map<String, Game> leftGames = mapGamesList(leftLines);
        Map<String, Game> rightGames = mapGamesList(rightLines);

        leftLines = mapGamesListFull(leftGames, rightGames, differenceCheckBox.isSelected());
        rightLines = mapGamesListFull(rightGames, leftGames, differenceCheckBox.isSelected());

        leftListView.setItems(FXCollections.observableList(Objects.requireNonNull(leftLines)));
        rightListView.setItems(FXCollections.observableList(Objects.requireNonNull(rightLines)));
    }

    private Map<String, Game> mapGamesList(List<String> lines) {
        return lines.subList(1, lines.size() - 1).stream().map(Game::parseLine)
                .collect(Collectors.toMap(Game::title, Function.identity(), (v1, v2) -> v1, LinkedHashMap::new));
    }

    public static List<String> mapGamesListFull(Map<String, Game> games1, Map<String, Game> games2, boolean diffOnly) {
        List<Game> list1 = new ArrayList<>(games1.values());
        List<Game> list2 = new ArrayList<>(games2.values());

        //1. найти общие
        List<Game> list1nulls = withNullsList(games1, list2);
        List<Game> list2nulls = withNullsList(games2, list1);

        List<String> result = new ArrayList<>();

        for (int i = 0; i < list1nulls.size(); i++) {
            Game game1 = list1nulls.get(i);
            Game game2 = list2nulls.get(i);
            if (game1 == null) {
                result.add("~" + game2.fullTitle());
            } else {
                result.add(compare(game1, game2, diffOnly));
            }
        }

        return result.stream().filter(Objects::nonNull).toList();
    }

    public static List<Game> withNullsList(Map<String, Game> games1, List<Game> list2) {
        List<Game> list1 = new ArrayList<>(games1.values());
        List<Game> result = new ArrayList<>(list1);
        List<Pair<Game, Boolean>> common2 = list2.stream().map(g2 -> {
            Game game1 = games1.get(g2.title());
            String title = game1 == null ? null : game1.title();
            return Pair.of(g2, g2.title().equals(title));
        }).toList();

        //2. добавить между ними оставшиеся
        int index2 = 0;
        int index1 = 0;

        for (int i = 0; i < common2.size(); i++) {
            var pair = common2.get(i);
            if (pair.getRight()) {
                index1 = index(list1, pair.getLeft().title(), index1);
                for (int j = 0; j < i - index2; j++) {
                    result.add(index1, null);
                }
                index1++;
                index2 = i + 1;
            }
        }

        if (index2 < list2.size()) {
            for (int j = index2; j < list2.size(); j++) {
                result.add(null);
            }
        }

        return result;
    }

    public static int index(List<Game> list, String title, int index) {
        for (int i = index; i < list.size(); i++) {
            if (list.get(i).title().equals(title)) {
                return i;
            }
        }
        throw new IllegalStateException();
    }

    public static String compare(Game game1, Game game2, boolean diffOnly) {
        CompareStatus status = compare(game1, game2);
        if (diffOnly && status.equals(CompareStatus.EQUALS)) {
            return null;
        } else {
            return status.getMarker() + game1.fullTitle();
        }
    }

    public static CompareStatus compare(Game game1, Game game2) {
        if (game2 == null) {
            return CompareStatus.ABSENT;
        }
        if (game1.isError()) {
            return CompareStatus.ERROR;
        }
        int code = 0;
        if (!game1.hash().equals(game2.hash())) {
            code++;
        }
        if (game1.size() != game2.size()) {
            code++;
        }
        return CompareStatus.values()[code];
    }

    private Path getGamePath(String fileName, boolean isUser) {
        return isUser ? Config.getUserFile(fileName) : Config.getBaseGamesFile(fileName);
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
