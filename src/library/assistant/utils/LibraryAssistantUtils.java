package library.assistant.utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.ui.main.MainController;

import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LibraryAssistantUtils {
    public static final String IMAGE_LOC = "/resources/Library.png";

    public static void setStageIcon(Stage stageIcon){
        stageIcon.getIcons().add(new Image(IMAGE_LOC));
    }
    public static void loadWindow(URL location, String title, Stage patentStage){
        try {
            Parent parent = FXMLLoader.load(location);
            Stage stage = null;
            if (patentStage!= null){
                stage = patentStage;
            } else {
                stage = new Stage(StageStyle.DECORATED);
            }
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            stage.show();
            setStageIcon(stage);
        } catch (IOException e) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
