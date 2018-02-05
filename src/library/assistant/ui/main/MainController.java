package library.assistant.ui.main;

import com.jfoenix.controls.JFXTextField;
import com.jfoenix.effects.JFXDepthManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.database.DBHandler;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MainController implements Initializable {

    private final String ADDMEMBER_FXML = "/library/assistant/ui/addMember/memberAdd.fxml";
    private final String ADDBOOK_FXML = "/library/assistant/ui/addBook/add_book.fxml";
    private final String LISTBOOK_FXML = "/library/assistant/ui/listbook/book_list.fxml";
    private final String LISTMEMBER_FXML = "/library/assistant/ui/memberList/member_list.fxml";
    private final String SETTINGS_FXML = "/library/assistant/settings/settings.fxml";

    @FXML
    private TextField bookIDInput;
    @FXML
    private Text bookName;
    @FXML
    private Text author;
    @FXML
    private Text status;
    @FXML
    private HBox member_Info;
    @FXML
    private HBox book_Info;
    @FXML
    private TextField memberIDInput;
    @FXML
    private Text memberName;
    @FXML
    private Text memberContact;
    @FXML
    private JFXTextField aboutBookID;
    @FXML
    private ListView<String> issueDataList;

    Boolean isReadyForSubmission = false;

    DBHandler dbHandler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        JFXDepthManager.setDepth(book_Info, 1);
        JFXDepthManager.setDepth(member_Info, 1);

        dbHandler = DBHandler.getInstance();
    }

    @FXML
    void loadAddBook() {
        loadWindow(ADDBOOK_FXML, "Add New Book");
    }

    @FXML
    private void loadAddMember() {
        loadWindow(ADDMEMBER_FXML, "Add New Member");
    }

    @FXML
    void loadBookTable() {
        loadWindow(LISTBOOK_FXML, "Book List");
    }

    @FXML
    void loadSettings() {
        loadWindow(SETTINGS_FXML, "Settings");
    }

    @FXML
    void loadMemberTable() {
        loadWindow(LISTMEMBER_FXML, "Member List");
    }

    @FXML
    void loadBookAboutInfo() {
        ObservableList<String> issueData = FXCollections.observableArrayList();
        isReadyForSubmission = false;

        String id = aboutBookID.getText();
        String qu = "SELECT * FROM ISSUE WHERE BOOKID = '" + id + "'";
        ResultSet resultSet = dbHandler.executQuery(qu);
        try{
            while (resultSet.next()) {
                String mBookID = id;
                String mMemberID = resultSet.getString("memberID");
                Timestamp mIssueTime = resultSet.getTimestamp("issueTime");
                int mRenewCount = resultSet.getInt("renew_count");

                issueData.add("Issue Date and Time :" + mIssueTime.toGMTString());
                issueData.add("Renew Count :" + mRenewCount);

                issueData.add("Book Information: ");
                qu = "SELECT * FROM BOOK WHERE ID = '" + mBookID + "'";
                ResultSet rsB = dbHandler.executQuery(qu);
                while(rsB.next()){
                    issueData.add("Book Name: " + rsB.getString("title"));
                    issueData.add("Book ID: " + rsB.getString("id"));
                    issueData.add("Book Author: " + rsB.getString("author"));
                    issueData.add("Book Publisher: " + rsB.getString("publisher"));
                }

                qu = "SELECT * FROM MEMBER WHERE ID = '" + mMemberID + "'";
                ResultSet rsM = dbHandler.executQuery(qu);
                issueData.add("Member Information: ");
                while(rsM.next()){
                    issueData.add("Name: " + rsM.getString("name"));
                    issueData.add("Contact: " + rsM.getString("mobile"));
                    issueData.add("Email: " + rsM.getString("email"));
                }
                isReadyForSubmission = true;
            }
        } catch (SQLException e){
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
        }
        issueDataList.getItems().setAll(issueData);
    }

    @FXML
    void loadBookInfo() {
        clearBookCache();
        String id = bookIDInput.getText();
        String qu = "SELECT * FROM BOOK WHERE id = '" + id + "'";
        ResultSet resultSet = dbHandler.executQuery(qu);
        boolean flag = false;
        try{
            while (resultSet.next()){
                String bName = resultSet.getString("title");
                String bAuthor = resultSet.getString("author");
                boolean bStatus = resultSet.getBoolean("isAvail");

                bookName.setText(bName);
                author.setText(bAuthor);
                String boolStatus = (bStatus)?"Available" : "Not Available";
                status.setText(boolStatus);

                flag = true;
            } if (!flag){
                bookName.setText("No Such book is available");
            }
        } catch (SQLException ex){
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    void loadMemberInfo() {
        cleaMemberCache();
        String memberId = memberIDInput.getText();
        String qu = "SELECT * FROM MEMBER WHERE id = '" + memberId + "'";
        ResultSet resultSet = dbHandler.executQuery(qu);
        boolean flag = false;
        try{

            while (resultSet.next()){
                String mName = resultSet.getString("name");
                String mContact = resultSet.getString("mobile");

                memberName.setText(mName);
                memberContact.setText(mContact);
                flag = true;
            } if (!flag){
                memberName.setText("No Such member is exist");
            }
        } catch (SQLException ex){
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void loadWindow(String location, String title){
        try {
            Parent parent = FXMLLoader.load(getClass().getResource(location));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            stage.show();
        } catch (IOException e) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @FXML
    void loadIssueOperation() {
        String memberID = memberIDInput.getText();
        String bookID = bookIDInput.getText();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Issue information");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure want to issue the book " + bookName.getText()
                            + "\n to " + memberName.getText() + " ?");
        Optional<ButtonType> response = alert.showAndWait();
        if (response.get() == ButtonType.OK){
            String str = "INSERT INTO ISSUE(memberID, bookID) VALUES ("
                    + "'" + memberID + "',"
                    + "'" + bookID + "')";
            String str2 = "UPDATE BOOK SET isAvail = false WHERE id = '" + bookID + "'";
            System.out.println(str + "and" + str2);

            if (dbHandler.executAction(str)&&dbHandler.executAction(str2)){
                Alert anotherAlert = new Alert(Alert.AlertType.INFORMATION);
                anotherAlert.setTitle("Successes");
                anotherAlert.setHeaderText(null);
                anotherAlert.setContentText("Book issue complete");
                anotherAlert.showAndWait();
            } else {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Failed");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Issue Operation failed");
                errorAlert.showAndWait();
            }
        } else {
            Alert informationAlert = new Alert(Alert.AlertType.INFORMATION);
            informationAlert.setTitle("Cancelled");
            informationAlert.setHeaderText(null);
            informationAlert.setContentText("Issue operation Cancelled");
        }
    }

    @FXML
    void loadSubmissionOperations() {
        if (!isReadyForSubmission) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed");
            alert.setHeaderText(null);
            alert.setContentText("Please select book to submit");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Submission Operation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure to return the book ?");

        Optional<ButtonType> responce = alert.showAndWait();
        if (responce.get() == ButtonType.OK) {
            String id = aboutBookID.getText();
            String ac1 = "DELETE FROM ISSUE WHERE BOOKID = '" + id + "'";
            String ac2 = "UPDATE BOOK SET ISAVAIL = TRUE WHERE ID = '" + id + "'";

            if (dbHandler.executAction(ac1) && dbHandler.executAction(ac2)) {
                Alert alertInf = new Alert(Alert.AlertType.INFORMATION);
                alertInf.setTitle("Successes");
                alertInf.setHeaderText(null);
                alertInf.setContentText("Book has been submitted");
                alertInf.showAndWait();
            } else {
                Alert alertErr = new Alert(Alert.AlertType.ERROR);
                alertErr.setTitle("Failed");
                alertErr.setHeaderText(null);
                alertErr.setContentText("Submission has been failed");
                alertErr.showAndWait();
            }
        } else {
            Alert informationAlert = new Alert(Alert.AlertType.INFORMATION);
            informationAlert.setTitle("Cancelled");
            informationAlert.setHeaderText(null);
            informationAlert.setContentText("Submission operation Cancelled");
        }
    }

    @FXML
    void loadRenewOperation() {

        if (!isReadyForSubmission) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Failed");
            alert.setHeaderText(null);
            alert.setContentText("Please select book to renew");
            alert.showAndWait();
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Renew Operation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure to renew the book ?");

        Optional<ButtonType> responce = alert.showAndWait();
        if (responce.get() == ButtonType.OK) {
            String ac = "UPDATE ISSUE SET issueTime = CURRENT_TIMESTAMP, renew_count = renew_count + 1 WHERE BOOKID = '" + aboutBookID.getText() + "'";
            System.out.println(ac);
            if(dbHandler.executAction(ac)){
                Alert alertSuccesses = new Alert(Alert.AlertType.INFORMATION);
                alertSuccesses.setTitle("Successes");
                alertSuccesses.setHeaderText(null);
                alertSuccesses.setContentText("The book has been renewed");
                alertSuccesses.showAndWait();
            } else {
                Alert alertErr = new Alert(Alert.AlertType.ERROR);
                alertErr.setTitle("Failed");
                alertErr.setHeaderText(null);
                alertErr.setContentText("Renew has been failed");
                alertErr.showAndWait();
            }
        } else {
            Alert informationAlert = new Alert(Alert.AlertType.INFORMATION);
            informationAlert.setTitle("Cancelled");
            informationAlert.setHeaderText(null);
            informationAlert.setContentText("Renew operation Cancelled");
        }
    }

    void clearBookCache(){
        bookName.setText("");
        author.setText("");
        status.setText("");
    }

    void cleaMemberCache(){
        memberName.setText("");
        memberContact.setText("");
    }
}
