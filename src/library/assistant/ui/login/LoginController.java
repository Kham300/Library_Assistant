package library.assistant.ui.login;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.settings.PreferenceWrapper;
import library.assistant.ui.addBook.LibraryAssistant;
import library.assistant.ui.main.MainController;
import library.assistant.utils.LibraryAssistantUtils;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController implements Initializable {

    @FXML
    private JFXTextField login;
    @FXML
    private JFXPasswordField password;

    PreferenceWrapper preferenceWrapper;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        preferenceWrapper = PreferenceWrapper.getPreference();
    }
    @FXML
    void handleCancelButtonAction() {
        System.exit(0);
    }

    @FXML
    void handleLoginButtonAction() {
        String uname = login.getText();
        String upass = DigestUtils.shaHex(password.getText());

        if (uname.equals(preferenceWrapper.getUserName())&&upass.equals(preferenceWrapper.getPassword())){
            //log in
            closeStage();
            loadMain();
        } else {
            login.getStyleClass().add("wrong-credentials");
            password.getStyleClass().add("wrong-credentials");
        }
    }

    private void closeStage() {
        ((Stage)login.getScene().getWindow()).close();
    }

    void loadMain(){
        try {
            Parent parent = FXMLLoader.load(getClass().getResource("/library/assistant/ui/main/main.fxml"));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Library Assistant");
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryAssistantUtils.setStageIcon(stage);
        } catch (IOException e) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

}
