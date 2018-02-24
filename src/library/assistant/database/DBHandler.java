package library.assistant.database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.PieChart;
import library.assistant.ui.memberList.MemberListController;

import javax.swing.*;
import java.sql.*;
import static library.assistant.ui.listbook.BookListController.*;

public class DBHandler {

    private static DBHandler dataBaseHandler;

    private static final String DB_USER ="root";
    private static final String DB_PASS = "root";
    private static final String DB_URL  = "jdbc:mysql://localhost:3306/library?" +
                                         "useSSL=true&amp;autoReconnect=true&amp;serverTimezone=UTC";
    private static Connection dbConnection;
    private static Statement statement;

    private DBHandler() {
        createConnection();
        setupBookTable();
        setupMemberTable();
        setupIssueTable();
    }

    public static DBHandler getInstance(){
        if (dataBaseHandler == null){
            dataBaseHandler = new DBHandler();
        }
        return dataBaseHandler;
    }

    void  createConnection(){
        try {
            dbConnection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            if (!dbConnection.isClosed()){
                System.out.println("соединенеие с бд установлено");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void setupBookTable(){
        String TABLE_NAME = "BOOK";
        try {
            statement = dbConnection.createStatement();

            DatabaseMetaData databaseMetaData = dbConnection.getMetaData();
            ResultSet tables = databaseMetaData.getTables(null, null, TABLE_NAME.toUpperCase(), null);

            if (tables.next()){
                System.out.println("Table " + TABLE_NAME + " already exists. Ready to go!");
            } else {
                statement.execute("CREATE  TABLE " + TABLE_NAME + "("
                            + "         id VARCHAR(200) PRIMARY KEY,\n"
                            + "         title VARCHAR(200),\n"
                            + "         author VARCHAR(200),\n"
                            + "         publisher VARCHAR(100),\n"
                            + "         isAvail boolean DEFAULT TRUE"
                            + " )");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + "......setupDataBase");
        } finally {

        }
    }

    public ResultSet executQuery(String query){
        ResultSet resultSet;
        try{
            statement = dbConnection.createStatement();
            resultSet = statement.executeQuery(query);
        }catch (SQLException e){
            System.out.println("Exception at executeQuery: dataHandler" + e.getLocalizedMessage());
            return null;
        }
        return resultSet;
    }

    public boolean executAction(String qu){
        try {
            statement = dbConnection.createStatement();
            statement.execute(qu);
            return true;
        } catch (SQLException ex){
            JOptionPane.showMessageDialog(null, "Error:"+ ex.getMessage(),
                    "Error Occured", JOptionPane.ERROR_MESSAGE);
            System.out.println("Exception at executQuery: dataHandler" + ex.getLocalizedMessage());
            return false;
        }
    }

    private void setupMemberTable() {
        String TABLE_NAME = "MEMBER";
        try {
            statement = dbConnection.createStatement();

            DatabaseMetaData databaseMetaData = dbConnection.getMetaData();
            ResultSet tables = databaseMetaData.getTables(null, null, TABLE_NAME.toUpperCase(), null);

            if (tables.next()){
                System.out.println("Table " + TABLE_NAME + " already exists. Ready to go!");
            } else {
                statement.execute("CREATE  TABLE " + TABLE_NAME + "("
                        + "         id VARCHAR(200) PRIMARY KEY,\n"
                        + "         name VARCHAR(200),\n"
                        + "         mobile VARCHAR(20),\n"
                        + "         email VARCHAR(100)\n"
                        + " )");
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage() + "......setupDataBase");
        } finally {
        }
    }

    void setupIssueTable(){
        String TABLE_NAME = "ISSUE";
        try{
            statement = dbConnection.createStatement();
            DatabaseMetaData databaseMetaData = dbConnection.getMetaData();
            ResultSet tables = databaseMetaData.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            if (tables.next()){
                System.out.println("Table" + TABLE_NAME + " is allready exist");
            } else {
                statement.execute("CREATE TABLE " + TABLE_NAME + "("
                            + "         bookID VARCHAR(200) PRIMARY KEY,\n"
                            + "         memberID VARCHAR(200),\n"
                            + "         issueTime TIMESTAMP default CURRENT_TIMESTAMP,\n"
                            + "         renew_count INTEGER  DEFAULT 0,\n"
                            + "         FOREIGN KEY (bookID) REFERENCES BOOK(id),\n"
                            + "         FOREIGN KEY (memberID) REFERENCES MEMBER(id)"
                            +" )");
            }
        }catch (SQLException e){
            System.err.println(e.getMessage()+ "///setupDataBase");
        }finally {
        }
    }

    public boolean deleteMember(MemberListController.Member member){
        try {
            String deleteStatement = "DELETE FROM MEMBER WHERE ID = ?";
            PreparedStatement preparedStatement = dbConnection.prepareStatement(deleteStatement);
            preparedStatement.setString(1, member.getId());
            int res = preparedStatement.executeUpdate();
            System.out.println(res);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteBook(Book book){
        try {
            String deleteStatement = "DELETE FROM BOOK WHERE ID = ?";
            PreparedStatement preparedStatement = dbConnection.prepareStatement(deleteStatement);
            preparedStatement.setString(1, book.getId());
            int res = preparedStatement.executeUpdate();
            System.out.println(res);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isBookAlreadyIssued(Book book){
        try {
            String checkStmt = "SELECT COUNT(*) FROM ISSUE WHERE bookID = ?";
            PreparedStatement preparedStatement = dbConnection.prepareStatement(checkStmt);
            preparedStatement.setString(1, book.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                int count = resultSet.getInt(1);
                if (count > 0){
                    return true;
                } else {
                    return false;
                }
            }
        } catch (SQLException ex){ex.printStackTrace();}
        return false;
    }

    public boolean updateBook(Book book){

        try {
            String update = "UPDATE MEMBER SET TITLE=?, AUTHOR=?, PUBLISHER=? WHERE ID=?";
            PreparedStatement statement = dbConnection.prepareStatement(update);
            statement.setString(1,book.getTitle());
            statement.setString(2, book.getAuthor());
            statement.setString(3, book.getPublisher());
            statement.setString(4, book.getId());
            int res = statement.executeUpdate();
            return (res > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateMember(MemberListController.Member member){
        try {
            String update ="UPDATE MEMBER SET NAME=?, MOBILE=?, EMAIL=? WHERE ID=?";
            PreparedStatement statement = dbConnection.prepareStatement(update);
            statement.setString(1, member.getName());
            statement.setString(2, member.getMobile());
            statement.setString(3, member.getEmail());
            statement.setString(4, member.getId());
            int res = statement.executeUpdate();
            return (res > 0);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public ObservableList<PieChart.Data> getBookGraphStatistics(){
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        try{

            String qu1 = "SELECT COUNT(*) FROM BOOK";
            String qu2 = "SELECT COUNT(*) FROM ISSUE";
            ResultSet resultSet = executQuery(qu1);
            if (resultSet.next()){
                int count = resultSet.getInt(1);
                data.add(new PieChart.Data("Total Books(" + count+ ")", count));
            }
            resultSet = executQuery(qu2);
            if (resultSet.next()){
                int count = resultSet.getInt(1);
                data.add(new PieChart.Data("Issued Books(" + count+ ")", count));
            }
        } catch (SQLException e) { e.printStackTrace();}
        return data;
    }

    public ObservableList<PieChart.Data> getMemberGraphStatistics(){
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        try{

            String qu1 = "SELECT COUNT(*) FROM MEMBER";
            String qu2 = "SELECT COUNT(DISTINCT memberID) FROM ISSUE";
            ResultSet resultSet = executQuery(qu1);
            if (resultSet.next()){
                int count = resultSet.getInt(1);
                data.add(new PieChart.Data("Total Members(" + count+ ")", count));
            }
            resultSet = executQuery(qu2);
            if (resultSet.next()){
                int count = resultSet.getInt(1);
                data.add(new PieChart.Data("Members with books(" + count+ ")", count));
            }
        } catch (SQLException e) { e.printStackTrace();}
        return data;
    }

}
