package com.example.quizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class ResultsActivity extends AppCompatActivity {
    Button btn_restart;
    TextView tv_score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        btn_restart = findViewById(R.id.btn_restart);
        tv_score = findViewById(R.id.tv_score);

        String score, numOfQuestions, username, tag; //variables from the intent to display to user

        //grab the variables from the intent
        username = getIntent().getStringExtra("username");
        score = getIntent().getStringExtra("score");
        numOfQuestions = getIntent().getStringExtra("numOfQuestions");

        //if the score is the same as the number of questions, say proper message tag
        if (Integer.parseInt(score) == Integer.parseInt(numOfQuestions)){
            tag = "Good Job";
        }else{
            tag = "Better Luck Next Time";
        }

        //display the end result message
        String displayScore = "\n" + score + "/" + numOfQuestions + "\n" + tag + " " + username + "!";
        tv_score.append(displayScore);

        btn_restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //create new intent and pass through the username if the user has hit the restart button
                Intent i = new Intent(ResultsActivity.this,MainActivity.class);
                i.putExtra("username",username);
                startActivity(i);
            }
        });
    }
}