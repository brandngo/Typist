package ca.brandonngo.typist;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage window) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Resources/FXML/Menu.fxml")); //loads menu fxml
        Scene menu = new Scene(root);   //add menu fxml to scene
        window.setScene(menu);  //add scene to window

        window.setTitle("Typist");
        window.getIcons().add(new Image("img/logo.png"));   //set Typist window icon
        window.setResizable(false);
        window.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
