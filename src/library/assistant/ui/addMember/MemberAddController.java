package library.assistant.ui.addMember;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import library.assistant.alert.AlertMaker;
import library.assistant.database.DBHandler;
import library.assistant.ui.memberList.MemberListController;

import java.net.URL;
import java.util.ResourceBundle;

import static library.assistant.ui.memberList.MemberListController.*;

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

        private Boolean isInEditMode = Boolean.FALSE;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbHandler = DBHandler.getInstance();
    }


    @FXML
    void addMember() {
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

        if (isInEditMode){
            handleUpdateMember();
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

    private void handleUpdateMember() {
        Member member = new Member(name.getText(), id.getText(), mobile.getText(), email.getText());
        if(dbHandler.updateMember(member)){
            AlertMaker.showSimpleAlert("Success", "Member is updated");
        } else {
            AlertMaker.showErrorMessage("Failed", "Can't update member");
        }
    }

    @FXML
    void cancel() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    public void inFlateUI(Member member){

        name.setText(member.getName());
        id.setText(member.getId());
        id.setEditable(false);
        mobile.setText(member.getMobile());
        email.setText(member.getEmail());

        isInEditMode = Boolean.TRUE;
    }

}
