package library.assistant.ui.main.toolbar;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

import static library.assistant.ui.main.MainController.*;
import static library.assistant.utils.LibraryAssistantUtils.loadWindow;

public class ToolbarController implements Initializable {
    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    void loadAddBook() {
        loadWindow(getClass().getResource(ADDBOOK_FXML), "Add New Book", null);
    }

    @FXML
    private void loadAddMember() {
        loadWindow(getClass().getResource(ADDMEMBER_FXML), "Add New Member", null);
    }

    @FXML
    void loadBookTable() {
        loadWindow(getClass().getResource(LISTBOOK_FXML), "Book List", null);
    }

    @FXML
    void loadSettings() {
        loadWindow(getClass().getResource(SETTINGS_FXML), "Settings", null);
    }

    @FXML
    void loadMemberTable() {
        loadWindow(getClass().getResource(LISTMEMBER_FXML), "Member List", null);
    }
}
