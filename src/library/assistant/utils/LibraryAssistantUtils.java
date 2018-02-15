package library.assistant.utils;

import javafx.scene.image.Image;
import javafx.stage.Stage;

public class LibraryAssistantUtils {
    public static final String IMAGE_LOC = "/resources/Library.png";

    public static void setStageIcon(Stage stageIcon){
        stageIcon.getIcons().add(new Image(IMAGE_LOC));
    }
}
