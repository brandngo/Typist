package ca.brandonngo.typist.controllers;

import ca.brandonngo.typist.Util;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuController implements Initializable {
    //Menu.fxml UI Components
    @FXML private Pane paneMenu;  //the pane which holds all components
    @FXML private ImageView imgTypistBanner;    //Typist banner image
    @FXML private Button btnMultiplayer;    //loads multiplayer screen
    @FXML private Button btnSingleplayer;   //loads singleplayer screen
    @FXML private Button btnInfo;   //loads info screen

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {    //Runs on launch
        paneMenu.styleProperty().set("-fx-background-color: " + Util.DARK_BLUE);    //set color of background

        imgTypistBanner.setEffect(new Glow(0.5));   //set effect of banner

        //set button properties
        btnMultiplayer.styleProperty().set("-fx-background-color: " + Util.LIGHT_BLUE);     //set color of buttons
        btnSingleplayer.styleProperty().set("-fx-background-color: " + Util.LIGHT_BLUE);
        btnInfo.setStyle(   //set button shape to circle
                "-fx-background-radius: 48; " +
                        "-fx-min-width: 50px; " +
                        "-fx-min-height: 50px; " +
                        "-fx-max-width: 50px; " +
                        "-fx-max-height: 50px;"
        );
        btnInfo.setGraphic(new ImageView("img/questionmarkicon.png"));  //set button image
    }

    @FXML   //handles singleplayer button action
    void launchSingleplayer(ActionEvent event) throws IOException {
        Scene scenSingleplayer = new Scene(FXMLLoader.load(getClass().getResource("../Resources/FXML/Singleplayer.fxml"))); //loads singleplayer fxml

        //get stage info
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow(); //get stage information
        window.setScene(scenSingleplayer);  //add fxml to stage
        window.show();
    }

    @FXML   //handles multiplayer button action
    void launchMultiplayer(ActionEvent event) throws IOException {
        /*  WORK IN PROGRESS --> NOT IN THIS VERSION
        Parent rtMultiplayer = FXMLLoader.load(getClass().getResource("../Resources/FXML/Multiplayer.fxml"));
        Scene scenMultiplayer = new Scene(rtMultiplayer);

        //get stage info
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(scenMultiplayer);
        window.show();
        */
    }

    @FXML   //handles help button action
    void launchHelp(ActionEvent event) throws IOException {
        Scene scenHelp = new Scene(FXMLLoader.load(getClass().getResource("../Resources/FXML/Help.fxml"))); //loads help fxml

        //get stage info
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow();
        window.setScene(scenHelp);
        window.show();
    }
}
