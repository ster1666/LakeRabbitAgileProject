package com.example.lakebaikal;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Method;
import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static com.example.lakebaikal.LoginActivity.bt_addr;
import static com.example.lakebaikal.LoginActivity.btcheck;

public class homeActivity extends AppCompatActivity {

    private static final String TAG = "homeActivity";
    public BluetoothManager bm;
    public BluetoothAdapter btAdapter = null;

    private FirebaseDatabase database;
    private DatabaseReference users;

    private static FirebaseUser user;
    //Used t0 contain user id from the user that signed in

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
                    //mTextMessage.setText(R.string.title_notifications);
                    try{
                        getaccountinfo();
                    }catch(Exception e)
                    {

                    }

                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LoginActivity.btaddrstate=false;
        bm = (BluetoothManager) getSystemService( Context.BLUETOOTH_SERVICE );
        btAdapter = bm.getAdapter();
        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        get_userBtaddr();
        discoverBT();
        checkTimestamp();




    }

    @Override
    protected void onRestart() {
        super.onRestart();
        btcheck(this,btAdapter);
        discoverBT();;
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

    public static InputFilter fundfilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            String blockCharacterSet = "~#^|$%*!@/()-'\":;,?{}=!$^';,?×+.÷<>{}€£¥₩%~`¤♡♥_|《》¡¿°•○●□■◇◆♧♣▲▼▶◀↑↓←→☆★▪:-);-):-D:-(:'(:O abcdefghijklmnopqrstpxyzåäöABCDEFGHIJKLMNOPQRSTPXYZÅÄÖ";
            if (source != null && blockCharacterSet.contains(("" + source))) {
                return "";
            }
            return null;
        }
    };

    public void register_btaddr_click(View view)
    {
        LoginActivity.btAddrPopup(this,user,users);
    }
    public void add_funds_click(View view)
    {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Account balance");
        alertDialog.setMessage("add balance to your account");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_PHONE);
        input.setFilters(new InputFilter[] { fundfilter });
        alertDialog.setView(input);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, int which) {


                        final Integer tfund = Integer.parseInt(input.getText().toString());

                        Log.d(TAG, "onClick: "+tfund);
                        users.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                {
                                    int tempbalance = Integer.valueOf(String.valueOf(dataSnapshot.child(bt_addr).child("balance").getValue()));
                                    tempbalance = tempbalance + tfund;// COST 100 WHEN PASSES
                                    users.child(bt_addr).child("balance").setValue(tempbalance);
                                    dialog.dismiss();
                                }}
                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
    //check bluetooth permission and support
    public void discoverBT() {
        // Check for Bluetooth support and then check to make sure it is turned on
        if(btAdapter.isEnabled()) {
            //run discoverable method
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(!Thread.currentThread().isInterrupted())
                    {
                        //TODO INSERT GPS CHECK IF YOU WANT TO DECREASE DISCOVERY TIME

                        Method method;
                        try {
                            method = btAdapter.getClass().getMethod("setScanMode", int.class, int.class);
                            method.invoke(btAdapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE, 0);
                            Log.d("LOG", "run:                                      DISCOVERABLE");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            Thread.sleep(120000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }).start();
        }
        else
        {
            btcheck(this,btAdapter);
        }
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

                                if(dataSnapshot.child(bt_addr).child("timestamp").getValue() != dataSnapshot.child(bt_addr).child("lastPayed").getValue())
                                {
                                    //TODO check if X time is between timestamp to pay
                                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy,HH:mm");
                                    String date = df.format( Calendar.getInstance().getTime());

                                    users.child(bt_addr).child("lastPayed").setValue(date);
                                    int tempbalance = Integer.valueOf(String.valueOf(dataSnapshot.child(bt_addr).child("balance").getValue()));
                                    tempbalance = tempbalance -100;// COST 100 WHEN PASSES
                                    users.child(bt_addr).child("balance").setValue(tempbalance);

                                    int temppasses = Integer.valueOf(String.valueOf(dataSnapshot.child(bt_addr).child("passes").getValue()));
                                    temppasses = temppasses +1;
                                    users.child(bt_addr).child("passes").setValue(temppasses);
                                    getaccountinfo();

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
    public void getaccountinfo() {

        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                {
                    String tempemail = String.valueOf(dataSnapshot.child(bt_addr).child("email").getValue());
                    String tempbalance = String.valueOf(dataSnapshot.child(bt_addr).child("balance").getValue());
                    String tempname = String.valueOf(dataSnapshot.child(bt_addr).child("fullname").getValue());
                    String tempaddress = String.valueOf(dataSnapshot.child(bt_addr).child("btaddr").getValue());
                    String temppasses = String.valueOf(dataSnapshot.child(bt_addr).child("passes").getValue());

                    String fill = "\n\r";
                    String title = " Account Info" + fill;
                    String fname = " Name: " + tempname + fill;
                    String email = " Email: " + tempemail + fill;
                    String balance = " Balance: " + tempbalance + fill;
                    String address = " Address: " + tempaddress + fill;
                    String passes = " Number of passes: " + temppasses + fill;

                    String concatt = title + fname + email + balance + address + passes;

                    mTextMessage.setText(concatt);
                    Log.d(TAG, "onDataChange: " + concatt);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }
}
