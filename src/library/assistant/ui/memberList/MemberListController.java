package library.assistant.ui.memberList;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.alert.AlertMaker;
import library.assistant.database.DBHandler;
import library.assistant.ui.addBook.BookAddController;
import library.assistant.ui.addMember.MemberAddController;
import library.assistant.ui.listbook.BookListController;
import library.assistant.ui.main.MainController;
import library.assistant.utils.LibraryAssistantUtils;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MemberListController implements Initializable {

    ObservableList<Member> memberObservableList = FXCollections.observableArrayList();

    @FXML
    private TableView<Member> tableView;
    @FXML
    private TableColumn<Member, String> nameCol;
    @FXML
    private TableColumn<Member, String> idCol;
    @FXML
    private TableColumn<Member, String> mobileCol;
    @FXML
    private TableColumn<Member, String> emailCol;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initCol();
        loadData();
    }

    private void initCol() {
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("mobile"));
        mobileCol.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    private void loadData() {
        memberObservableList.clear();

        DBHandler dbHandler = DBHandler.getInstance();
        String query = "SELECT * FROM MEMBER";
        ResultSet resultSet = dbHandler.executQuery(query);
        try {
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String id = resultSet.getString("id");
                String mobile = resultSet.getString("mobile");
                String email = resultSet.getString("email");

                memberObservableList.add(new Member(name, id, mobile, email));
            }
        }catch (SQLException ex){
            Logger.getLogger(MemberListController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableView.getItems().setAll(memberObservableList);
    }

    public static class Member {

        private final SimpleStringProperty name;
        private final SimpleStringProperty id;
        private final SimpleStringProperty mobile;
        private final SimpleStringProperty email;


        public Member(String name, String id, String mobile, String email) {
            this.name = new SimpleStringProperty(name);
            this.id = new SimpleStringProperty(id);
            this.mobile = new SimpleStringProperty(mobile);
            this.email = new SimpleStringProperty(email);
        }

        public String getName() {
            return name.get();
        }

        public String getId() {
            return id.get();
        }

        public String getMobile() {
            return mobile.get();
        }

        public String getEmail() {
            return email.get();
        }
    }

    @FXML
    void handleDeleteMember() {
        Member selectedMember = tableView.getSelectionModel().getSelectedItem();
        if (selectedMember == null){
            AlertMaker.showErrorMessage("No member selected", "Please select a member for deletion");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deleting member");
        alert.setContentText("Are you sure  to delete the member " + selectedMember.getName() + " ?");
        Optional<ButtonType> answer = alert.showAndWait();
        if (answer.get() == ButtonType.OK){
            boolean result = DBHandler.getInstance().deleteMember(selectedMember);
            if (result){
                AlertMaker.showSimpleAlert("Member deleted", selectedMember.getName() + " was deleted successfully");
            } else {
                AlertMaker.showSimpleAlert("Deletion failed", selectedMember.getName() + " could not be deleted!");
            }
        } else {
            AlertMaker.showSimpleAlert("Deletion cancelled", "Deletion process cancelled");
        }
    }

    @FXML
    void handleEditMember() {
        Member selectedForEdit = tableView.getSelectionModel().getSelectedItem();
        if (selectedForEdit == null){
            AlertMaker.showErrorMessage("No member selected", "Please select a book for editing");
            return;
        } try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/library/assistant/ui/addMember/memberAdd.fxml"));
            Parent parent = loader.load();

            MemberAddController controller = (MemberAddController) loader.getController();
            controller.inFlateUI(selectedForEdit);

            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Member");
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryAssistantUtils.setStageIcon(stage);

            //refresh automatically the ne data in memberlist
            stage.setOnCloseRequest((e) -> handleRefreshMemberList());
        } catch (IOException e) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    @FXML
    void handleRefreshMemberList() {
        loadData();
    }

}
