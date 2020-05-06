package com.example.motorcycleapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivityThirdV2 extends AppCompatActivity {

    private Button submitButton;
    private EditText userName;
    private EditText pinNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_screenv2);

        submitButton = findViewById(R.id.nextBtn);
        userName = findViewById(R.id.name);
        pinNumber = findViewById(R.id.pinNum);

        userName.addTextChangedListener(loginTextWatcher);
        pinNumber.addTextChangedListener(loginTextWatcher);

        final SharedPreferences database;
        database = getSharedPreferences("UserInfo", MODE_PRIVATE);

        final Pattern pattern = Pattern.compile("[a-zA-Z0-9]*");

        submitButton.setEnabled(false);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String usernameValue=userName.getText().toString();
                String pinNumValue=pinNumber.getText().toString();

                Matcher matcher = pattern.matcher(usernameValue);

                if (!matcher.matches()) {
                    Toast.makeText(ActivityThirdV2.this, "Invalid! Username must contain with letters and numbers only", Toast.LENGTH_SHORT).show();
                } else if (usernameValue.length()<8 || usernameValue.length()>16){
                    Toast.makeText(ActivityThirdV2.this, "Invalid! Username must be 8-16 characters", Toast.LENGTH_SHORT).show();
                } else if ( pinNumValue.length() != 4) {
                    Toast.makeText(ActivityThirdV2.this, "Invalid! PIN number must contain 4 numbers only", Toast.LENGTH_SHORT).show();
                } else {
                    @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = database.edit();
                    editor.putString("username",usernameValue);
                    editor.putString("pinNum",pinNumValue);
                    editor.apply();

                    openActivityThird21();

                }

            }
        });
    }

    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {  }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String nameInput = userName.getText().toString().trim();
            String pinNumberInput = pinNumber.getText().toString().trim();

            submitButton.setEnabled(!nameInput.isEmpty() && !pinNumberInput.isEmpty());
        }
        @Override
        public void afterTextChanged(Editable s) { }
    };

    public void openActivityThird21() {
        Intent intent = new Intent(this, ActivityThird21.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){ }
}
