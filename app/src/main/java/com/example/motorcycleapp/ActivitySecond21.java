package com.example.motorcycleapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;

public class ActivitySecond21 extends AppCompatActivity {

    private Button searchBtn;
    private Button nextBtn;
    private Switch bluetoothSwitch;
    private BluetoothAdapter myBluetoothAdapter;
    private ListView myListView;
    private TextView pDeviceName;
    private Set<BluetoothDevice> pairedDevices;
    private ArrayAdapter<String> BTArrayAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    public ArrayList<BluetoothDevice> mBTDevices = new ArrayList<>();
    public int itemPosition;
    public boolean isUnpairing = false;
    public boolean isSwitchedOn = false;

    //to disable the functionality of back button in android phones
    @Override
    public void onBackPressed(){ }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // On Screen load
        setContentView(R.layout.activity_second_screenv21);

        myListView = findViewById(R.id.myListView);
        pDeviceName = (TextView) findViewById(R.id.pDeviceName);
        nextBtn = findViewById(R.id.nextBtn);

        nextBtn.setEnabled(false);

        // create the arrayAdapter that contains the BTDevices, and set it to the ListView
        BTArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        myListView.setAdapter(BTArrayAdapter);

        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        bluetoothSwitch = findViewById(R.id.bluetoothSwitch);

        if (myBluetoothAdapter.isEnabled()) {
            bluetoothSwitch.setChecked(true);
        } else {
            bluetoothSwitch.setChecked(false);
        }


