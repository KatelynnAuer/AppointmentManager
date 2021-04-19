package controllers;

import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Translatable parent class
 * @author Katelynn Auer
 */
public class TranslatableScreen extends ControlledScreen{
    private ResourceBundle languageBundle = ResourceBundle.getBundle("locale");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void translate(Button button, String string) {
        button.setText(this.languageBundle.getString(string));
    }

    public void translate(Label label, String string) {
        label.setText(this.languageBundle.getString(string));
    }
    public void translate(TableColumn tableColumn, String string) {
        tableColumn.setText(this.languageBundle.getString(string));
    }

    public void translate(TextField textField, String string) {
        textField.setPromptText(this.languageBundle.getString(string));;
    }

    public void translate(Tab t, String string) {
        t.setText(this.languageBundle.getString(string));;
    }

    public void translate(RadioButton r, String string) {
        r.setText(this.languageBundle.getString(string));;
    }

    public ResourceBundle language () {
    return this.languageBundle;
    }


}
