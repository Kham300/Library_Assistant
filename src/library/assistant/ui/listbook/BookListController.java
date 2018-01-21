package library.assistant.ui.listbook;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import library.assistant.database.DBHandler;
import library.assistant.ui.addBook.BookAddController;

import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BookListController implements Initializable {

    ObservableList<Book> bookObservableList = FXCollections.observableArrayList();

    @FXML
    private AnchorPane rootPane;
    @FXML
    private TableView<Book> tableView;
    @FXML
    private TableColumn<Book, String> titleCol;
    @FXML
    private TableColumn<Book, String> idCol;
    @FXML
    private TableColumn<Book, String> authorCol;
    @FXML
    private TableColumn<Book, String> publisherCol;
    @FXML
    private TableColumn<Book, Boolean> availableCol;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initCol();
        loadData();
    }

    private void initCol() {
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        availableCol.setCellValueFactory(new PropertyValueFactory<>("availability"));
    }


    private void loadData() {
        DBHandler dbHandler = DBHandler.getInstance();
        String query = "SELECT * FROM BOOK";
        ResultSet resultSet = dbHandler.executQuery(query);
        try {
            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String id = resultSet.getString("id");
                String author = resultSet.getString("author");
                String publisher = resultSet.getString("publisher");
                Boolean avail = resultSet.getBoolean("isAvail");

                bookObservableList.add(new Book(title, id, author, publisher,avail));
            }
        }catch (SQLException ex){
            Logger.getLogger(BookAddController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableView.getItems().setAll(bookObservableList);
    }

    public static class Book{

        private final SimpleStringProperty title;
        private final SimpleStringProperty id;
        private final SimpleStringProperty author;
        private final SimpleStringProperty publisher;
        private final SimpleBooleanProperty availability;

        public Book(String title, String id, String author, String publisher, Boolean availability) {
            this.title = new SimpleStringProperty(title);
            this.id = new SimpleStringProperty(id);
            this.author = new SimpleStringProperty(author);
            this.publisher = new SimpleStringProperty(publisher);
            this.availability = new SimpleBooleanProperty(availability);
        }

        public String getTitle() {
            return title.get();
        }

        public String getId() {
            return id.get();
        }

        public String getAuthor() {
            return author.get();
        }

        public String getPublisher() {
            return publisher.get();
        }


        public boolean isAvailability() {
            return availability.get();
        }

    }
}

