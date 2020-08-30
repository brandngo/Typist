package ca.brandonngo.typist.controllers;

import ca.brandonngo.typist.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HelpController extends Util implements Initializable {    //Runs on launch
    //Help.fxml UI Components
    @FXML private Pane paneHelp;  //the pane which holds all components
    @FXML private StackPane stckpPane;  //the pane which holds information imageviews and texts
    @FXML private Button btnBack;   //back to menu button

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {    //Runs on launch
        //set background colors
        stckpPane.styleProperty().set("-fx-background-color: " + Util.DARK_BLUE);
        paneHelp.styleProperty().set("-fx-background-color: " + Util.DARK_BLUE);

        btnBack.styleProperty().set("-fx-shape: M 0 0 L 0 2 L 0 0 L 2 0 L 0 2");    //makes the button into a triangular shape (using svg path)
        btnBack.setGraphic(new ImageView("img/backmenu.png"));  //set image for back button
    }

    @FXML   //handles help button action
    void bringBack(ActionEvent event) throws IOException {
        Scene scenMenu = new Scene(FXMLLoader.load(getClass().getResource("../Resources/FXML/Menu.fxml"))); //loads menu fxml

        //get stage info
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow(); //get stage information
        window.setScene(scenMenu);  //add fxml to stage
        window.show();
    }
}
