package com.example.lakebaikal;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {

    public BluetoothManager bm;
    public BluetoothAdapter btAdapter = null;

    private TextView mTextMessage;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bm = (BluetoothManager) getSystemService( Context.BLUETOOTH_SERVICE );
        btAdapter = bm.getAdapter();

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        checkBT();
    }

    //check bluetooth permission and support
    private void checkBT() {
        // Check for Bluetooth support and then check to make sure it is turned on
        if (btAdapter == null) {
            Toast.makeText(this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
        }
        else {
            if (!btAdapter.isEnabled()) {
                //ask user to turn on bluetooth
                Intent enableBtIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE );
                startActivity(enableBtIntent);
            }
            if(btAdapter.isEnabled())
            {
                //run discoverable method
                new Thread( new Runnable() {
                    @Override
                    public void run() {
                        Method method;
                        try {
                            method = btAdapter.getClass().getMethod("setScanMode", int.class, int.class);
                            method.invoke(btAdapter,BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE,0);
                            Log.d( "LOG", "run: DISCOVERABLE" );
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                } ).start();
            }
        }
    }

}
