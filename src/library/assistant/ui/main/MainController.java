package library.assistant.ui.main;

import com.jfoenix.controls.*;
import com.jfoenix.transitions.hamburger.*;
import com.jfoenix.effects.JFXDepthManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import library.assistant.alert.AlertMaker;
import library.assistant.database.DBHandler;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import static library.assistant.utils.LibraryAssistantUtils.*;

public class MainController implements Initializable {

    public static final String ADDMEMBER_FXML = "/library/assistant/ui/addMember/memberAdd.fxml";
    public static final String ADDBOOK_FXML = "/library/assistant/ui/addBook/add_book.fxml";
    public static final String LISTBOOK_FXML = "/library/assistant/ui/listbook/book_list.fxml";
    public static final String LISTMEMBER_FXML = "/library/assistant/ui/memberList/member_list.fxml";
    public static final String TOOLBAR_FXML = "/library/assistant/ui/main/toolbar/toolbar.fxml";
    public static final String SETTINGS_FXML = "/library/assistant/settings/settings.fxml";

    @FXML
    private AnchorPane rootAnchorPane;
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
    private StackPane rootPane;
    @FXML
    private JFXHamburger hamburger;
    @FXML
    private JFXDrawer drawer;
    @FXML
    private Text memberNameHolder;
    @FXML
    private Text memberEmailHolder;
    @FXML
    private Text memberContactHolder;
    @FXML
    private Text bookNameHolder;
    @FXML
    private Text bookAuthorHolder;
    @FXML
    private Text bookPublisherHolder;
    @FXML
    private Text issueDateHolder;
    @FXML
    private Text noDaysHolder;
    @FXML
    private Text fineHolder;
    @FXML
    private JFXButton renewBtn;
    @FXML
    private JFXButton submissionBtn;
    @FXML
    private HBox submissionDataContainer;
    @FXML
    private StackPane bookInfoContainer;
    @FXML
    private StackPane memberInfoContainer;
    @FXML
    private Tab bookIssueTab;

    Boolean isReadyForSubmission = false;
    DBHandler dbHandler;
    PieChart bookChart;
    PieChart memberChart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        JFXDepthManager.setDepth(book_Info, 1);
        JFXDepthManager.setDepth(member_Info, 1);

        dbHandler = DBHandler.getInstance();

        initDrawer();

