package md.leonis.dreambeam.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import md.leonis.dreambeam.statik.Storage;
import md.leonis.dreambeam.utils.FileUtils;
import md.leonis.dreambeam.utils.JavaFxUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static md.leonis.dreambeam.statik.Config.str;
import static md.leonis.dreambeam.statik.Config.strError;

public class AuditStageController implements Closeable {

    public Button closeButton;
    public Button userUniqueButton;
    public Button userDuplicatesButton;
    public Button userTitlesButton;
    public Button renameButton;
    public Button baseDuplicatesButton;
    public Button textsButton;
    public Button reloadFilesButton;

    public TextArea textArea;

    Map<String, String> oldTitlesMap = new HashMap<>();

    @FXML
    private void initialize() {
        reloadFilesButtonClick();
    }

    public void userUniqueButtonClick() {
        var uniqueGames = getUserUniqueGames();
        uniqueGames.add(0, String.format(str("audit.user.unique.records"), uniqueGames.size()));
        uniqueGames.add(1, "");
        textArea.setText(String.join("\n", uniqueGames));
    }

    private List<String> getUserUniqueGames() {
        var list = new ArrayList<>(Storage.userHashes.values());
        list.removeAll(Storage.baseHashes.values());
        return list.stream().sorted().collect(Collectors.toList());
    }

    public void userDuplicatesButtonClick() {
        var duplicates = Storage.userDuplicates.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .map(e -> String.format("%s == %s", e.getKey(), e.getValue())).collect(Collectors.toList());
        duplicates.add(0, String.format(str("audit.user.duplicates"), duplicates.size()));
        duplicates.add(1, "");
        textArea.setText(String.join("\n", duplicates));
    }

    public void userTitlesButtonClick() {
        oldTitlesMap = new HashMap<>();
        Storage.userHashes.forEach((key, value) -> {
            String baseTitle = Storage.baseHashes.get(key);
            if (baseTitle != null && !baseTitle.equals(value)) {
                oldTitlesMap.put(baseTitle, value);
            }
        });
        List<String> lines = oldTitlesMap.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .map(e -> String.format(str("audit.texts.comparison"), e.getValue() , e.getKey())).collect(Collectors.toList());
        lines.add(0, String.format(str("audit.user.old.titles"), oldTitlesMap.size()));
        lines.add(1, "");
        textArea.setText(String.join("\n", lines));

        renameButton.setDisable(false);
    }

    public void baseDuplicatesButtonClick() {
        var duplicates = Storage.baseDuplicates.entrySet().stream().sorted(Map.Entry.comparingByKey())
                .map(e -> String.format("%s == %s", e.getKey(), e.getValue())).collect(Collectors.toList());
        duplicates.add(0, String.format(str("audit.base.duplicates"), duplicates.size()));
        duplicates.add(1, "");
        textArea.setText(String.join("\n", duplicates));
    }

    public void textsButtonClick() {
        MainStageController.loadTexts();
        List<String> lines = new ArrayList<>();
        Storage.textMap.forEach((key, value) -> {
            if (!Storage.baseHashes.containsKey(key)) {
                lines.add(key);
            }
        });
        lines.add(0, String.format(str("audit.base.unused.texts"), lines.size()));
        lines.add(1, "");
        textArea.setText(String.join("\n", lines));
    }

    public void reloadFilesButtonClick() {
        MainStageController.calculateUserHashes(false, true);
        MainStageController.calculateBaseHashes(false, true);
    }

    public void closeButtonClick(ActionEvent actionEvent) {
        JavaFxUtils.closeStage(actionEvent);
    }

    @Override
    public void close() throws IOException {
    }

    public void renameButtonClick() {
        renameButton.setDisable(true);
        try {
            for (Map.Entry<String, String> entry : oldTitlesMap.entrySet()) {
                FileUtils.renameFile(FileUtils.getUserFile(entry.getValue()), FileUtils.getUserFile(entry.getKey()));
                JavaFxUtils.log(String.format(str("audit.renamed"), entry.getValue(), entry.getKey()));
            }
        } catch (Exception e) {
            JavaFxUtils.showAlert(strError(), str("audit.rename.error"),
                    e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
