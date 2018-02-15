package library.assistant.ui.addBook;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import library.assistant.alert.AlertMaker;
import library.assistant.database.DBHandler;
import library.assistant.ui.listbook.BookListController;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import static library.assistant.ui.listbook.BookListController.*;

public class BookAddController implements Initializable{
    @FXML
    private JFXTextField title;
    @FXML
    private JFXTextField id;
    @FXML
    private JFXTextField author;
    @FXML
    private JFXTextField publisher;
    @FXML
    private AnchorPane rootPane;
    private Boolean isInEditMode = Boolean.FALSE;


    DBHandler dbHandler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dbHandler = DBHandler.getInstance();
        checkData();
    }


    @FXML
    public void addBook() {
        String bookID = id.getText();
        String bookAuthor = author.getText();
        String bookName = title.getText();
        String bookPublisher = publisher.getText();

        if (bookID.isEmpty()||bookAuthor.isEmpty()||bookPublisher.isEmpty()||bookName.isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Enter in all fields !");
            alert.showAndWait();
            return;
        }

        if (isInEditMode){
            handlerEditOperation();
            return;
        }

        String qu = "INSERT INTO BOOK VALUES (" +
                "'" + bookID +"',"+
                "'" + bookName +"',"+
                "'" + bookAuthor +"',"+
                "'" + bookPublisher  +"',"+
                ""  + "true" + "" +
                ")";
        System.out.println(qu);
        if (dbHandler.executAction(qu)){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Success");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Failed");
            alert.showAndWait();
        }
    }

    @FXML
    void cancel() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }


    private void checkData() {
        String query = "SELECT title FROM BOOK";
        ResultSet resultSet = dbHandler.executQuery(query);
        try {
            while (resultSet.next()) {
                String titlex = resultSet.getString("title");
                System.out.println(titlex);
            }
        }catch (SQLException ex){
            Logger.getLogger(BookAddController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void inFlateUI(Book book){
        title.setText(book.getTitle());
        id.setText(book.getId());
        author.setText(book.getAuthor());
        publisher.setText(book.getPublisher());
        id.setEditable(false);
        isInEditMode = Boolean.TRUE;
    }

    private void handlerEditOperation() {
        Book book = new Book(title.getText(), id.getText(), author.getText(), publisher.getText(), true);
        if(dbHandler.updateBook(book)){
            AlertMaker.showSimpleAlert("Success", "BookUpdated");
        } else {
            AlertMaker.showErrorMessage("Failed", "Can't update Book");
        }
    }

}
