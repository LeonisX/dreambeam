package md.leonis.dreambeam.view;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import md.leonis.dreambeam.model.ListViewHandler;
import md.leonis.dreambeam.utils.*;
import org.apache.commons.lang3.StringUtils;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BaseStageController implements Closeable {

    public Button closeButton;
    public ListView<String> gamesListView;
    public TextArea textArea;

    private String prevText = "";
    private int prevListIndex = 0;
    private List<String> list;
    private final List<String> hashes = new ArrayList<>();

    @FXML
    private void initialize() {
        JavaFxUtils.currentStage.setOnHiding(e -> showText(""));

        gamesListView.setCellFactory(Utils::colorLines);
        gamesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> showText(newValue));

        //навигация набором букв
        var handler = new ListViewHandler<>(gamesListView);
        gamesListView.setOnKeyPressed(handler::handle);

        ServiceUtils.calculateUserHashes(true);
        ServiceUtils.loadTexts();

        showGames();
        gamesListView.getSelectionModel().selectFirst();
    }

    private void showText(String line) {
        saveText(!getTextAreaText().equals(prevText));

        prevListIndex = gamesListView.getSelectionModel().getSelectedIndex();
        if (line.startsWith("+")) {
            String hash = hashes.get(prevListIndex);
            textArea.setText(Config.textMap.get(hash));
            prevText = Config.textMap.get(hash);
        } else {
            textArea.clear();
            prevText = "";
        }
    }

    private String getTextAreaText() {
        return StringUtils.isBlank(textArea.getText()) ? "" : textArea.getText();
    }

    private void saveText(boolean needToSave) {
        if (needToSave) {
            String hash = hashes.get(prevListIndex);
            Path path = Config.getTextFile(hash).normalize().toAbsolutePath();
            if (StringUtils.isNotBlank(textArea.getText())) {
                try {
                    Config.textMap.put(hash, textArea.getText());
                    FileUtils.writeToRussianFile(path, textArea.getText());
                } catch (IOException e) {
                    JavaFxUtils.showAlert("Ошибка!", "Не удалось сохранить текст в файл " + path, e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
                }
            } else {
                Config.textMap.remove(hash);
                FileUtils.deleteSilently(path);
            }

            String prevValue = list.get(prevListIndex);
            if (prevValue.startsWith("+")) {
                prevValue = prevValue.substring(1);
            }
            if (hasText(hash)) {
                list.set(prevListIndex, addTextMark(prevValue));
            } else {
                list.set(prevListIndex, prevValue);
            }
        }
    }

    private void showGames() {
        textArea.clear();
        list = Config.baseHashes.entrySet().stream().sorted(Map.Entry.comparingByValue()).map(e -> {
            hashes.add(e.getKey());
            return addTextMark(e);
        }).collect(Collectors.toList());
        gamesListView.setItems(FXCollections.observableList(list));
    }

    private String addTextMark(Map.Entry<String, String> entry) {
        return hasText(entry.getKey()) ? addTextMark(entry.getValue()) : entry.getValue();
    }

    private String addTextMark(String value) {
        return '+' + value;
    }

    private boolean hasText(String hash) {
        return Config.textMap.containsKey(hash);
    }

    public void closeButtonClick(ActionEvent actionEvent) {
        saveText(true);
        Node source = (Node) actionEvent.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @Override
    public void close() throws IOException {
    }
}
