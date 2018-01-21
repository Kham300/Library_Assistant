package library.assistant.ui.memberList;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import library.assistant.database.DBHandler;
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
}
