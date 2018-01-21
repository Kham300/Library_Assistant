package library.assistant.ui.addMember;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import library.assistant.database.DBHandler;

import java.net.URL;
import java.util.ResourceBundle;

public class MemberAddController implements Initializable{
        DBHandler dbHandler;

        @FXML
        private AnchorPane rootPane;
        @FXML
        private JFXTextField name;
        @FXML
        private JFXTextField id;
        @FXML
        private JFXTextField mobile;
        @FXML
        private JFXTextField email;
        @FXML
        private JFXButton saveButton;
        @FXML
        private JFXButton cancelButton;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbHandler = DBHandler.getInstance();
    }


    @FXML
    void addMember(ActionEvent event) {
        String mName = name.getText();
        String mId = id.getText();
        String mMobile = mobile.getText();
        String mEmail = email.getText();

        Boolean flag = mName.isEmpty()||mId.isEmpty()||mMobile.isEmpty()||mEmail.isEmpty();
        if (flag){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Enter in all fields");
            alert.showAndWait();
            return;
        }

        String st = "INSERT INTO MEMBER VALUES ("+
                "'"+ mId +"',"+
                "'"+ mName +"',"+
                "'"+ mMobile +"',"+
                "'"+ mEmail +"'"+
                ")";
        System.out.println(st);
        if (dbHandler.executAction(st)){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Saved");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Failed");
            alert.showAndWait();
        }
    }

    @FXML
    void cancel(ActionEvent event) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

}
