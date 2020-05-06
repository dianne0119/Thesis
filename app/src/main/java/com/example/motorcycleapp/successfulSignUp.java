package com.example.motorcycleapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Set;

public class successfulSignUp extends AppCompatActivity {

    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.successful_signup);

        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener((new View.OnClickListener() {
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
    public void onBackPressed(){ }
}
