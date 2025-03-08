package md.leonis.dreambeam.view;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import md.leonis.dreambeam.model.ListViewHandler;
import md.leonis.dreambeam.statik.Storage;
import md.leonis.dreambeam.utils.*;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static md.leonis.dreambeam.statik.Config.str;
import static md.leonis.dreambeam.statik.Config.strError;

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
        var handler = new ListViewHandler<>(gamesListView, null);
        gamesListView.setOnKeyPressed(handler::handle);

        ServiceUtils.loadTexts();

        showGames();
        gamesListView.getSelectionModel().selectFirst();
    }

    private void showText(String line) {
        saveText(!getTextAreaText().equals(prevText));

        prevListIndex = gamesListView.getSelectionModel().getSelectedIndex();
        if (line.startsWith("+")) {
            String hash = hashes.get(prevListIndex);
            textArea.setText(Storage.textMap.get(hash));
            prevText = Storage.textMap.get(hash);
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
            Path path = FileUtils.getTextFile(hash).normalize().toAbsolutePath();
            if (StringUtils.isNotBlank(textArea.getText())) {
                try {
                    Storage.textMap.put(hash, textArea.getText());
                    FileUtils.writeToRussianFile(path, textArea.getText());
                } catch (IOException e) {
                    JavaFxUtils.showAlert(strError(), String.format(str("base.error.save.text"), path), e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
                }
            } else {
                Storage.textMap.remove(hash);
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
        list = Storage.baseHashes.entrySet().stream().sorted(Map.Entry.comparingByValue()).map(e -> {
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
        return Storage.textMap.containsKey(hash);
    }

    public void closeButtonClick(ActionEvent actionEvent) {
        saveText(true);
        JavaFxUtils.closeStage(actionEvent);
    }

    @Override
    public void close() throws IOException {
    }
}
