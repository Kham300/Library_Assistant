package library.assistant.database;

import javax.swing.*;
import java.sql.*;

public class DBHandler {

    private static DBHandler dataBaseHandler;

    private static final String DB_USER ="root";
    private static final String DB_PASS = "root";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/library?" +
            "useSSL=true&amp;autoReconnect=true&amp;serverTimezone=UTC";
    private static Connection dbConnection;
    private static Statement statement;

    private DBHandler() {
        createConnection();
        setupBookTable();
        setupMembertable();
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

    private void setupMembertable() {
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
                            + "         issueTime TIMESTAMP default CURRENT_TIMESTAMP ,\n"
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

}
