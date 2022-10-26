package com.example.quizapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.*;
import android.view.*;
import android.widget.*;

public class LoginActivity extends AppCompatActivity {
    Button btn_start;
    EditText et_enterName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_start = findViewById(R.id.btn_start);
        et_enterName = findViewById(R.id.et_enterName);
        final int maxDigitForScreen = 24; //constant for screen size

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_enterName.getText().length() <= 0){ //if the user entered name with 0 characters, show pop up
                    Toast toast = Toast.makeText(getApplicationContext(),"Please enter a name.",Toast.LENGTH_LONG);
                    toast.show();
                }else if(et_enterName.getText().length() > maxDigitForScreen){ //if user entered name over max digit for screen, show pop up
                    Toast toast = Toast.makeText(getApplicationContext(),"Please have fewer than 24 characters.",Toast.LENGTH_LONG);
                    toast.show();
                }else{ //if username is between 0 & 24, create an intent and pass the name to the main page
                    Intent i = new Intent(LoginActivity.this,MainActivity.class);
                    i.putExtra("username",et_enterName.getText().toString());
                    startActivity(i);
                }
            }
        });
    }
}