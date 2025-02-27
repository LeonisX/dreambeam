package md.leonis.dreambeam.view;

import javafx.fxml.FXML;
import javafx.scene.control.*;

public class WizardStageController {

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
    public TextField publisherTextField;
    public Spinner<Integer> disksSpinner;
    public Label diskLabel;
    public Spinner<Integer> diskSpinner;
    public CheckBox homebrewCheckBox;
    public CheckBox gdiCheckBox;
    public TextField titleTextField;
    public TextField disksTextField;
    public TextField tagsTextField;
    public Button okButton;
    public Button cancelButton;
    public ToggleGroup languageToggleGroup;
    public ToggleGroup translatorToggleGroup;
    public ToggleGroup regionToggleGroup;

    public SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 16, 1);

    @FXML
    private void initialize() {
        disksSpinner.setValueFactory(valueFactory);
        diskSpinner.setValueFactory(valueFactory);
        showDiscs();

        //todo было бы хорошо выводить предыдущие результаты
        languageToggleGroup.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> updateTags());
        translatorToggleGroup.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> updateTags());
        regionToggleGroup.selectedToggleProperty().addListener((ov, old_toggle, new_toggle) -> updateTags());

        homebrewCheckBox.selectedProperty().addListener((obs, oldValue, newValue) -> updateTags());
        gdiCheckBox.selectedProperty().addListener((obs, oldValue, newValue) -> updateTags());

        disksSpinner.valueProperty().addListener((obs, oldValue, newValue) -> updateDiscs());
        diskSpinner.valueProperty().addListener((obs, oldValue, newValue) -> updateDiscs());
    }

    private void updateTags() {
        //todo
    }

    private void updateDiscs() {
        showDiscs();
        //todo
    }

    private  void showDiscs() {
        diskLabel.setVisible(disksSpinner.getValue() > 1);
        diskSpinner.setVisible(disksSpinner.getValue() > 1);
    }
}
