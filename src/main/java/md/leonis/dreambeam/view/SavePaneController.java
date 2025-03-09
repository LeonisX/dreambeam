package md.leonis.dreambeam.view;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import md.leonis.dreambeam.model.DiskImage;
import md.leonis.dreambeam.statik.Config;
import md.leonis.dreambeam.statik.Storage;
import md.leonis.dreambeam.utils.FileUtils;
import md.leonis.dreambeam.utils.JavaFxUtils;
import md.leonis.dreambeam.utils.StringUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static md.leonis.dreambeam.statik.Config.str;
import static md.leonis.dreambeam.statik.Config.strError;

public class SavePaneController implements Closeable {

    public Button closeButton;
    public HBox okHBox;
    public HBox nokHBox;
    public Button saveButton;
    public TextField titleTextField;
    public Button runWizardButton;
    public Button findSimilarButton;
    public Label okLabel;
    public TableView<Diff> similarTableView;
    public TableColumn<Diff, String> titleTableColumn;
    public TableColumn<Diff, String> diffTableColumn;
    public TableColumn<Diff, Source> sourceTableColumn;

    private String name;
    private boolean recognized;

    @FXML
    private void initialize() {
        name = Storage.baseHashes.get(Storage.diskImage.getCrc32());
        recognized = (name != null);
        boolean recognizedUser = false;

        if (!recognized) {
            name = Storage.userHashes.get(Storage.diskImage.getCrc32());
            recognized = (name != null);
            recognizedUser = (name != null);
        }

        if (recognized) {
            titleTextField.setText(name);
        }

        if (recognizedUser) {
            JavaFxUtils.log(String.format("@%s %s %s", str("save.disk.recognized.as"), name, str("save.user.database")));
        } else if (recognized) {
            JavaFxUtils.log(String.format("@%s %s", str("save.disk.recognized.as"), name));
        } else {
            JavaFxUtils.log(String.format("#%s", str("save.unknown.disk")));
        }

        saveButton.setDisable(!recognized);
        nokHBox.setVisible(!recognized);
        okLabel.setText(recognizedUser ? str("save.user.disk.recognized") : str("save.base.disk.recognized"));
        okHBox.setVisible(recognized);

        titleTextField.textProperty().addListener((obs, old, nev) -> {
            name = nev;
            saveButton.setDisable(StringUtils.isBlank(titleTextField.getText()));
        });

        similarTableView.getSortOrder().add(diffTableColumn);
        similarTableView.getSelectionModel().selectedItemProperty()
                .addListener((obs, oldValue, newValue) -> titleTextField.setText(newValue.title));

        titleTableColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(cellData.getValue().title()));
        diffTableColumn.setCellValueFactory(cellData -> new ReadOnlyStringWrapper(String.format("%.2f%%", cellData.getValue().diff())));
        sourceTableColumn.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().source()));

        if (Storage.diskImage.isError()) {
            findSimilarButtonClick();
        }
    }

    public void closeButtonClick() {
        JavaFxUtils.showPrimaryPanel();
    }

    public void saveButtonClick() {
        if (FileUtils.exists(FileUtils.getBaseGamesFile(name)) && !recognized) {
            JavaFxUtils.showAlert(str("save.base.image.exists"), str("save.alt.version"), String.format("%s, %s (Alt)", str("save.for.example"), name), Alert.AlertType.WARNING);

        } else if (FileUtils.exists(FileUtils.getUserFile(name))) {
            var buttonType = JavaFxUtils.showConfirmation(str("save.user.image.exists"), str("save.overwrite"), name);
            if (buttonType.isPresent() && buttonType.get().equals(ButtonType.OK)) {
                logSave();
                saveAndClose();
            }

        } else {
            saveToBase();
            logSave();
            saveAndClose();
        }
    }

    private void logSave() {
        JavaFxUtils.log(str("save.image.saved.log"));
        JavaFxUtils.log(Storage.HR);
    }

    private void saveToBase() {
        try {
            if (Config.admin) {
                Storage.saveUserFileToBase(name);
            }
        } catch (Exception e) {
            showFileAlert(e, name);
        }
    }

    private void saveAndClose() {
        try {
            Storage.saveUserFile(name);
            JavaFxUtils.showPrimaryPanel();
        } catch (Exception e) {
            showFileAlert(e, name);
        }
    }

    private void showFileAlert(Exception e, String name) {
        JavaFxUtils.showAlert(strError(), String.format("%s: %s", str("save.file.save.error"), name), e.getClass().getSimpleName() + ": " + e.getMessage(), Alert.AlertType.ERROR);
    }

    public void runWizardButtonClick() {
        Storage.wizardName = null;
        JavaFxUtils.showWizardWindow();
        Thread thread = new Thread(() -> {
            while (Storage.wizardName == null) {
                sleep();
            }
            if (StringUtils.isNotBlank(Storage.wizardName)) {
                titleTextField.setText(Storage.wizardName);
                JavaFxUtils.log(str("save.image.name.log"));
                JavaFxUtils.log(Storage.wizardName);
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    public void findSimilarButtonClick() {
        List<Diff> diffList = calculateDiffPoints(Storage.baseFiles.values(), Storage.baseHashes, Source.BASE);
        diffList.addAll(calculateDiffPoints(Storage.userFiles.values().stream().filter(f -> !Storage.baseHashes.containsKey(f.getCrc32())).toList(), Storage.userHashes, Source.USER));

        SortedList<Diff> sortedData = new SortedList<>(FXCollections.observableList(diffList));
        sortedData.comparatorProperty().bind(similarTableView.comparatorProperty());
        similarTableView.setItems(sortedData);

        if (diffList.isEmpty()) {
            JavaFxUtils.showAlert("", "Something unique...", "", Alert.AlertType.INFORMATION);
        }
    }

    public List<Diff> calculateDiffPoints(Collection<DiskImage> files, Map<String, String> hashes, Source source) {
        return files.parallelStream().map(diskImage ->
                new Diff(diskImage.getCrc32(), hashes.get(diskImage.getCrc32()), Storage.diskImage.calculateDiffPoints(diskImage), source))
                .filter(r -> r.diff() > 0).sorted((d1, d2) -> Double.compare(d2.diff(), d1.diff())).collect(Collectors.toList());
    }

    private void sleep() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException ignored) {
        }
    }

    @Override
    public void close() throws IOException {
    }

    public record Diff(String crc32, String title, double diff, Source source) {
    }

    public enum Source {
        BASE, USER
    }
}
