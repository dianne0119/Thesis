package com.example.motorcycleapp;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import java.util.concurrent.TimeUnit;

public class ActivityFifthV2 extends AppCompatActivity {

    private LottieAnimationView animation_view;
    private Button changeKeyBtn;
    private TextView manualDesc;
    private TextView resetDesc;
    private Button ResetBtn;
    private TextView logoutBtn;

    //nc3STnt2
    private final static String TAG = "SLAUGHTER";
    int i,v,f,h;
    String Alphanumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZ" + "abcdefghijklmnopqrstuvxyz" + "0123456789";

    int flag =0;
    int atmtc =0;

    volatile boolean isResume=true;
    private volatile boolean exitThread4 = false;
    private volatile boolean exitThread5 = false;
    private SharedPreferences database;

    private BluetoothAdapter mBluetoothAdapter;
    private String mDeviceName="IMB0001";
    private String mDeviceAddress="90:E2:02:04:84:7C";
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothGattCharacteristic characteristicTX;
    private BluetoothGattCharacteristic characteristicRX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fifth_screenv2);

        logoutBtn = findViewById(R.id.logOutButton);
        changeKeyBtn=findViewById(R.id.changeKeyBtn);
        manualDesc=findViewById(R.id.manualDesc);
        resetDesc=findViewById(R.id.resetDesc);
        ResetBtn=findViewById(R.id.ResetBtn);
        animation_view=findViewById(R.id.animation_view);

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        database = getSharedPreferences("UserInfo", MODE_PRIVATE);

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE not supported", Toast.LENGTH_SHORT).show();
            finish();
        }

        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothLeService.close();
                SharedPreferences.Editor editor = database.edit();
                editor.putString("NotLoggedOut","off");
                editor.apply();
                openActivityMainV2();
            }
        });

        changeKeyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                manualChangeKeyCodeThread thread2 = new manualChangeKeyCodeThread();
                thread2.setPriority(2);
                thread2.start();

                animation_view.setAnimation("manual_check.json");
                animation_view.playAnimation();
                animation_view.addAnimatorListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        resetDesc.setVisibility(View.INVISIBLE);
                        manualDesc.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        manualDesc.setVisibility(View.VISIBLE);
                        if (manualDesc.getVisibility() == View.VISIBLE) {
                            resetDesc.setVisibility(View.INVISIBLE);
                        }
                        else {
                            resetDesc.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });

                changeKeyBtn.setEnabled(false);
                ResetBtn.setEnabled(false);
                Timer buttonTimer = new Timer();
                buttonTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                changeKeyBtn.setEnabled(true);
                                ResetBtn.setEnabled(true);
                            }
                        });
                    }
                }, 1);

            }
        });

        ResetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                manualResetKeyCodeThread thread3 = new manualResetKeyCodeThread();
                thread3.setPriority(3);
                thread3.start();

                animation_view.setAnimation("reset_check.json");
                animation_view.playAnimation();
                animation_view.addAnimatorListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        manualDesc.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        resetDesc.setVisibility(View.VISIBLE);
                        if (resetDesc.getVisibility() == View.VISIBLE) {
                            manualDesc.setVisibility(View.INVISIBLE);
                        }
                        else {
                            manualDesc.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });

                changeKeyBtn.setEnabled(false);
                ResetBtn.setEnabled(false);
                Timer buttonTimer = new Timer();
                buttonTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                changeKeyBtn.setEnabled(true);
                                ResetBtn.setEnabled(true);
                            }
                        });
                    }
                }, 1);

            }
        });

    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }

            mDeviceName = "IMB0001";
            mDeviceAddress = "90:E2:02:04:84:7C";
            mBluetoothLeService.connect(mDeviceAddress);

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };


    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                setupSerial();
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                if(isResume == true) {
                    receiveData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
                }
            }
        }
    };


    private final ExpandableListView.OnChildClickListener servicesListClickListner = new ExpandableListView.OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
            if (mGattCharacteristics != null) {
                final BluetoothGattCharacteristic characteristic = mGattCharacteristics.get(groupPosition).get(childPosition);
                final int charaProp = characteristic.getProperties();
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                    if (mNotifyCharacteristic != null) {
                        mBluetoothLeService.setCharacteristicNotification(mNotifyCharacteristic, false);
                        mNotifyCharacteristic = null;
                    }
                    mBluetoothLeService.readCharacteristic(characteristic);
                }
                if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                    mNotifyCharacteristic = characteristic;
                    mBluetoothLeService.setCharacteristicNotification(characteristic, true);
                }
                return true;
            }
            return false;
        }
    };

    private void setupSerial() {
        String uuid;
        String unknownServiceString = getResources().getString(
                R.string.unknown_service);

        for (BluetoothGattService gattService : mBluetoothLeService.getSupportedGattServices()) {

            uuid = gattService.getUuid().toString();
            System.out.println("UUID:"+ uuid);

            if (SampleGattAttributes.lookup(uuid, unknownServiceString) == "HM 10 Serial") {
                characteristicTX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
                characteristicRX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
                mBluetoothLeService.setCharacteristicNotification(characteristicRX, true);
                break;
            }
        }
    }

    private void receiveData(String data) {

       // System.out.println("DATA RECEIVED:" + data);

        try {
            v = Integer.parseInt(String.valueOf(data.charAt(0)));
            f = Integer.parseInt(String.valueOf(data.charAt(1)));
            h = Integer.parseInt(String.valueOf(data.charAt(2)));
        } catch (NumberFormatException nfe) {
        //   unpairBluetooth();
        }

//        System.out.print("V:" + v);
//        System.out.print(", F:" + f);
//        System.out.println(", H:" + h);

        if(h ==1) {
            unpairBluetooth();
        }
		
        if(v == 1 && f == 1) {
            flag = 1;
            atmtc = 1;
        }

        if(v == 0 && f == 1) {
            flag=0;
            if(flag==0 && atmtc ==1) {

                automaticKeyCodeThread thread4 = new automaticKeyCodeThread();
                thread4.setPriority(4);
                thread4.start();

            }
        }
    }

    private static IntentFilter makeGattUpdateIntentFilter() {

        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


    public class manualChangeKeyCodeThread extends Thread {
        private volatile boolean exitThread2 = false;

        @Override
        public void run() {
            while (!exitThread2) {
                System.out.println("Thread2 is starting...");

                // [ START ] Implement Hybrid Encryption ( AES - RSA )
                //generate random keycode
                System.out.println("MANUAL .........");

                StringBuilder keycode = new StringBuilder(16);
                for (i = 0; i <16; i++) {
                    int index = (int)(Alphanumeric.length() * Math.random());
                    keycode.append(Alphanumeric.charAt(index));
                }
                System.out.println("KEYCODE (GENERATED) : " + keycode);

                //KEYCODE ENCRYPT
                long startTime = System.nanoTime(); //start time of execution

                String plaintext_encrypt = AES.Key(keycode.toString());
                System.out.println("String Hex [ AES Cipher ] " + plaintext_encrypt);

                //Encrypt Ciphertext and AES Key with RSA Key [ COMPLEX DATA ]
                String[] rsa_cipher_encrypt = RSA.Send(plaintext_encrypt); //CipherText
                long endTime = System.nanoTime();   //end time of execution

                // get difference of two nanoTime values
                long timeElapsed = endTime - startTime;
                System.out.println("Execution time in microseconds  : " + (timeElapsed * 0.0010));

                StringBuilder stringBuilder = new StringBuilder();
                int i = 0;
                String message = "";
                long startSend = System.nanoTime(); //start time of sending
                for (i = 0; i < 32; i++)
                {
                    if ( i % 10 == 0 )
                    {
                        message = stringBuilder.toString();
//                        System.out.println("MESSAGE" + message );
                        Log.d(TAG, "Sending: " + message);
                        final byte[] tx = message.getBytes();
                        if (mConnected) {
                            characteristicTX.setValue(tx);
                            mBluetoothLeService.writeCharacteristic(characteristicTX);
                        }
                        stringBuilder = new StringBuilder();

                        try {
                            TimeUnit.MILLISECONDS.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    stringBuilder.append(rsa_cipher_encrypt[i]);

                    if ( i % 30 == 0 || i % 31 == 0 )
                    {
                        message = stringBuilder.toString();
                        Log.d(TAG, "Sending: " + message);
                        final byte[] tx = message.getBytes();
                        if (mConnected) {
                            characteristicTX.setValue(tx);
                            mBluetoothLeService.writeCharacteristic(characteristicTX);
                        }
                        stringBuilder = new StringBuilder();

                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }

                long endSend = System.nanoTime();   //end time of Sending
                // get difference of two nanoTime values
                long timeSent = endSend - startSend;
                System.out.println("\nExecution time in microseconds  : " + (timeSent * 0.0010));
                System.out.println("SENT...");
                // [ END ] Implement Hybrid Encryption ( AES - RSA )

                exitThread2 = true; //stop thread2
                System.out.println("Thread2 is done...");
            }
        }
    }

    public class manualResetKeyCodeThread extends Thread {
        private volatile boolean exitThread3 = false;

        @Override
        public void run() {
            while (!exitThread3) {
                System.out.println("Thread3 is starting...");

                // [ START ] Implement Hybrid Encryption ( AES - RSA )
                System.out.println("RESET BTN .........");

                String keycode = "keyc123400000000";
                System.out.println("KEYCODE (DEFAULT) : " + keycode);

                //KEYCODE ENCRYPT
                long startTime = System.nanoTime(); //start time of execution

                String plaintext_encrypt = AES.Key(keycode.toString());
                System.out.println("String Hex [ AES Cipher ] " + plaintext_encrypt);

                //Encrypt Ciphertext and AES Key with RSA Key [ COMPLEX DATA ]
                String[] rsa_cipher_encrypt = RSA.Send(plaintext_encrypt); //CipherText
                long endTime = System.nanoTime();   //end time of execution

                // get difference of two nanoTime values
                long timeElapsed = endTime - startTime;
                System.out.println("Execution time in microseconds  : " + (timeElapsed * 0.0010));

                StringBuilder stringBuilder = new StringBuilder();
                int i = 0;
                String message = "";
                long startSend = System.nanoTime(); //start time of sending
                for (i = 0; i < 32; i++)
                {
                    if ( i % 10 == 0 )
                    {
                        message = stringBuilder.toString();
//                        System.out.println("MESSAGE" + message );
                        Log.d(TAG, "Sending: " + message);
                        final byte[] tx = message.getBytes();
                        if (mConnected) {
                            characteristicTX.setValue(tx);
                            mBluetoothLeService.writeCharacteristic(characteristicTX);
                        }
                        stringBuilder = new StringBuilder();

                        try {
                            TimeUnit.MILLISECONDS.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    stringBuilder.append(rsa_cipher_encrypt[i]);

                    if ( i % 30 == 0 || i % 31 == 0 )
                    {
                        message = stringBuilder.toString();
                        Log.d(TAG, "Sending: " + message);
                        final byte[] tx = message.getBytes();
                        if (mConnected) {
                            characteristicTX.setValue(tx);
                            mBluetoothLeService.writeCharacteristic(characteristicTX);
                        }
                        stringBuilder = new StringBuilder();

                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }

                long endSend = System.nanoTime();   //end time of Sending
                // get difference of two nanoTime values
                long timeSent = endSend - startSend;
                System.out.println("\nExecution time in microseconds  : " + (timeSent * 0.0010));
                System.out.println("SENT...");
                // [ END ] Implement Hybrid Encryption ( AES - RSA )

                exitThread3 = true; //stop thread3
                System.out.println("Thread3 is done...");
            }
        }
    }

    public class automaticKeyCodeThread extends Thread {

        @Override
        public void run() {

            System.out.println("Thread4 is starting...");
            while (!exitThread4) {

                // [ START ] Implement Hybrid Encryption ( AES - RSA )
                //generate random keycode
                System.out.println("AUTOMATIC .........");

                StringBuilder keycode = new StringBuilder(16);
                for (i = 0; i <16; i++) {
                    int index = (int)(Alphanumeric.length() * Math.random());
                    keycode.append(Alphanumeric.charAt(index));
                }
                System.out.println("KEYCODE (GENERATED) : " + keycode);

                //KEYCODE ENCRYPT
                long startTime = System.nanoTime(); //start time of execution

                String plaintext_encrypt = AES.Key(keycode.toString());
                System.out.println("String Hex [ AES Cipher ] " + plaintext_encrypt);

                //Encrypt Ciphertext and AES Key with RSA Key [ COMPLEX DATA ]
                String[] rsa_cipher_encrypt = RSA.Send(plaintext_encrypt); //CipherText
                long endTime = System.nanoTime();   //end time of execution

                // get difference of two nanoTime values
                long timeElapsed = endTime - startTime;
                System.out.println("Execution time in microseconds  : " + (timeElapsed * 0.0010));

                StringBuilder stringBuilder = new StringBuilder();
                int i = 0;
                String message = "";
                long startSend = System.nanoTime(); //start time of sending
                for (i = 0; i < 32; i++)
                {
                    if ( i % 10 == 0 )
                    {
                        message = stringBuilder.toString();
//                        System.out.println("MESSAGE" + message );
                        Log.d(TAG, "Sending: " + message);
                        final byte[] tx = message.getBytes();
                        if (mConnected) {
                            characteristicTX.setValue(tx);
                            mBluetoothLeService.writeCharacteristic(characteristicTX);
                        }
                        stringBuilder = new StringBuilder();

                        try {
                            TimeUnit.MILLISECONDS.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    stringBuilder.append(rsa_cipher_encrypt[i]);

                    if ( i % 30 == 0 || i % 31 == 0 )
                    {
                        message = stringBuilder.toString();
                        Log.d(TAG, "Sending: " + message);
                        final byte[] tx = message.getBytes();
                        if (mConnected) {
                            characteristicTX.setValue(tx);
                            mBluetoothLeService.writeCharacteristic(characteristicTX);
                        }
                        stringBuilder = new StringBuilder();

                        try {
                            TimeUnit.MILLISECONDS.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }

                long endSend = System.nanoTime();   //end time of Sending
                // get difference of two nanoTime values
                long timeSent = endSend - startSend;
                System.out.println("\nExecution time in microseconds  : " + (timeSent * 0.0010));
                System.out.println("SENT...");
                // [ END ] Implement Hybrid Encryption ( AES - RSA )

                flag = 0;
                atmtc = 0;

            }
        }
    }

    public class checkBluetoothThread extends Thread {

        @Override
        public void run() {
            System.out.println("Thread5 is starting...");

            while (!exitThread5) {

                BluetoothAdapter myBluetooth;
                myBluetooth = BluetoothAdapter.getDefaultAdapter();
                final SharedPreferences database;
                database = getSharedPreferences("UserInfo", MODE_PRIVATE);

                if (!myBluetooth.isEnabled()) {

                    SharedPreferences.Editor editor = database.edit();
                    editor.putString("bluetoothStat","off");
                    editor.apply();

                    openActivityMainV2();

                }
            }
        }
    }

	private void unpairBluetooth() {
		mBluetoothLeService.close();
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
            openActivityMainV2();
	}
	
    @Override
    protected void onRestart() {
        super.onRestart();
        isResume=false;
        exitThread4 = true; //stop thread4
        System.out.println("Thread4 is done...");
        openScreen4();
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("NAA KOS ON RESUME");

        checkBluetoothThread thread5 = new checkBluetoothThread();
        thread5.setPriority(5);
        thread5.start();

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        for (BluetoothDevice bt : pairedDevices) {
            System.out.println(bt.getName());
            if (bt.getName().contains("IMB0001")) {
//                immoNamee.setText(bt.getName());
                flag=1;
            }
        }

        System.out.println("FLAG:" + flag);

        if(flag==0) {

            SharedPreferences.Editor editor = database.edit();
            editor.clear();
            editor.apply();

            editor.putString("bluetoothImmoNotPaired","off");
            editor.apply();
            openActivityMainV2();
        }

        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    public void openActivityMainV2() {

        exitThread4 = true; //stop thread4
        System.out.println("Thread4 is done...");

        exitThread5 = true; //stop thread5
        System.out.println("Thread5 is done...");

        isResume = false;
        Intent intent = new Intent(this, ActivityMainV2.class);
        startActivity(intent);
    }

    public void openScreen4(){

        Intent intent = new Intent(this, ActivityFourthV2.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){ }

}