        initGraphs();
    }

    private void initGraphs() {
        bookChart = new PieChart(dbHandler.getBookGraphStatistics());
        bookInfoContainer.getChildren().add(bookChart);
        memberChart = new PieChart(dbHandler.getMemberGraphStatistics());
        memberInfoContainer.getChildren().add(memberChart);

        bookIssueTab.setOnSelectionChanged(event -> {
            clearIssueEntries();
            if (bookIssueTab.isSelected()){
                refereshGraphs();
            }
        });
    }

    private void initDrawer() {
        try {
            VBox toolbar = FXMLLoader.load(getClass().getResource(TOOLBAR_FXML));
            drawer.setSidePane(toolbar);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HamburgerSlideCloseTransition task = new HamburgerSlideCloseTransition(hamburger);
        task.setRate(-1);
        hamburger.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            task.setRate(task.getRate() * -1);
            task.play();
            if (drawer.isHidden()){
                drawer.open();
            } else {
                drawer.close();
            }
        });
    }

    @FXML
    void loadBookInfo() {
        clearBookCache();
        enableDisableGraph(false);

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
    void loadBookInfo2(){
        cleanEntries();
        ObservableList<String> issueData = FXCollections.observableArrayList();
        isReadyForSubmission = false;

        try {
            String id = bookIDInput.getText();
            String myQuery = "SELECT ISSUE.bookID, ISSUE.memberID, ISSUE.issueTime, ISSUE.renew_count,\n"
                    + "MEMBER.name, MEMBER.mobile, MEMBER.email,\n"
                    + "BOOK.title, BOOK.author, BOOK.publisher\n"
                    + "FROM ISSUE\n"
                    + "LEFT JOIN MEMBER\n"
                    + "ON ISSUE.memberID=MEMBER.ID\n"
                    + "LEFT JOIN BOOK\n"
                    + "ON ISSUE.bookID=BOOK.ID\n"
                    + "WHERE ISSUE.bookID='" + id + "'";
            ResultSet resultSet = dbHandler.executQuery(myQuery);
            if (resultSet.next()) {
                memberNameHolder.setText(resultSet.getString("name"));
                memberContactHolder.setText(resultSet.getString("mobile"));
                memberEmailHolder.setText(resultSet.getString("email"));

                bookNameHolder.setText(resultSet.getString("title"));
                bookAuthorHolder.setText(resultSet.getString("author"));
                bookPublisherHolder.setText(resultSet.getString("publisher"));

                Timestamp mIssueTime = resultSet.getTimestamp("issueTime");
                Date dateOfIssue = new Date(mIssueTime.getTime());
                issueDateHolder.setText(dateOfIssue.toString());
                long timeElapsed = System.currentTimeMillis() - mIssueTime.getTime();
                long daysElapsed = TimeUnit.DAYS.convert(timeElapsed, TimeUnit.MICROSECONDS);
                noDaysHolder.setText(String.valueOf(daysElapsed));
                fineHolder.setText("Not SupYEt");

                isReadyForSubmission = true;
                disableEnableControls(true);
                submissionDataContainer.setOpacity(1);
            } else {
                JFXButton jfxButton = new JFXButton("Okay, i will check");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(jfxButton), "No such Book Exists in Issue Database", null);
            }
        }catch (Exception e){e.printStackTrace();}
    }

    private void cleanEntries() {
        memberNameHolder.setText("");
        memberEmailHolder.setText("");
        memberContactHolder.setText("");

        bookNameHolder.setText("");
        bookAuthorHolder.setText("");
        bookPublisherHolder.setText("");

        issueDateHolder.setText("");
        noDaysHolder.setText("");
        fineHolder.setText("");

        disableEnableControls(false );
        submissionDataContainer.setOpacity(0);
    }

    private void disableEnableControls(boolean enableFlag){
        if (enableFlag){
            renewBtn.setDisable(false);
            submissionBtn.setDisable(false);
        } else {
            renewBtn.setDisable(true);
            submissionBtn.setDisable(true);
        }
    }

    @FXML
    void loadMemberInfo() {
        cleaMemberCache();
        enableDisableGraph(false);

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

    @FXML
    void loadIssueOperation() {
        String memberID = memberIDInput.getText();
        String bookID = bookIDInput.getText();

        JFXButton yesButton = new JFXButton("YES");

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Issue information");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure want to issue the book " + bookName.getText()
                + "\n to " + memberName.getText() + " ?");
        Optional<ButtonType> response = alert.showAndWait();
        if (response.get() == ButtonType.OK) {
            String str = "INSERT INTO ISSUE(memberID, bookID) VALUES ("
                    + "'" + memberID + "',"
                    + "'" + bookID + "')";
            String str2 = "UPDATE BOOK SET isAvail = false WHERE id = '" + bookID + "'";
            System.out.println(str + "and" + str2);

            if (dbHandler.executAction(str) && dbHandler.executAction(str2)) {
                JFXButton button = new JFXButton("Done!");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "Book Issue Complete", null);
                refereshGraphs();
            } else {
                JFXButton button = new JFXButton("Okay.I'll Check");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "Issue Operation Failed", null);
            }
            clearIssueEntries();
            JFXButton noButton = new JFXButton("NO");
            noButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {
                JFXButton button = new JFXButton("That's Okay");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(button), "Issue Cancelled", null);
                clearIssueEntries();

            });
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(yesButton, noButton), "Confirm Issue", "Are you sure want to issue the book " + bookName.getText() + " to " + memberName.getText() + " ?");

        }
    }

    private void clearIssueEntries() {
        bookIDInput.clear();
        memberIDInput.clear();
        bookName.setText("");
        author.setText("");
        status.setText("");
        memberName.setText("");
        memberContact.setText("");
        enableDisableGraph(true);
    }

    @FXML
    void loadSubmissionOperations() {
        if (!isReadyForSubmission) {
            JFXButton btn = new JFXButton("Okay!");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Please select a book to submit", "Cant simply submit a null book :-)");
            return;
        }

        JFXButton yesButton = new JFXButton("YES, Please");
        yesButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent ev) -> {
            String id = bookIDInput.getText();
            String ac1 = "DELETE FROM ISSUE WHERE BOOKID = '" + id + "'";
            String ac2 = "UPDATE BOOK SET ISAVAIL = TRUE WHERE ID = '" + id + "'";

            if (dbHandler.executAction(ac1) && dbHandler.executAction(ac2)) {
                JFXButton btn = new JFXButton("Done!");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Book has been submitted", null);
                disableEnableControls(false);
                submissionDataContainer.setOpacity(0);
            } else {
                JFXButton btn = new JFXButton("Okay.I'll Check");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Submission Has Been Failed", null);
            }
        });
        JFXButton noButton = new JFXButton("No, Cancel");
        noButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent ev) -> {
            JFXButton btn = new JFXButton("Okay!");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Submission Operation cancelled", null);
        });

        AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(yesButton, noButton), "Confirm Submission Operation", "Are you sure want to return the book ?");
    }

    @FXML
    void loadRenewOperation() {
        if (!isReadyForSubmission) {
            JFXButton btn = new JFXButton("Okay!");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Please select a book to renew", null);
            return;
        }
        JFXButton yesButton = new JFXButton("YES, Please");
        yesButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {
            String ac = "UPDATE ISSUE SET issueTime = CURRENT_TIMESTAMP, renew_count = renew_count+1 WHERE BOOKID = '" + bookIDInput.getText() + "'";
            System.out.println(ac);
            if (dbHandler.executAction(ac)) {
                JFXButton btn = new JFXButton("Alright!");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Book Has Been Renewed", null);
                disableEnableControls(false);
                submissionDataContainer.setOpacity(0);
            } else {
                JFXButton btn = new JFXButton("Okay!");
                AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Renew Has Been Failed", null);
            }
        });
        JFXButton noButton = new JFXButton("No, Don't!");
        noButton.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent event1) -> {
            JFXButton btn = new JFXButton("Okay!");
            AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(btn), "Renew Operation cancelled", null);
        });
        AlertMaker.showMaterialDialog(rootPane, rootAnchorPane, Arrays.asList(yesButton, noButton), "Confirm Renew Operation", "Are you sure want to renew the book ?");
    }

    @FXML
    void handleMenuClose() {
        ((Stage)rootPane.getScene().getWindow()).close();
    }

    @FXML
    void handleMenuAddBook() {
        loadWindow(getClass().getResource(ADDBOOK_FXML), "Add New Book", null);
    }

    @FXML
    void handleMenuAddMember() {
        loadWindow(getClass().getResource(ADDMEMBER_FXML), "Add New Member",null);
    }

    @FXML
    void handleMenuViewBook() {
        loadWindow(getClass().getResource(LISTBOOK_FXML), "Book List", null);
    }

    @FXML
    void handleMenuViewMember() {
        loadWindow(getClass().getResource(LISTMEMBER_FXML), "Member List", null);
    }


    @FXML
    void handleMenuViewFullScreen() {
        Stage stage = ((Stage)rootPane.getScene().getWindow());
        stage.setFullScreen(!stage.isFullScreen());
    }

    @FXML
    void handleMenuAbout() {

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

    private void enableDisableGraph(boolean status){
        if (status){
            bookChart.setOpacity(1);
            memberChart.setOpacity(1);
        } else {
            bookChart.setOpacity(0);
            memberChart.setOpacity(0);
        }
    }

    private void refereshGraphs(){
        memberChart.setData(dbHandler.getMemberGraphStatistics());
        bookChart.setData(dbHandler.getBookGraphStatistics());
    }
}
