package com.example.quizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import java.io.*;
import java.util.*;

import android.content.Intent;
import android.util.*;
import android.view.*;
import android.widget.*;

public class MainActivity extends AppCompatActivity {

    int correctAnswers = 0; //counting the correct answers
    int numOfQuestions = 0; //counting the number of questions in the quiz
    Button selectedTerm = null; //Used for comparing if term is correct
    String username; //to display user's name
    boolean nextQuestion = false; // to prevent user from hitting buttons after submitting
    String correctTerm; //holding the string for the correct term

    Button btn_term1, btn_term2, btn_term3, btn_term4, btn_submit;
    TextView tv_userName, tv_questionNum, tv_termDef;

    ArrayList<String> terms = new ArrayList<>();
    ArrayList<String> definitions = new ArrayList<>();
    ArrayList<String> buttonTerms = new ArrayList<>();
    Map<String,String> quizMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = getIntent().getStringExtra("username"); //collected from the login intent

        btn_term1 = findViewById(R.id.btn_term1);
        btn_term2 = findViewById(R.id.btn_term2);
        btn_term3 = findViewById(R.id.btn_term3);
        btn_term4 = findViewById(R.id.btn_term4);
        btn_submit = findViewById(R.id.btn_submit);
        tv_termDef = findViewById(R.id.tv_termDef);
        tv_userName = findViewById(R.id.tv_userName);
        tv_questionNum = findViewById(R.id.tv_questionNum);

        btn_term1.setOnClickListener(onTermClicked);
        btn_term2.setOnClickListener(onTermClicked);
        btn_term3.setOnClickListener(onTermClicked);
        btn_term4.setOnClickListener(onTermClicked);
        btn_submit.setOnClickListener(onSubmitClicked);

        tv_userName.append(username); //append the name to the current string

        readQuizFile(); //call the read file method and pass in definitions & terms

        for (int i = 0; i < definitions.size(); i++) { //populate the hashmap with terms and definitions
            quizMap.put(definitions.get(i),terms.get(i));
        }

