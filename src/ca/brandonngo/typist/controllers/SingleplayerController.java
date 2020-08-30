package ca.brandonngo.typist.controllers;

import ca.brandonngo.typist.Timer;
import ca.brandonngo.typist.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SingleplayerController extends Util implements Initializable {
    //Singleplayer.fxml UI Components
    @FXML private Pane paneSingleplayer;  //the pane which holds all components
    @FXML private Label lTimer; //label containing the timer
    @FXML private TextField tfTypeBox;    //where the user types
    @FXML private TextFlow tflwWordBox;   //contains each generated word
    @FXML private ScrollPane spWordBox;   //scroll component that manages viewport of the TextFlow
    @FXML private LineChart<NumberAxis, NumberAxis> lcGraph; //line graph displaying previous WPM scores
    @FXML private Button btnBack; //back to menu button
    @FXML private Button btnReset;    //game reset button

    //variables
    private boolean blnGameActive = false;  //stores whether a game is active
    private Timer time;

    private String[] strWordList;   //stores the word list generated each game
    ArrayList<String> strWordsTyped = new ArrayList<>();    //stores what the user types (arraylist will expand as needed)
    private Text[] tArray;  //stores collection of generated words

    XYChart.Series lineWPM; //stores the WPM line data points
    ArrayList<Integer> intWPMData;  //stores previously saved WPM data
    int intCurrentSavedGames;   //stores number of saved scores

    //variables for scrolling viewport
    private int intCurrentIndex = 0;  //current word referenced as an index of strWordList
    private int intScrollState = 1; //stores how many times the textflow has been scrolled while game is active
    private int intCurrentLastTextIndex = 0;    //stores the word index trigger for scroll
    private int intPreviousLastTextIndex = 0; //stores the previous word index trigger

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {    //Runs on launch
        btnBack.styleProperty().set("-fx-shape: M 0 0 L 0 2 L 0 0 L 2 0 L 0 2");    //set button shape to triangle (using svg path)
        btnBack.setGraphic(new ImageView("img/backmenu.png"));  //set image for back button

        //set background colors
        paneSingleplayer.styleProperty().set("-fx-background-color: " + Util.DARK_BLUE);
        spWordBox.styleProperty().set("-fx-background-color: " + Util.AQUA_GREEN);
        tflwWordBox.styleProperty().set("-fx-background-color: " + Util.AQUA_GREEN);

        lTimer.setStyle(    //sets the rounded edges and color of timer label
                "-fx-background-radius: 2; " +
                "-fx-background-color: " + Util.NAVY_BLUE
        );

        btnReset.styleProperty().set("-fx-background-radius: 5");
        btnReset.setGraphic(new ImageView("img/restart.png"));  //set image for reset button

        //populate linechart
        intWPMData = extractWPM();
        intCurrentSavedGames = intWPMData.size();   //for games in the current session
        lineWPM = populateLineChart(lcGraph, intWPMData);

        //call the text generator here
        strWordList = generateWordList();
        tArray = populateTflwWordBox(strWordList, tflwWordBox);

        time = new Timer(lTimer, tfTypeBox, tflwWordBox, spWordBox, lineWPM);   //create new timer object (will start a new thread)
    }

    @FXML   //handles reset button action
    void gameReset(ActionEvent event) {
        reset();
    }

    public void reset() {
        blnGameActive = false;  //game is inactive
        time.setRunning(blnGameActive); //pass game state to timer thread
        lTimer.setText("60");   //reset the label time
        generalReset(tfTypeBox, tflwWordBox, spWordBox);

        //determine new value of intCurrentSavedGames;
        intWPMData = extractWPM();
        intCurrentSavedGames = intWPMData.size();

        strWordsTyped.clear();  //purge words typed
        tflwWordBox.setTextAlignment(TextAlignment.LEFT);
        tfTypeBox.setEditable(true);

        //reset viewport values
        intCurrentIndex = 0;
        intScrollState = 1;
        intCurrentLastTextIndex = 0;
        intPreviousLastTextIndex = 0;

        //call text generator
        strWordList = generateWordList();
        tArray = populateTflwWordBox(strWordList, tflwWordBox);
    }

    @FXML   //holds game logic
    void getWordInput() {
        tfTypeBox.setOnKeyPressed(event -> {
            if (!blnGameActive) {   //trigger game start and configure settings
                blnGameActive = true;   //set state to active
                intCurrentLastTextIndex = getLastTextIndexInRow(tflwWordBox, tArray, intPreviousLastTextIndex); //get scroll trigger token
                time.setRunning(blnGameActive); //pass state to timer class

                //configure settings + send scoring data over
                time.setCurrentSavedGames(intCurrentSavedGames);
                time.setWordList(strWordList);
                time.setWordsTyped(strWordsTyped);
            }
            else if(event.getCode().equals(KeyCode.SPACE)) {    //game running upon space key event
                strWordsTyped.add(tfTypeBox.getText().replace(" ", ""));    //do not include the space as part of user input

                //changes the color to grey if right entry, and red if wrong entry
                if (isWordCorrect(strWordsTyped.get(intCurrentIndex), strWordList[intCurrentIndex])) {  //if user input matches respective word
                    tArray[intCurrentIndex].setFill(Color.DARKGREY);
                }
                else {
                    tArray[intCurrentIndex].setFill(Color.RED);
                }

                tfTypeBox.setText("");  //clear the field for next word input

                if (intCurrentIndex == intCurrentLastTextIndex) {   //determining if scroll trigger token is correct
                    intPreviousLastTextIndex = intCurrentLastTextIndex; //set previous index
                    intCurrentLastTextIndex = getLastTextIndexInRow(tflwWordBox, tArray, intPreviousLastTextIndex)+1;   //get new index
                    spWordBox.setVvalue((tArray[0].getLayoutBounds().getHeight()+tflwWordBox.getLineSpacing()-36.4)*intScrollState);    //scroll to current row of words in the TextFlow
                    intScrollState++;   //increase number of scrolls
                }
                intCurrentIndex++;  //increase index of word
            }
        });
    }

    @FXML   //handles back button action
    void bringBack(ActionEvent event) throws IOException {
        Scene scenMenu = new Scene(FXMLLoader.load(getClass().getResource("../Resources/FXML/Menu.fxml"))); //loads menu fxml

        blnGameActive = false;  //game state is inactive
        time.stop();    //closes timer thread

        //get stage info
        Stage window = (Stage)((Node)event.getSource()).getScene().getWindow(); //get stage information
        window.setScene(scenMenu);  //add fxml to stage
        window.show();
    }

}