        if(myBluetoothAdapter == null) {
            nextBtn.setEnabled(false);
            searchBtn.setEnabled(false);
            searchBtn.setEnabled(false);

            //pop up message
            Toast.makeText(getApplicationContext(),"Your device does not support Bluetooth",
                    Toast.LENGTH_LONG).show();
        } else {
            searchBtn = findViewById(R.id.searchBtn);
            searchBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getApplicationContext(),"Bluetooth is currently turned off.",
                            Toast.LENGTH_LONG).show();
                }
            });

            Log.d("SAMPLE", String.valueOf(bluetoothSwitch.isChecked()));

            // If switch is already turned on or bluetooth is already enabled
            if (bluetoothSwitch.isChecked()) {
                // Search devices button
                searchBtn = findViewById(R.id.searchBtn);
                searchBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("SAMPLE", "Inside bluetoothSwitch.isChecked()");
                        searchDevices(v);
                    }
                });

                // On discovered item click
                myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        // Broadcasts receiver to detect if device has paired, pairing, or not.
                        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                        registerReceiver(mBroadcastReceiver4, filter);

                        String selectedItem = (String) parent.getItemAtPosition(position);
                        pairedDevices = myBluetoothAdapter.getBondedDevices();

                        for(BluetoothDevice bt : pairedDevices)
                            Log.v("SAMPLE", "Currently paired devices: " + bt.getName());

                        Log.v("SAMPLE", "mBTDevices: " + mBTDevices);

                        if (mBTDevices.size() > 0) {
                            pairDevice(position);
                        }
                        else {
                            Toast.makeText(ActivitySecond21.this, "unable to connect", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }

            // If Bluetooth is already on.
            if (myBluetoothAdapter.isEnabled()) {
                Toast.makeText(getApplicationContext(),"Bluetooth is already on",
                        Toast.LENGTH_LONG).show();
            }

            // On switch click
            bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Log.d("SAMPLE", "ON CLICK SWITCH!");
                    Log.d("SAMPLE", "CHECK isChecked value : " + isChecked);
                    // If switch is turned on
                    if (isChecked) {
                        isSwitchedOn = true;
                        Log.d("SAMPLE", "Setting Switch to on!");
                        on(buttonView);

                        // Search devices button
                        searchBtn = findViewById(R.id.searchBtn);
                        searchBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (isSwitchedOn) {
                                    Log.v("SAMPLE", "Inside onCheckedChanged");
                                    searchDevices(v);
                                } else {
                                    Toast.makeText(getApplicationContext(),"Bluetooth is currently turned off",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });

                        // On discovered item click
                        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                // Broadcasts receiver to detect if device has paired, pairing, or not.
                                IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
                                registerReceiver(mBroadcastReceiver4, filter);

                                String selectedItem = (String) parent.getItemAtPosition(position);
                                pairedDevices = myBluetoothAdapter.getBondedDevices();

                                for(BluetoothDevice bt : pairedDevices)
                                    Log.v("SAMPLE", "Currently paired devices: " + bt.getName());

                                Log.v("SAMPLE", "mBTDevices: " + mBTDevices);

                                if (mBTDevices.size() > 0) {
                                    pairDevice(position);
                                }
                                else {
                                    Toast.makeText(ActivitySecond21.this, "unable to connect", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });

                    } else {
                        isSwitchedOn = false;
                        Log.d("SAMPLE", "Setting Switch to off!");
                        off(buttonView);
                    }
                }
            });

        }

    }

    public void openActivityThirdV2() {
        Intent intent = new Intent(this, ActivityThirdV2.class);
        startActivity(intent);

        // NOTE: To success fully connect to screen, declare class name in AndroidManifest.xml
    }


    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name and the MAC address of the object to the arrayAdapter
                if (device.getName() != null) {
                    mBTDevices.add(device);
                    BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                    BTArrayAdapter.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(getApplicationContext(),"No nearby devices found.",
                        Toast.LENGTH_LONG).show();
            }
        }
    };

    public void searchDevices(View v) {
        Toast.makeText(getApplicationContext(),"Searching devices...",
                Toast.LENGTH_LONG).show();

        // Initialize mBTDevices to always be an empty arraylist
        mBTDevices = new ArrayList<>();

        Log.d("SAMPLE", "Inside search devices!");

        myBluetoothAdapter.cancelDiscovery();
        BTArrayAdapter.clear();
        myBluetoothAdapter.startDiscovery();
        registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }

    public void on(View view){
        if (!myBluetoothAdapter.isEnabled()) {
            myBluetoothAdapter.enable();
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);

            Toast.makeText(getApplicationContext(),"Bluetooth turned on" ,
                    Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Bluetooth is already on",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void off(View view){
        myBluetoothAdapter.disable();
        bluetoothSwitch.setChecked(false);

        mBTDevices = new ArrayList<>();
        myBluetoothAdapter.cancelDiscovery();
        BTArrayAdapter.clear();
        nextBtn.setEnabled(false);

        IntentFilter BTIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver1, BTIntent);

        Toast.makeText(getApplicationContext(),"Bluetooth turned off",
                Toast.LENGTH_LONG).show();
    }
    private void pairDevice(int position) {
        itemPosition = position;

        Log.v("SAMPLE", "PAIRED item @ position : " + position);
        final String deviceName = mBTDevices.get(position).getName();
        String deviceAddress = mBTDevices.get(position).getAddress();

        Log.v("SAMPLE", "PAIRED DeviceName : " + deviceName);

        //create the bond if and only if device found is IMB0001.

        if (deviceName.equals("IMB0001")) {
            nextBtn.setEnabled(true);
            //NOTE: Requires API 17+? I think this is JellyBean
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2){
                Log.v("SAMPLE", "Trying to pair with " + deviceName);
                Log.v("SAMPLE", "Bond state : " + mBTDevices.get(position).getBondState());
                if (mBTDevices.get(position).getBondState() == 12) {
                    pDeviceName.setVisibility(View.VISIBLE);
                    pDeviceName.setText(deviceName);

                    pDeviceName.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            confirmDialogDemo(deviceName);
                        }
                    });

                    nextBtn = findViewById(R.id.nextBtn);
                    nextBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openActivityThirdV2();
                        }
                    });

                    Toast.makeText(getApplicationContext(),"Device is already PAIRED with : " + deviceName,
                            Toast.LENGTH_LONG).show();
                } else {
                    mBTDevices.get(position).createBond();
                }
            }
        } else {
            // If user selects a device to pair with that is not IMB0001.

            Toast.makeText(getApplicationContext(),"Device cannot be paired!",
                    Toast.LENGTH_LONG).show();
        }

    }

    private void unpairDevice(BluetoothDevice device) {
        isUnpairing = true;
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (action.equals(myBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, myBluetoothAdapter.ERROR);

                switch(state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d("TAG", "onReceive: STATE OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d("TAG", "mBroadcastReceiver1: STATE TURNING OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d("TAG", "mBroadcastReceiver1: STATE ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d("TAG", "mBroadcastReceiver1: STATE TURNING ON");
                        break;
                }
            }
        }
    };

    private BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d("TAG", "onReceive: ACTION FOUND.");

            if (action.equals(BluetoothDevice.ACTION_FOUND)){
                BluetoothDevice device = intent.getParcelableExtra (BluetoothDevice.EXTRA_DEVICE);
                mBTDevices.add(device);
                Log.d("TAG", "onReceive: " + device.getName() + ": " + device.getAddress());
            }
        }
    };

    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                final String deviceName = mDevice.getName();

                if (deviceName.equals("IMB0001")) {

                    // Case 1: bonded already
                    if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED){
                        Log.v("TAG", "BroadcastReceiver: BOND_BONDED.");

                        Toast.makeText(getApplicationContext(),"Your device has successfully paired!",
                                Toast.LENGTH_LONG).show();

                        pDeviceName.setVisibility(View.VISIBLE);
                        pDeviceName.setText(deviceName);

                        nextBtn = findViewById(R.id.nextBtn);
                        nextBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openActivityThirdV2();
                            }
                        });

                        pDeviceName.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                confirmDialogDemo(deviceName);
                            }
                        });
                    }

                    // Case 2: creating a bond
                    if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                        Log.v("TAG", "BroadcastReceiver: BOND_BONDING.");

                        Toast.makeText(getApplicationContext(),"Your device is pairing...",
                                Toast.LENGTH_LONG).show();
                    }

                    // Case 3: breaking a bond
                    if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                        Log.v("TAG", "BroadcastReceiver: BOND_NONE.");

                        pDeviceName.setVisibility(View.INVISIBLE);

                        if (isUnpairing) {
                            Toast.makeText(getApplicationContext(),"Your device has successfully unpaired from: " + deviceName,
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(),"Your device has not paired.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }

            }
        }
    };

    private void confirmDialogDemo(final String deviceName) {
        final BluetoothDevice device = mBTDevices.get(itemPosition);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to unpair from " + deviceName + "?");
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                unpairDevice(device);
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.show();
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(bReceiver);
        unregisterReceiver(mBroadcastReceiver1);
        unregisterReceiver(mBroadcastReceiver3);
        unregisterReceiver(mBroadcastReceiver4);
    }

}

