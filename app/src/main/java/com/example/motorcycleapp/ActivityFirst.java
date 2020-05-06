package com.example.motorcycleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ActivityFirst extends AppCompatActivity {

    private Button button;

    @Override
    public void onBackPressed(){
        finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);

        button = findViewById(R.id.letsGetStartedBtn);

        final SharedPreferences database;
        database = getSharedPreferences("UserInfo", MODE_PRIVATE);
        final String registeredUsername = database.getString("username","");

        if(!registeredUsername.isEmpty()){
            openActivityMainV2();
        }

        button.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityMainV2();
            }
        }));
    }

    public void openActivityMainV2() {
        Intent intent = new Intent(this, ActivityMainV2.class);
        startActivity(intent);
    }

    @Override
    public void onRestart() {
        super.onRestart();
        final SharedPreferences database;
        database = getSharedPreferences("UserInfo", MODE_PRIVATE);
        final String registeredUsername = database.getString("username","");
        if(!registeredUsername.isEmpty()){
            openActivityMainV2();
        }
    }
}