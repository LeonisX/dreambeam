package md.leonis.dreambeam.view;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import md.leonis.dreambeam.model.ListViewHandler;
import md.leonis.dreambeam.statik.Storage;
import md.leonis.dreambeam.utils.JavaFxUtils;
import md.leonis.dreambeam.utils.StringUtils;
import md.leonis.dreambeam.utils.Utils;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WizardStageController implements Closeable {

    public ListView<String> gamesListView;
    public RadioButton rusRadioButton;
    public RadioButton japRadioButton;
    public RadioButton gerRadioButton;
    public RadioButton multiRadioButton;
    public RadioButton engRadioButton;
    public RadioButton freRadioButton;
    public RadioButton spaRadioButton;

    public TitledPane translatorTitledPane;
    public RadioButton kudosRadioButton;
    public RadioButton vectorRadioButton;
    public RadioButton rgrRadioButton;
    public RadioButton paradoxRadioButton;
    public RadioButton studiaMaxRadioButton;
    public RadioButton redStationRadioButton;
    public RadioButton pinachetRadioButton;
    public RadioButton unknownTranslatorRadioButton;
    public RadioButton otherTranslatorRadioButton;
    public TextField translatorTextField;

    public TitledPane regionTitledPane;
    public RadioButton palRadioButton;
    public RadioButton ntscuRadioButton;
    public RadioButton ntscjRadioButton;
    public RadioButton ntscPalRadioButton;
    public RadioButton unknownRegionRadioButton;
    public TextField regionTextField;
    public TextField publisherTextField;

    public Spinner<Integer> disksSpinner;
    public Label diskLabel;
    public Spinner<Integer> diskSpinner;
    public CheckBox homebrewCheckBox;
    public CheckBox gdiCheckBox;
    public CheckBox badDumpCheckBox;

    public TextField titleTextField;
    public TextField disksTextField;
    public TextField tagsTextField;
    public Button okButton;

    public ToggleGroup languageToggleGroup;
    public ToggleGroup translatorToggleGroup;
    public ToggleGroup regionToggleGroup;

    public SpinnerValueFactory<Integer> disksValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 16, 1);
    public SpinnerValueFactory<Integer> diskValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 16, 1);

    @FXML
    private void initialize() {
        rusRadioButton.setUserData("Rus");
        japRadioButton.setUserData("Jap");
        gerRadioButton.setUserData("Ger");
        multiRadioButton.setUserData("Multi");
        engRadioButton.setUserData("Eng");
        freRadioButton.setUserData("Fre");
        spaRadioButton.setUserData("Spa");

        kudosRadioButton.setUserData("Kudos");
        vectorRadioButton.setUserData("Vector");
        rgrRadioButton.setUserData("RGR");
        paradoxRadioButton.setUserData("Paradox");
        studiaMaxRadioButton.setUserData("Studia Max");
        redStationRadioButton.setUserData("Red Station");
        pinachetRadioButton.setUserData("Pinachet Game");
        unknownTranslatorRadioButton.setUserData("-");
        otherTranslatorRadioButton.setUserData("?");

        palRadioButton.setUserData("PAL-E");
        ntscuRadioButton.setUserData("NTSC-U");
        ntscjRadioButton.setUserData("NTSC-J");
        ntscPalRadioButton.setUserData("NTSC-U, PAL-E");
        unknownRegionRadioButton.setUserData("-");

        disksSpinner.setValueFactory(disksValueFactory);
        diskSpinner.setValueFactory(diskValueFactory);
        showDisks();

        //todo добавлять ещё свои свежие игры
        List<String> games = Utils.cleanAndSortGameNames(Storage.baseHashes.values());
        gamesListView.setItems(FXCollections.observableList(games));

        //todo было бы хорошо выводить предыдущие результаты
        languageToggleGroup.selectedToggleProperty().addListener((group, oldToggle, newToggle) -> languageToggleGroupListen());
        translatorToggleGroup.selectedToggleProperty().addListener((group, oldToggle, newToggle) -> updateTranslator());
        regionToggleGroup.selectedToggleProperty().addListener((group, oldToggle, newToggle) -> updateRegion());

        homebrewCheckBox.selectedProperty().addListener((obs, oldValue, newValue) -> updateTags());
        gdiCheckBox.selectedProperty().addListener((obs, oldValue, newValue) -> updateTags());

        disksSpinner.valueProperty().addListener((obs, oldValue, newValue) -> updateDisks());
        diskSpinner.valueProperty().addListener((obs, oldValue, newValue) -> updateDisks());

        gamesListView.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue)
                -> titleTextField.setText(gamesListView.getSelectionModel().getSelectedItem()));

        //навигация набором букв
        var handler = new ListViewHandler<>(gamesListView, null);
        gamesListView.setOnKeyPressed(handler::handle);

        badDumpCheckBox.setSelected(Storage.error);

        translatorTextField.textProperty().addListener((observable, oldValue, newValue) -> updateTags());
        regionTextField.textProperty().addListener((observable, oldValue, newValue) -> updateTags());
        publisherTextField.textProperty().addListener((observable, oldValue, newValue) -> updateTags());

        languageToggleGroupListen();
    }

    private void languageToggleGroupListen() {
        RadioButton button = (RadioButton) languageToggleGroup.getSelectedToggle();
        translatorTitledPane.setVisible(button.equals(rusRadioButton));
        regionTitledPane.setVisible(!button.equals(rusRadioButton));
        updateTags();
    }

    private void updateTranslator() {
        RadioButton translatorButton = (RadioButton) translatorToggleGroup.getSelectedToggle();
        if (!translatorButton.getUserData().equals("?")) {
            translatorTextField.setText(translatorButton.getUserData().toString());
        } else {
            translatorTextField.setText("");
        }

        updateTags();
    }

    private void updateRegion() {
        RadioButton regionButton = (RadioButton) regionToggleGroup.getSelectedToggle();
        regionTextField.setText(regionButton.getUserData().toString());

        updateTags();
    }

    private void updateTags() {
        List<String> tags = new ArrayList<>();
        RadioButton languageButton = (RadioButton) languageToggleGroup.getSelectedToggle();
        String language = languageButton.getUserData().toString();

        if (language.equals("Rus")) {
            tags.add(language);
            tags.add(translatorTextField.getText());
        } else {
            tags.add(regionTextField.getText());
            tags.add(language);
            tags.add(publisherTextField.getText());
        }

        if (homebrewCheckBox.isSelected()) {
            tags.add("Homebrew");
        }

        if (gdiCheckBox.isSelected()) {
            tags.add("GDI");
        }

        tagsTextField.setText(tags.stream().map(t -> "(" + t + ")").collect(Collectors.joining(" ")));
    }

    private void updateDisks() {
        showDisks();

        if (disksSpinner.getValue() > 0) {
            disksTextField.setText(String.format("(Disk %s of %s)", diskSpinner.getValue(), disksSpinner.getValue()));
        } else {
            disksTextField.setText("");
        }
    }

    private void showDisks() {
        diskLabel.setVisible(disksSpinner.getValue() > 1);
        diskSpinner.setVisible(disksSpinner.getValue() > 1);
    }

    public void okButtonClick(ActionEvent actionEvent) {
        Storage.wizardName = Stream.of(titleTextField, disksTextField, tagsTextField)
                .map(f -> f.getText().trim()).filter(StringUtils::isNotBlank)
                .collect(Collectors.joining(" "));
        close(actionEvent);
    }

    private void close(ActionEvent actionEvent) {
        JavaFxUtils.closeStage(actionEvent);
    }

    @Override
    public void close() throws IOException {
    }
}
