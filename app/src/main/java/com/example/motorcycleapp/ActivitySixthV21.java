package com.example.motorcycleapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Set;

public class ActivitySixthV21 extends AppCompatActivity {

    private Button submitButton;
    private TextView textView;
    private EditText pin;
    private EditText confirmPin;
    volatile boolean isResume = true;
    private BluetoothAdapter mBluetoothAdapter;
    private int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sixth_screenv21);

        submitButton = findViewById(R.id.resetBtn);
        textView = findViewById(R.id.textView20);
        pin = (EditText) findViewById(R.id.pin);
        confirmPin = (EditText) findViewById(R.id.confirmPin);

        pin.addTextChangedListener(loginTextWatcher);
        confirmPin.addTextChangedListener(loginTextWatcher);

        final SharedPreferences database;
        database = getSharedPreferences("UserInfo", MODE_PRIVATE);

        submitButton.setEnabled(false);
        new Thread(new Task()).start();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String pinValue=pin.getText().toString();
                String confirmPinValue=confirmPin.getText().toString();

                if(!pinValue.equals(confirmPinValue) ) {
                    Toast.makeText(ActivitySixthV21.this, "Invalid! Pin Number Does Not Match", Toast.LENGTH_SHORT).show();
                } else if (pinValue.length() != 4  || confirmPinValue.length() !=4) {
                    Toast.makeText(ActivitySixthV21.this, "Invalid! Pin Number must contain 4 numbers only", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences.Editor editor = database.edit();
                    editor.putString("pinNum",pinValue);
                    editor.apply();

                    pin.setText("");
                    confirmPin.setText("");
                    openresetPin();
                }
            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityFourthV2();
            }
        });

    }

    //disable if Input boxes are empty
    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String passwordInput = pin.getText().toString().trim();
            String confirmPasswordInput = confirmPin.getText().toString().trim();

            submitButton.setEnabled(!passwordInput.isEmpty() && !confirmPasswordInput.isEmpty());
        }
        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    class Task implements Runnable {

        @Override
        public void run() {
            BluetoothAdapter myBluetooth;
            myBluetooth = BluetoothAdapter.getDefaultAdapter();
            final SharedPreferences database;
            database = getSharedPreferences("UserInfo", MODE_PRIVATE);

            while (isResume == true) {

                if (!myBluetooth.isEnabled()) {

                    SharedPreferences.Editor editor = database.edit();
                    editor.putString("bluetoothStat","off");
                    editor.apply();

                    openActivityMainV2();

                } else {

                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                    for (BluetoothDevice bt : pairedDevices) {
                        if (bt.getName().contains("IMB0001")) {
                            flag=1;
                        }
                    }

                    if(flag==0) {

                        SharedPreferences.Editor editor = database.edit();
                        editor.clear();
                        editor.apply();

                        editor.putString("bluetoothImmoNotPaired","off");
                        editor.apply();
                        openActivityMainV2();
                    }
                }
            }
        }
    }

    public void openActivityMainV2() {
        isResume = false;
        Intent intent = new Intent(this, ActivityMainV2.class);
        startActivity(intent);
    }

    public void openActivityFourthV2() {
        isResume = false;
        Intent intent = new Intent(this, ActivityFourthV2.class);
        startActivity(intent);
    }

    public void openresetPin() {
        isResume = false;
        Intent intent = new Intent(this, resetPin.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){ }
}
