package com.example.lakebaikal;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.util.Objects;

public class bluetoothActivity {

    public boolean btcheck(Context context, BluetoothAdapter btAdapter,BluetoothManager bm){

        if(btexist(context,bm))
        {
            return true;
        }
        else
        {
            //DONT SUPPORT BLUETOOTH
            try{
                Log.d("LOG", "btcheck: DONT SUPPORT");
            }catch(Exception e)
            {

            }
            return false;

        }
    }
    public boolean btexist(Context context, BluetoothManager bm){
        // Check for Bluetooth support and then check to make sure it is turned on
        BluetoothAdapter adapter = bm.getAdapter();

        if (adapter == null) {
            try{
                Toast.makeText(context, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            }catch(Exception e)
            {

            }
            return false;

        }
        else {

            return true;

        }
    }
    public boolean BTactive(BluetoothAdapter btAdapter)
    {
        if (!btAdapter.isEnabled()) {
            //ask user to turn on bluetooth
            try{
                Log.d("LOG", "btcheck: BLUETOOTH Active");
            }catch(Exception e)
            {

            }

            //Intent enableBtIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE );
            //context.startActivity(enableBtIntent);
            btAdapter.enable();
            try {
                Thread.sleep( 2000 );
            } catch (InterruptedException e) {
            }
            return false;
        }
        return true;
    }
}
