package ca.brandonngo.typist;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

public class Timer extends Util implements Runnable {
    //Timer class variables
    private Thread timerThread;
    public final AtomicBoolean threadRunning = new AtomicBoolean(false);  //this controls thread state
    private final int intGameTime = 60; //controls the length of the timer
    public static boolean blnGameActive;   //determines if a game is active

    //FX UI Components
    private Label lTimer;
    private TextField tfBox;
    private TextFlow tflwWordBox;
    private ScrollPane spPane;

    //for calculating score
    private ArrayList<String> strWordsTyped;
    private String[] strWordList;

    //for updating score
    XYChart.Series lineWPM;
    private int intCurrentSavedGames;

    //things that have to be passed once to the Timer class (UI Components)
    public Timer(Label ta, TextField tfBox, TextFlow tflwWordBox, ScrollPane spPane, XYChart.Series lineWPM) {
        start();
        this.lTimer = ta;
        this.tfBox = tfBox;
        this.tflwWordBox = tflwWordBox;
        this.spPane = spPane;
        this.lineWPM = lineWPM;
    }

    //setters
    public void setWordList(String[] strWordList) {
        this.strWordList = strWordList;
    }

    public void setWordsTyped(ArrayList<String> strWordsTyped) {
        this.strWordsTyped = strWordsTyped;
    }

    public static void setRunning(boolean blnRunning) {
        blnGameActive = blnRunning;
    }

    public void setCurrentSavedGames (int intCurrentSavedGames) {
        this.intCurrentSavedGames = intCurrentSavedGames;
    }

    //will initialise the thread
    public void start() {
        timerThread = new Thread(this);
        threadRunning.set(true);
        timerThread.setDaemon(true);    //ends timer thread on program close
        timerThread.start();
    }

    //closes the thread using the AtomicBoolean as thread.stop() is deprecated
    public void stop() {
        threadRunning.set(false);
    }

    //the logic of the timer thread
    @Override
    public void run() {
        while (threadRunning.get()) {      //keep thread running while the singleplayer fxml is displayed
            if (blnGameActive) {   //if a new game is being started then run
                for (int time = intGameTime; time >= 0; time--) {
                    if (blnGameActive == false) {  //if a reset happens during an active game
                        lTimer.setText("60");
                        break;
                    }
                    else if (time > 0) {    //during game
                        try {
                            int finalTime = time;   //grabs the current time
                            Platform.runLater(() -> lTimer.setText(Integer.toString(finalTime)));     //update label on the application GUI thread
                            Thread.sleep(1000);     //1s = 1000ms
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else if (time ==  0){  //when game ends (t = 0)
                        Platform.runLater(() -> lTimer.setText("0"));

                        //run reset configs
                        generalReset(tfBox, tflwWordBox, spPane);

                        //calculate, save, show score
                        int[] score = getScore(strWordList, strWordsTyped);
                        try {
                            saveScore(score);
                        } catch (IOException e) {e.printStackTrace(); }

                        lineWPM.getData().add(new XYChart.Data(intCurrentSavedGames++, score[0]));     //add new score to graph

                        Text[] formattedScore = formatScoreText(score);    //applies the font, color, size of each score property
                        tflwWordBox.setTextAlignment(TextAlignment.CENTER);
                        ObservableList list = tflwWordBox.getChildren();    //get an editable list of words in TextFlow

                        Platform.runLater(() -> {   //update the TextFlow to display the score
                            for (int i = 0; i < formattedScore.length; i++) {
                                list.add(formattedScore[i]);
                            }
                        });

                        blnGameActive = false; //game is now set to not active
                    }
                }
            }
        }
    }
}
