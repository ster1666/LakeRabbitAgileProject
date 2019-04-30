package com.example.lakebaikal;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private SignInButton googleSignInButton;

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "LoginActivity";

    public static boolean btaddrstate=true;
    public static boolean enableBT=false;

    private FirebaseDatabase database;
    private DatabaseReference users;

    public BluetoothManager bm;
    public BluetoothAdapter btAdapter = null;

    public static String  bt_addr = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bm = (BluetoothManager) getSystemService( Context.BLUETOOTH_SERVICE );
        btAdapter = bm.getAdapter();
        btcheck(this,btAdapter);
        googleSignInButton = findViewById(R.id.google_sign_in_button);
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");
        googleSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add google sign in functionality

                createSignInIntent();
            }
        });

    }

    //TODO MAKE A CHANGE USER ALTERNATIVE
    public void createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        this.startActivityForResult(AuthUI
                        .getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build()
                ,RC_SIGN_IN);
        // [END auth_fui_create_intent]
    }

    // [START auth_fui_result]
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        if (requestCode == RC_SIGN_IN) {
            final IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK) {
                // Successfully signed in
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(response.isNewUser())
                        {
                            Log.d(TAG, "onActivityResult: NEW");
                            btAddrPopup(LoginActivity.this,user,users);

                        }
                        else
                        {
                            Log.d(TAG, "onActivityResult: OLD " + user.getUid());
                            Intent homeActivity = new Intent(LoginActivity.this, com.example.lakebaikal.homeActivity.class);

                            bt_addr = users.getKey();
                            Log.d(TAG, "onActivityResult: "+ bt_addr);

                            startActivity(homeActivity);
                            finish();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            } else {
                Log.d(TAG, "SIGN IN FAILED");

                finish();
            }
        }
    }
    public static InputFilter addressfilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (source != null && !Pattern.matches("[a-fA-F0-9:]+",""+source)) {
                Log.d("LOG", "filter: ");
                return "";
            }
            return null;

        }
    };
    public static void btAddrPopup(final Context context, final FirebaseUser user, final DatabaseReference users) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle("Bluetooth");
        alertDialog.setMessage("Please register your bluetooth adress");
        final String oldbtaddr = bt_addr;
        final EditText input = new EditText(context);
        input.setInputType(InputType.TYPE_TEXT_VARIATION_URI);
        input.setFilters(new InputFilter[] { addressfilter });
        alertDialog.setView(input);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(!Pattern.matches("..:..:..:..:..:..",input.getText()))
                        {
                            Toast.makeText(context, "Your input doesnt match a bluetooth address\n please try again...\n address example: XX:XX:XX:XX:XX:XX\n your see your bluetooth address in mobile settings", Toast.LENGTH_LONG).show();
                            btAddrPopup(context,user,users);
                        }
                        else
                        {
                            bt_addr =String.valueOf(input.getText().toString().toUpperCase());
                            if(btaddrstate){
                                Log.d(TAG, "btaddr: new");
                                users.child(bt_addr.toUpperCase()).setValue(new User(user.getUid(), user.getDisplayName(), user.getEmail(), bt_addr,0));
                                Toast.makeText(context, "Account registered.", Toast.LENGTH_LONG).show();
                            }
                            else
                            {
                                Log.d(TAG, "btaddr: change " + oldbtaddr);
                                users.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        {
                                            users.child(bt_addr.toUpperCase()).setValue(new User(user.getUid(), user.getDisplayName(),
                                                    user.getEmail(), bt_addr,Integer.valueOf(String.valueOf(dataSnapshot.child(oldbtaddr).child("balance").getValue()))));

                                            String templastpayed = String.valueOf(dataSnapshot.child(oldbtaddr).child("lastPayed").getValue());
                                            users.child(bt_addr).child("lastPayed").setValue(templastpayed);

                                            int temppasses = Integer.valueOf(String.valueOf(dataSnapshot.child(oldbtaddr).child("passes").getValue()));
                                            users.child(bt_addr).child("passes").setValue(temppasses);


                                            users.child(oldbtaddr).removeValue();
                                            Toast.makeText(context, "Account address updated.", Toast.LENGTH_LONG).show();

                                        }}
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            }
                        }

                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }
    public static void btcheck(Context context, final BluetoothAdapter btAdapter){
        // Check for Bluetooth support and then check to make sure it is turned on
        if (btAdapter == null) {
            Toast.makeText(context, "Bluetooth is not available", Toast.LENGTH_LONG).show();
        }
        else {

            if (!btAdapter.isEnabled()) {
                //ask user to turn on bluetooth
                if(!enableBT)
                {
                    enableBT=true;
                    Log.d(TAG, "btcheck: AGREED ON USING BLUETOOTH");
                    Intent enableBtIntent = new Intent( BluetoothAdapter.ACTION_REQUEST_ENABLE );
                    context.startActivity(enableBtIntent);

                }
                else
                {
                    //TODO IF USER REFUSE TO AGREE ON BLUETOOTH
                }

            }
            else
            {
                enableBT=true;
            }
        }
    }


}