        update(); //call the update method
    }//End onCreate

    public View.OnClickListener onSubmitClicked = new View.OnClickListener() {
        @SuppressLint("UseCompatLoadingForDrawables")
        @Override
        public void onClick(View view) {

            if (!nextQuestion && selectedTerm != null ) { //If the user hasn't submitted their answer, and if they have selected their button
                if (checkAnswer(selectedTerm.getText().toString())) { //call to check answer function and passing the term name
                    selectedTerm.setBackground(getDrawable(R.drawable.correct_answer)); //if it's correct highlight green
                } else {
                    selectedTerm.setBackground(getDrawable(R.drawable.incorrect_answer)); //if its incorrect highlight re
                }

                nextQuestion = true; //So user can't press buttons until the next page was called
                selectedTerm = null; //reset the selected term

                if (numOfQuestions == quizMap.size()){ //if the number of questions gone through is the size of the quiz
                    btn_submit.setText(R.string.btn_finish); //change submit button text to finish
                }else{
                    btn_submit.setText(R.string.btn_next); // change submit button text to next question
                }

            }else{ //If the user has submitted their answer
                if (definitions.size() == 0) { //if there are no more definitions
                    //create a new intent and pass through the score, number of questions, and username to the result page
                    Intent i = new Intent(MainActivity.this,ResultsActivity.class);
                    i.putExtra("score",Integer.toString(correctAnswers));
                    i.putExtra("numOfQuestions",Integer.toString(numOfQuestions));
                    i.putExtra("username",username);
                    startActivity(i);
                }else{ //if there are still definitions to go through
                    update(); //update the page
                    nextQuestion = false; //reset next question
                    btn_submit.setText(R.string.btn_submit); //reset text on submit button
                    defaultTermColor(btn_term1,btn_term2,btn_term3,btn_term4); //change all terms to their default color
                }
            }
        }
    };//End on submit

    public View.OnClickListener onTermClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (!nextQuestion) { //only selectable if the user hasn't submitted their answer
                defaultTermColor(btn_term1,btn_term2,btn_term3,btn_term4); //reset all colors to the term buttons
                switch (view.getId()) { //Switch to determine which button was pushed
                    case (R.id.btn_term1):
                        selectButton(btn_term1);//call to method to change color of selected button
                        selectedTerm = btn_term1; //Assign the proper button to the selected term button for comparing
                        break;
                    case (R.id.btn_term2):
                        selectButton(btn_term2);
                        selectedTerm = btn_term2;
                        break;
                    case (R.id.btn_term3):
                        selectButton(btn_term3);
                        selectedTerm = btn_term3;
                        break;
                    case (R.id.btn_term4):
                        selectButton(btn_term4);
                        selectedTerm = btn_term4;
                        break;
                    default:
                        break;
                }
            }
        }
    };

    @SuppressLint("UseCompatLoadingForDrawables")
    protected void selectButton(Button btn){
        //Sets text and background color to highlight the selected term
        btn.setBackground(getDrawable(R.drawable.rounded_corner_submit));
        btn.setTextColor(getColor(R.color.white));
    }//end selected button color method

    @SuppressLint("UseCompatLoadingForDrawables")
    protected void defaultTermColor(Button btn1,Button btn2,Button btn3,Button btn4){
        //Grabs all buttons, and sets their text, and background color to their default
        btn1.setBackground(getDrawable(R.drawable.rounded_corner_terms));
        btn1.setTextColor(getColor(R.color.dark_grey));
        btn2.setBackground(getDrawable(R.drawable.rounded_corner_terms));
        btn2.setTextColor(getColor(R.color.dark_grey));
        btn3.setBackground(getDrawable(R.drawable.rounded_corner_terms));
        btn3.setTextColor(getColor(R.color.dark_grey));
        btn4.setBackground(getDrawable(R.drawable.rounded_corner_terms));
        btn4.setTextColor(getColor(R.color.dark_grey));
    }//End default color method

    protected void readQuizFile(){
        String errorMsg; // string to log
        String line; // line taken from readLine
        String[] lineSplit; // hold the split line
        BufferedReader buffer; //reading input stream

        try {
            InputStream is = getResources().openRawResource(R.raw.quiz); //new input stream targeted at the quiz file
            buffer = new BufferedReader(new InputStreamReader(is)); //new buffer reader targeted at the input stream

            while ((line = buffer.readLine()) != null){ // while there is another line in the buffer
                lineSplit = line.split(" \\$ "); //split the line with the '$' token
                definitions.add(lineSplit[0]); //add the first half of the split to definitions
                terms.add(lineSplit[1]);//add the second half of the split to terms
            }

            is.close(); //close the input stream

            //throw new IOException(); test that they will send to log

        } catch (IOException e) { //IO exception
            errorMsg = "Unable to read file.";
            Log.e("ERROR",errorMsg); // Log the error
        }catch (Exception e){ //Default exception
            errorMsg = "Unknown error occurred";
            Log.e("ERROR",errorMsg); // Log the error
        }
    }//End read method

    protected boolean checkAnswer(String selectedTerm){
        boolean isCorrect = false;
        if (Objects.equals(selectedTerm, correctTerm)){//check if the selected term is the correct one
            correctAnswers++; //add to correct answers variable
            isCorrect = true; //return true
        }
        definitions.remove(0); //remove that definition so there's a timer
        return isCorrect;
    }//end check answer method

    protected void update(){
        String displayNum = "QUESTION " + (numOfQuestions + 1); //Display what question num the user is on
        tv_questionNum.setText(displayNum);
        numOfQuestions++;

        Collections.shuffle(definitions); //shuffle the definitions
        correctTerm = quizMap.get(definitions.get(0)); //Grab the first definition to display, and find its correct term in the map

        while (!Objects.equals(terms.get(0), correctTerm)){ //shuffle the term array list until the first term matches the definition term
            Collections.shuffle(terms);
        }

        for (int i = 0; i < 4; i++) {
            buttonTerms.add(terms.get(i)); //Add the first four terms to another array list
        }

        Collections.shuffle(buttonTerms); //Shuffle array list so the correct term is on a random button each time

        //Set the text of all the term buttons and definition display
        tv_termDef.setText(definitions.get(0));
        btn_term1.setText(buttonTerms.get(0));
        btn_term2.setText(buttonTerms.get(1));
        btn_term3.setText(buttonTerms.get(2));
        btn_term4.setText(buttonTerms.get(3));

        buttonTerms.clear();//clear the button list for next update
    }//End update Method

}//End Main