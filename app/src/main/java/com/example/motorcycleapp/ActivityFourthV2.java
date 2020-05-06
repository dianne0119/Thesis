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

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

public class ActivityFourthV2 extends AppCompatActivity {

    private TextView textView;
    private EditText pinNumber;
    private Button nextButton;
    private int counter=5,flag=0;
    private boolean isCounter=true;
    volatile boolean isResume = true;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth_screenv2);

        textView = findViewById(R.id.forgotPinBtn);
        pinNumber = findViewById(R.id.pin);
        nextButton = findViewById(R.id.submitBtn);

        pinNumber.addTextChangedListener(loginTextWatcher);

        final SharedPreferences database;
        database = getSharedPreferences("UserInfo", MODE_PRIVATE);
        final String registeredPinNumber = database.getString("pinNum","");

        nextButton.setEnabled(false);
        new Thread(new Task()).start();


        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivitySixthV2();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String pinNumberValue = pinNumber.getText().toString();

                if((registeredPinNumber.equals(pinNumberValue)) ){
                    pinNumber.setText("");
                    openActivityFifthV2();
                } else {
                    counter --;
                    if(counter == 0){
                        nextButton.setEnabled(false);
                        isCounter = false;

                        SharedPreferences.Editor editor = database.edit();
                        editor.putString("lockDownStat","on");
                        editor.apply();

                        openActivityMainV2();
                    }
                    Toast.makeText(ActivityFourthV2.this, "Invalid pin number! "+counter+" number attempts remaining", Toast.LENGTH_SHORT).show();
                }

            }
        });

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivitySixthV2();
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
            SharedPreferences database;
            database = getSharedPreferences("UserInfo", MODE_PRIVATE);
            final String registeredUsername = database.getString("username","");

            String nameInput = pinNumber.getText().toString().trim();

            nextButton.setEnabled(!nameInput.isEmpty() && !registeredUsername.isEmpty() && isCounter);
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

    @Override
    protected void onRestart() {
        super.onRestart();

        SharedPreferences database;
        database = getSharedPreferences("UserInfo", MODE_PRIVATE);
        final String registeredLockdownStat = database.getString("lockDownStat","");

        if(registeredLockdownStat.equals("on")) {
            openActivityMainV2();
        } else {
            new Thread(new Task()).start();
            isResume=true;
        }

    }

    public void openActivityFifthV2() {
        isResume = false;
        Intent intent = new Intent(this, ActivityFifthV2.class);
        startActivity(intent);
    }

    public void openActivitySixthV2() {
        isResume = false;
        Intent intent = new Intent(this, ActivitySixthV2.class);
        startActivity(intent);
    }

    public void openActivityMainV2() {
        isResume = false;
        Intent intent = new Intent(this, ActivityMainV2.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){ }

}
