package library.assistant.settings;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {


    @FXML
    private JFXTextField nDaysWithoutFine;
    @FXML
    private JFXTextField finePerDay;
    @FXML
    private JFXTextField userName;
    @FXML
    private JFXPasswordField password;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initDefaultValues();
    }

    private void initDefaultValues() {
        PreferenceWrapper preferenceWrapper = PreferenceWrapper.getPreference();
        nDaysWithoutFine.setText(String.valueOf(preferenceWrapper.getnDaysWithoutFine()));
        finePerDay.setText(String.valueOf(preferenceWrapper.getFinePerDay()));
        userName.setText(String.valueOf(preferenceWrapper.getUserName()));
        password.setText(String.valueOf(preferenceWrapper.getPassword()));
    }

    @FXML
    void handleCancelButtonAction() {
        ((Stage)nDaysWithoutFine.getScene().getWindow()).close();
    }

    @FXML
    void handleSaveButtonAction() {
        int nDays = Integer.parseInt(nDaysWithoutFine.getText());
        float fine = Float.parseFloat(finePerDay.getText());
        String username = userName.getText();
        String pass = password.getText();

        PreferenceWrapper preferenceWrapper = PreferenceWrapper.getPreference();
        preferenceWrapper.setnDaysWithoutFine(nDays);
        preferenceWrapper.setFinePerDay(fine);
        preferenceWrapper.setUserName(username);
        preferenceWrapper.setPassword(pass);

        PreferenceWrapper.writePreferencesToFile(preferenceWrapper);
    }
}
