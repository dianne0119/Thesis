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

public class ActivitySeventh21 extends AppCompatActivity {

    private TextView signIn;
    private Button submitBtn;
    private EditText password;
    private EditText confirmPassword;
    volatile boolean isResume = true;
    private BluetoothAdapter mBluetoothAdapter;
    private int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seventh_screenv21);

        signIn = findViewById(R.id.signinBtn);
        submitBtn = findViewById(R.id.resetBtn);
        password = (EditText) findViewById(R.id.newPass);
        confirmPassword = (EditText) findViewById(R.id.confirmPin);

        password.addTextChangedListener(loginTextWatcher);
        confirmPassword.addTextChangedListener(loginTextWatcher);

        final SharedPreferences database;
        database = getSharedPreferences("UserInfo", MODE_PRIVATE);

        new Thread(new Task()).start();
        submitBtn.setEnabled(false);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityMainV2();
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String passwordValue=password.getText().toString();
                String confirmpassValue=confirmPassword.getText().toString();

                if(!passwordValue.equals(confirmpassValue) ) {
                    Toast.makeText(ActivitySeventh21.this, "Invalid! Password Does Not Match", Toast.LENGTH_SHORT).show();
                } else if (passwordValue.length()<8 || passwordValue.length()>16 ||
                        confirmpassValue.length()<8 || confirmpassValue.length()>16 ) {
                    Toast.makeText(ActivitySeventh21.this, "Invalid! Password must be 8-16 characters", Toast.LENGTH_SHORT).show();
                } else {
                    SharedPreferences.Editor editor = database.edit();
                    editor.putString("password",passwordValue);
                    editor.putString("confirmPass",confirmpassValue);
                    editor.apply();
                    openresetPass();
                }

            }
        });
    }

    private TextWatcher loginTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            String passwordInput = password.getText().toString().trim();
            String confirmPasswordInput = confirmPassword.getText().toString().trim();

            submitBtn.setEnabled(!passwordInput.isEmpty() && !confirmPasswordInput.isEmpty());
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
        new Thread(new Task()).start();
        isResume=true;
    }

    public void openActivityMainV2() {
        isResume = false;
        Intent intent = new Intent(this, ActivityMainV2.class);
        startActivity(intent);
    }

    public void openresetPass() {
        isResume = false;
        Intent intent = new Intent(this, resetPass.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){ }
}
