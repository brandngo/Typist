package ca.brandonngo.typist;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.Effect;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.scene.text.Font;


import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Util {
    //Typist theme colors
    public static String AQUA_GREEN = "#21e6c1";
    public static String LIGHT_BLUE = "#278ea5";
    public static String NAVY_BLUE = "#1f4287";
    public static String DARK_BLUE = "#071e3d";

    /* Stores lines from a file to a string array
       Requires: Path of text file
       Returns: String[]
    */
    public String[] getArrayFromFile(String URL) {
        int intIteratorIndex = 0;
        String[] strArray = new String[400];    //String array of size 400
        Scanner file = null;    //if path not found

        try {
            file = new Scanner(new File(URL));  //open file
            while (file.hasNextLine()) {
                strArray[intIteratorIndex] = file.nextLine();   //read line
                intIteratorIndex++;
            }
            file.close();   //close file
        } catch (Exception e) {
            e.printStackTrace();
        }

        return strArray;
    }

    /* Generates random word list
       Requires: n/a
       Returns: String[]
    */
    public String[] generateWordList() {
        int intIndex = 0;
        String[] strWordFile = getArrayFromFile("src/wordbank.txt");    //get word bank array
        String[] strWordList = new String[200]; //initialise word list array

        while (intIndex < 199) {    //while word list not full
            int intTokenRandom = (int)(Math.random() * 399);    //generate a random number on range of 0 to 399
            strWordList[intIndex] = strWordFile[intTokenRandom];    //set the random word into word list
            intIndex++;
        }

        return strWordList;
    }

    /* Add words to textflow
       Requires: word list, TextFlow component
       Returns: Text[]
    */
    public static Text[] populateTflwWordBox(String[] wordList, TextFlow tflwWordBox) {
        Text[] tWord = new Text[wordList.length];   //initialise Text[]
        ObservableList list = tflwWordBox.getChildren();    //get editable list of TextFlow
        for (int i = 0; i<tWord.length-1; i++) {    //iterate through whole word list
            tWord[i] = new Text(wordList[i] + " "); //separate with a space

            tWord[i].setFont(Font.font("arial", FontWeight.NORMAL, FontPosture.REGULAR, 50));   //set font of word
            list.add(tWord[i]); //add word to TextFlow
        }
        return tWord;
    }

    /* Generate line data points and add to graph
       Requires: LineChart component and y values
       Returns: XYChart.Series (line data set)
    */
    public static XYChart.Series populateLineChart(LineChart lc, ArrayList<Integer> wpmData) {
        XYChart.Series wpm = new XYChart.Series();  //create new data set

        for (int i = 0; i<wpmData.size(); i++) {
            wpm.getData().add(new XYChart.Data(i, wpmData.get(i))); //add each data point
        }
        lc.getData().add(wpm);  //add data set to graph

        return wpm; //return data set
    }

    /* Determines index of word to trigger scroll event
       Requires: TextFlow component, Text array of words, previous scroll trigger index
       Returns: int
    */
    public static int getLastTextIndexInRow(TextFlow tflwWordBox, Text[] tWordArray, int intPreviousIndex) {
        double dblSizeOfRow = tflwWordBox.getWidth();   //gets the total size which a row can be
        double dblTotalTextSize = -0.02;    //stores the text size to a certain index (set to -0.02 to account for occasional floating point errors)
        int intIndex = 5; //stores index of word that triggers the scroll event (set to 5 to account for potential failure of the method)

        //scan 10 words and see how many fit in one row, if exceeds then index is the number of words in that one row
        for (int word = intPreviousIndex; word < intPreviousIndex+10; word++) {
            dblTotalTextSize += tWordArray[word].getLayoutBounds().getWidth();  //note that each Text object already has the space separator accounted for

            double dblNextTextSize = tWordArray[word + 1].getLayoutBounds().getWidth();    //gets the size of the next word
            if ((dblNextTextSize + dblTotalTextSize) > dblSizeOfRow) {  //determines if the next word will not fit onto the row
                intIndex = word;    //set scroll trigger index
                break;
            }
        }
        return intIndex;
    }

    /* Determines if word is spelled correctly (case sensitive)
       Requires: two strings
       Returns: boolean
    */
    public static boolean isWordCorrect(String strWord1, String strWord2) {
        if (strWord1.equals(strWord2)) {
            return true;
        } else return false;
    }

    /* Calculates score and other metrics
       Requires: word list, words typed
       Returns: int[] --> [WPM][right words][wrong words][characters][right characters][wrong characters]
    */
    public static int[] getScore(String[] strWordList, ArrayList<String> strWordsTyped) {
        int intRightWords = 0;
        int intWrongWords = 0;
        int intWPM;
        final double intTimeInMin = 1;    //game time in minutes

        int intScore[] = new int[6];    //[WPM][right words][wrong words][characters][right characters][wrong characters]

        //net variables are used for the calculation of WPM exclusively
        int intNetCharacters = 0;
        int intRightCharacters = 0;
        int intWrongCharacters = 0;
        double dblNetWords;

        //find # of characters and number of right/wrong words and characters
        for (int i = 0; i<strWordsTyped.size(); i++) {  //iterates for the length of the arraylist
            intNetCharacters += strWordsTyped.get(i).length();
            if (isWordCorrect(strWordsTyped.get(i), strWordList[i])) {  //checks if word is correct
                intRightWords++;
                intRightCharacters += strWordsTyped.get(i).length();    //takes number of characters in the word
            } else {
                intWrongWords++;
                intWrongCharacters += strWordsTyped.get(i).length();
            }
        }

        //calculate WPM
        dblNetWords = intNetCharacters / 5; //each "word" should be 5 characters
        intWPM = (int)(Math.round((Math.abs(dblNetWords-(intWrongWords*0.6)/intTimeInMin)*100)/100.0)); //algorithm for finding WPM

        //pack metrics
        intScore[0] = intWPM;
        intScore[1] = intRightWords;
        intScore[2] = intWrongWords;
        intScore[3] = intNetCharacters;
        intScore[4] = intRightCharacters;
        intScore[5] = intWrongCharacters;

        return intScore;
    }

    /* Applies text properties to score data
       Requires: score array (set of metrics packaged in int[])
       Returns: Text[] --> [WPM][characters][right characters][wrong characters][right words][wrong words]
    */
    public static Text[] formatScoreText(int[] score) {
        Text[] formattedText = new Text[6];

        //WPM
        Text tWPM = new Text(score[0] + " WPM\n");
            tWPM.setFont(Font.font("arial", FontWeight.NORMAL, FontPosture.REGULAR, 80));
            tWPM.setFill(Color.SLATEBLUE);
            Effect glow = new Glow(1.0);
            tWPM.setEffect(glow);
            tWPM.setUnderline(true);

        //Characters
        Text tChars = new Text("Characters: " + score[3] + "   ");
            tChars.setFont(Font.font("arial", FontWeight.NORMAL, FontPosture.REGULAR, 20));
        Text tRightChars = new Text(" "+score[4]);
            tRightChars.setFont(Font.font("arial", FontWeight.NORMAL, FontPosture.REGULAR, 20));
            tRightChars.setFill(Color.GREEN);
        Text tWrongChars = new Text("|"+score[5]);
            tWrongChars.setFont(Font.font("arial", FontWeight.NORMAL, FontPosture.REGULAR, 20));
            tRightChars.setFill(Color.RED);

        //Words
        Text tRightWords = new Text("\nRight Words: " + score[1] + "   ");
            tRightWords.setFont(Font.font("arial", FontWeight.NORMAL, FontPosture.REGULAR, 30));
        Text tWrongWords = new Text("Wrong Words: " + score[2]);
            tWrongWords.setFont(Font.font("arial", FontWeight.NORMAL, FontPosture.REGULAR, 30));

        //pack formatted text
        formattedText[0] = tWPM;
        formattedText[1] = tChars;
        formattedText[2] = tRightChars;
        formattedText[3] = tWrongChars;
        formattedText[4] = tRightWords;
        formattedText[5] = tWrongWords;

        return formattedText;
    }

    /* Removes element contents, does not replace specific game logic variables
       Requires: TextField component, TextFlow component, ScrollPane component
       Returns: n/a
    */
    public static void generalReset(TextField tfBox, TextFlow tflwWordBox, ScrollPane spPane) {
        tfBox.setText("");  //clear user input field
        tfBox.setEditable(false);   //prevent input from player while game is inactive

        spPane.setVvalue(0);    //reset viewport

        //clear the current list of words/score display
        try {
            tflwWordBox.getChildren().clear();
        } catch (IllegalStateException e) { //if this method happens to be run from a different thread
            Platform.runLater(() -> tflwWordBox.getChildren().clear());
        }
    }

    /* Writes score metrics to text file
       Requires: score array (set of metrics packaged in int[])
       Returns: n/a
    */
    public static void saveScore(int[] score) throws IOException {
        BufferedWriter scoresList = new BufferedWriter(new FileWriter("src/scoreslist.txt", true)); //open text file and add to it
        scoresList.write(score[0] + "." + score[1] + "." + score[2] + "." + score[3] + "." + score[4] + "." + score[5] +"\n");  //"." will be the regex separating each metric
        scoresList.close(); //close text file
    }

    /* Takes the WPM score from scores text file
       Requires: n/a
       Returns: ArrayList<Integer>
    */
    public static ArrayList<Integer> extractWPM() {
        ArrayList<Integer> listWPM = new ArrayList<>(); //initialise list
        Scanner file = null;    //if path cannot be found
        try {
            file = new Scanner(new File("src/scoreslist.txt")); //set file
            while (file.hasNextLine()) {    //while file is not empty
                String score = file.nextLine(); //read line
                listWPM.add(Integer.parseInt(score.substring(0,score.indexOf("."))));   //add WPM (the number before the first ".")
            }
            file.close();   //close file
        } catch (Exception e) {
            e.printStackTrace();
        }

        return listWPM;
    }
}
