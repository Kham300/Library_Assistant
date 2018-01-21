package library.assistant.ui.main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import library.assistant.database.DBHandler;

public class MainLoader extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent parent = FXMLLoader.load(getClass().getResource("main.fxml"));

        Scene scene = new Scene(parent);

        primaryStage.setScene(scene);
        primaryStage.show();

        new Thread(() -> DBHandler.getInstance()).start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
