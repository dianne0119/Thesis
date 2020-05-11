package com.example.motorcycleapp;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.solver.widgets.WidgetContainer;

import java.lang.reflect.Method;
import java.util.Set;

public class ActivityMainV2 extends AppCompatActivity {

    private Button signUpBtn;
    private TextView forgotPassword;
    private Button loginButton;
    private EditText Name;
    private EditText Password;
    private int counter=5;
    private boolean isCounter=true;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainv2);

        Name = (EditText) findViewById(R.id.userName);
        Password = (EditText) findViewById(R.id.pin);
        signUpBtn = findViewById(R.id.signUpbutton);

        Name.addTextChangedListener(loginTextWatcher);
        Password.addTextChangedListener(loginTextWatcher);
        forgotPassword = findViewById(R.id.forgot);
        loginButton = findViewById(R.id.loginButton);

        final SharedPreferences database;
        database = getSharedPreferences("UserInfo", MODE_PRIVATE);
        final String registeredUsername = database.getString("username","");
        final String registeredPassword = database.getString("password","");
        final String bleStat = database.getString("bluetoothStat","");
        final String bleImmoNotPaired = database.getString("bluetoothImmoNotPaired","");
        final String lockDownStatus = database.getString("lockDownStat","");

        loginButton.setEnabled(!registeredUsername.isEmpty() && !registeredPassword.isEmpty());

        if(bleStat.equals("off")) {

            SharedPreferences.Editor editor = database.edit();
            editor.putString("bluetoothStat","on");
            editor.apply();

            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMainV2.this);
            builder.setCancelable(true);
            builder.setTitle("WARNING");
            builder.setMessage("It seems that you have turned off your bluetooth. That is why you are automatically log out to the system.");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            builder.show();
        } else if (bleImmoNotPaired.equals("off")) {

            SharedPreferences.Editor editor = database.edit();
            editor.putString("bluetoothImmoNotPaired","on");
            editor.apply();

            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMainV2.this);
            builder.setCancelable(true);
            builder.setTitle("IMMOBILIZER NOT FOUND");
            builder.setMessage("It seems that you have unpaired the immobilizer. Your account has been removed by the system. Please register again.");

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            builder.show();
        }

        if (lockDownStatus.equals("on")) {

            findViewById(R.id.group).setVisibility(View.VISIBLE);
            signUpBtn.setEnabled(false);
            forgotPassword.setEnabled(false);
            loginButton.setEnabled(false);

        } else {

            signUpBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMainV2.this);
                    builder.setCancelable(true);
                    builder.setTitle("CONFIRM DELETION");
                    builder.setMessage("Creating a new account will remove your previous account entirely. Are you sure you want to continue?");

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

                            for (BluetoothDevice bt : pairedDevices) {
                                if (bt.getName().contains("IMB0001")) {
                                    try {
                                        Method method = bt.getClass().getMethod("removeBond", (Class[]) null);
                                        method.invoke(bt, (Object[]) null);

                                        SharedPreferences.Editor editor = database.edit();
                                        editor.clear();
                                        editor.apply();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            loginButton.setEnabled(false);
                            openActivitySecond21();
                        }
                    });

                    if(!registeredUsername.isEmpty()){
                        builder.show();
                    } else {
                        openActivitySecond21();
                    }

                }
            });

            forgotPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(ActivityMainV2.this);
                    builder.setCancelable(true);
                    builder.setTitle("INVALID");
                    builder.setMessage("No account yet. Please register first.");

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.cancel();
                        }
                    });


                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) { }
                    });

                    if(registeredUsername.isEmpty()){
                        builder.show();
                    } else {
                        openActivitySeventh2();
                    }

                }
            });

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String usernameValue = Name.getText().toString();
                    String passwordValue = Password.getText().toString();


                    if((usernameValue.equals(registeredUsername)) && (passwordValue.equals(registeredPassword)) ){
                        Name.setText("");
                        Password.setText("");
                        openActivityFourthV2();
                    } else if (usernameValue.isEmpty() || passwordValue.isEmpty()) {
                        Toast.makeText(ActivityMainV2.this, "Invalid! Must input username and password", Toast.LENGTH_SHORT).show();
                    } else {
                        counter --;
                        if(counter ==0){
                            loginButton.setEnabled(false);
                            isCounter = false;

                            SharedPreferences.Editor editor = database.edit();
                            editor.putString("lockDownStat","on");
                            editor.apply();

                            openActivityMainV2();
                        }
                        Toast.makeText(ActivityMainV2.this, "Invalid username and password! "+counter+" number attempts remaining", Toast.LENGTH_SHORT).show();
                    }


                }


            });
        }



    }

    private TextWatcher loginTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            SharedPreferences database;
            database = getSharedPreferences("UserInfo", MODE_PRIVATE);
            final String registeredUsername = database.getString("username","");
            final String registeredLockdownStat = database.getString("lockDownStat","");

            String nameInput = Name.getText().toString().trim();
            String passwordInput = Password.getText().toString().trim();

            loginButton.setEnabled(!nameInput.isEmpty() && !passwordInput.isEmpty() && !registeredUsername.isEmpty() && isCounter && !registeredLockdownStat.equals("on"));
        }
        @Override
        public void afterTextChanged(Editable s) {


        }
    };

    @Override
    public void onBackPressed(){

    }

    @Override
    protected void onRestart() {
        super.onRestart();

        SharedPreferences database;
        database = getSharedPreferences("UserInfo", MODE_PRIVATE);
        final String registeredLockdownStat = database.getString("lockDownStat","");

        if(registeredLockdownStat.equals("on")) {
            openActivityMainV2();
        }

    }

    public void openActivitySecond21() {
        Intent intent = new Intent(this, ActivitySecond21.class);
        startActivity(intent);
    }

    public void openActivitySeventh2() {
        Intent intent = new Intent(this, ActivitySeventh2.class);
        startActivity(intent);
    }

    public void openActivityFourthV2() {
        Intent intent = new Intent(this, ActivityFourthV2.class);
        startActivity(intent);
    }

    public void openActivityMainV2() {
        Intent intent = new Intent(this, ActivityMainV2.class);
        startActivity(intent);
    }

    public void openActivityFirst() {
        Intent intent = new Intent(this, ActivityFirst.class);
        startActivity(intent);

        // NOTE: To success fully connect to screen, declare class name in AndroidManifest.xml
    }
}
