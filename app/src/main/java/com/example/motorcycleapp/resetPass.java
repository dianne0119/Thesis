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

import static androidx.core.content.ContextCompat.startActivity;

public class resetPass extends AppCompatActivity {

    private Button loginButton;
    volatile boolean isResume = true;
    private BluetoothAdapter mBluetoothAdapter;
    private int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.successful_reset_pass);

        new Thread(new Task()).start();

        loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openActivityMainV2();
            }
        }));
    }

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

    @Override
    public void onBackPressed(){ }
}
