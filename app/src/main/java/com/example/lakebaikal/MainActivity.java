    package com.example.lakebaikal;

    import android.bluetooth.BluetoothAdapter;
    import android.bluetooth.BluetoothDevice;
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

    import com.google.firebase.auth.FirebaseUser;
    import com.google.firebase.database.DataSnapshot;
    import com.google.firebase.database.DatabaseError;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.database.ValueEventListener;

    import java.lang.reflect.Method;

    public class MainActivity extends AppCompatActivity {

        private static final String TAG = "MainActivity";
        public BluetoothManager bm;
        public BluetoothAdapter btAdapter = null;

        private FirebaseDatabase database;
        private DatabaseReference users;

        //Used t0 contain user id from the user that signed in
        private String userId;

        private TextView mTextMessage;
        private Intent currentIntent;



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

            currentIntent = getIntent();
            userId = currentIntent.getStringExtra("userId");

            Log.d(TAG, userId);

            database = FirebaseDatabase.getInstance();
            users = database.getReference("Users");

            mTextMessage = (TextView) findViewById(R.id.message);
            BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
            checkBT();
        }

        @Override
        protected void onRestart() {
            super.onRestart();
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

                                final String blueToothAddress = android.provider.Settings.Secure.getString(getContentResolver(), "bluetooth_address");
                                Log.d(TAG, blueToothAddress);


                                users.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.hasChild(userId)){

                                            users.child(userId)
                                                    .child("bluetoothAddress")
                                                    .setValue(blueToothAddress);
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });


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
