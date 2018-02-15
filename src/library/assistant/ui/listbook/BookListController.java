package library.assistant.ui.listbook;

import javafx.beans.property.SimpleBooleanProperty;
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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.alert.AlertMaker;
import library.assistant.database.DBHandler;
import library.assistant.ui.addBook.BookAddController;
import library.assistant.ui.main.MainController;
import library.assistant.utils.LibraryAssistantUtils;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
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
        bookObservableList.clear();

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

    @FXML
    public void handleBookDeleteOption() {
        Book selectedForDeletion = tableView.getSelectionModel().getSelectedItem();
        if (selectedForDeletion == null){
            AlertMaker.showErrorMessage("No Book selected", "Please select a book for deletion");
            return;
        }
        if (DBHandler.getInstance().isBookAlreadyIssued(selectedForDeletion)){
            AlertMaker.showErrorMessage("Can't BE Deleted", "This book is already Issued and cant be deleted!");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deleting book");
        alert.setContentText("Are you sure  to delete the book " + selectedForDeletion.getTitle() + " ?");
        Optional<ButtonType> answer = alert.showAndWait();
        if (answer.get() == ButtonType.OK){
            boolean result = DBHandler.getInstance().deleteBook(selectedForDeletion);
            if (result){
                AlertMaker.showSimpleAlert("Book deleted", selectedForDeletion.getTitle() + " was deleted successfully");
            } else {
                AlertMaker.showSimpleAlert("Deletion failed", selectedForDeletion.getTitle() + " could not be deleted!");
            }
        } else {
            AlertMaker.showSimpleAlert("Deletion cancelled", "Deletion process cancelled");
        }
    }

    @FXML
    public void handleBookEditOption() {
        Book selectedForEdit = tableView.getSelectionModel().getSelectedItem();
        if (selectedForEdit == null){
            AlertMaker.showErrorMessage("No Book selected", "Please select a book for editing");
            return;
        } try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/library/assistant/ui/addBook/add_book.fxml"));
            Parent parent = loader.load();

            BookAddController controller = (BookAddController) loader.getController();
            controller.inFlateUI(selectedForEdit);
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Book");
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryAssistantUtils.setStageIcon(stage);
            //refresh automatically the ne data in booklist
            stage.setOnCloseRequest((e) -> handleBookRefreshOption());
        } catch (IOException e) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
        }
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


    @FXML
    void handleBookRefreshOption() {
        loadData();
    }
}

