package com.example.lakebaikal;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.lakebaikal.LoginActivity.bt_addr;

public class homeActivity extends AppCompatActivity {

    private static final String TAG = "homeActivity";
    public BluetoothManager bm;
    public static BluetoothAdapter btAdapter = null;

    private FirebaseDatabase database;
    private DatabaseReference users;

    private static FirebaseUser user;

    private BottomNavigationView bottomNavigationView;


    @Override
    //TODO MAKE A LOGOUT BUTTON
    //TODO MAKE HISTORY VIEW THAT SAVES EVENTS BETWEEN LOGIN?
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LoginActivity.btaddrstate=false;
        bm = (BluetoothManager) getSystemService( Context.BLUETOOTH_SERVICE );
        btAdapter = bm.getAdapter();
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");

        get_userBtaddr();
        discoverBT();
        autoenableBT();
        checkTimestamp();

        //Navigation
        bottomNavigationView = findViewById(R.id.navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment selectedFragment = null;

                switch (item.getItemId()){
                    case R.id.navigation_home:
                        selectedFragment = AccountFragment.newInstance();
                        break;
                    case R.id.navigation_notifications:
                        selectedFragment = PaymentHistoryFragment.newInstance();
                        break;
                }
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, selectedFragment);
                transaction.commit();
                return true;
            }
        });
        setDefaultFragment();
    }

    private void setDefaultFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, AccountFragment.newInstance());
        transaction.commit();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        autoenableBT();
        discoverBT();;
    }

    @Override
    protected void onResume() {
        super.onResume();
        autoenableBT();
        discoverBT();
    }

    public void get_userBtaddr()
    {
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                {
                    for(DataSnapshot post : dataSnapshot.getChildren())
                    {
                        try{
                            if(user.getEmail().equalsIgnoreCase(post.child("email").getValue().toString()))
                            {
                                LoginActivity.bt_addr = post.child("btaddr").getValue().toString();
                                Log.d(TAG, "onDataChange: FOUND BTADDR "+LoginActivity.bt_addr);
                                //TODO HANDLE IF YOUR ACCOUNT CANT BE FOUND IN FB
                            }
                        }catch(Exception e)
                        {

                        }
                    }
                }}
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




    public void autoenableBT()
    {
        if(LoginActivity.enableBT)
        {
            Log.d("LOG", "btcheck: ");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(!Thread.currentThread().isInterrupted())
                    {
                        Log.d("LOG", "btcheck");
                        if(!btAdapter.isEnabled())
                        {
                            btAdapter.cancelDiscovery();
                            Log.d("LOG", "run: BLUETOOTH AUTOMATICLY ACTIVATED!!!");
                            btAdapter.enable();
                            while(true)
                            {
                                try {
                                    Thread.sleep(3000);
                                    discoverBT();
                                    break;
                                } catch (InterruptedException e) {
                                    e.printStackTrace();

                                }
                            }

                        }
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }
    //check bluetooth permission and support
    public void discoverBT() {
        // Check for Bluetooth support and then check to make sure it is turned on

        //run discoverable method
        new Thread(new Runnable() {
            @Override
            public void run() {
                //TODO INSERT GPS CHECK IF YOU WANT TO DECREASE DISCOVERY TIME
                Method method;
                try {
                    method = btAdapter.getClass().getMethod("setScanMode", int.class, int.class);
                    method.invoke(btAdapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, 0);
                    Log.d("LOG", "run:                                      DISCOVERABLE");

                } catch (Exception e) {
                    Log.d(TAG, "run: DISCOVER BROKE");
                }

            }
        }).start();

    }
    //check timestamp and payment
    public void checkTimestamp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted())
                {
                    Log.d(TAG, "run:                                        CHECKTIMESTAMP!");
                    users.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            {
                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                //DateFormat df = new SimpleDateFormat("HH:mm");
                                long elapsed= 0;
                                Date dstamp=null,dpayed=null;
                                String timestamp = (String)dataSnapshot.child(bt_addr).child("timestamp").getValue();
                                Log.d( TAG, "onDataChange: TIMESTAMP "+timestamp );

                                String lasttime =(String)dataSnapshot.child(bt_addr).child("lastPayed").getValue();
                                Log.d( TAG, "onDataChange: LASTPAYED "+lasttime );

                                if(timestamp.length() >= 2)
                                {
                                    try {
                                        dstamp = df.parse(timestamp);
                                        dpayed = df.parse(lasttime);
                                    } catch (ParseException e) {

                                    }

                                    Log.d(TAG, "onDataChange: DATE TIMESTAMP: "+dstamp + " DATE LASTPAYED: "+dpayed);

                                    try
                                    {
                                        elapsed = dpayed.getTime() - dstamp.getTime();
                                    }catch(Exception e)
                                    {

                                    }
                                    elapsed = Math.abs(elapsed);
                                    Log.d( TAG, "onDataChange: ELAPSED: "+elapsed );
                                    if(elapsed >= 10000|| dpayed == null)
                                    {
//                                    DateFormat df = new SimpleDateFormat("yyyy-dd-MM HH:mm");
//                                    String date = df.format( Calendar.getInstance().getTime());

                                        users.child(bt_addr).child("lastPayed").setValue(timestamp);
                                        int tempbalance = Integer.valueOf(String.valueOf(dataSnapshot.child(bt_addr).child("balance").getValue()));
                                        tempbalance = tempbalance -100;// COST 100 WHEN PASSES
                                        users.child(bt_addr).child("balance").setValue(tempbalance);

                                        int temppasses = Integer.valueOf(String.valueOf(dataSnapshot.child(bt_addr).child("passes").getValue()));
                                        temppasses = temppasses +1;
                                        users.child(bt_addr).child("passes").setValue(temppasses);

                                    }
                                }


                            }}
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                    try {
                        Thread.sleep(10000);//HOW OFTEN TO CHECK TIMESTAMP
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }


}
