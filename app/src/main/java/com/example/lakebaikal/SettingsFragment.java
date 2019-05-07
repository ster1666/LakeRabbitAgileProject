package com.example.lakebaikal;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.lakebaikal.LoginActivity.bt_addr;


/**
 */
public class SettingsFragment extends Fragment {

    public static String TAG = "SettingsFragment";
    View fragment;
    Button mSignOutBtn, mAddFundsBtn, mRegisterBtBtn, mMapBtn;
    private FirebaseDatabase database;
    private DatabaseReference users;
    private static FirebaseUser user;

    private static final int ERROR_DIALOG_REQUEST = 9001;


    public static SettingsFragment newInstance()  {
        // Required empty public constructor
        SettingsFragment settingsFragment = new SettingsFragment();
        return settingsFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(isServicesOK()) {

            /**/
            MapButton();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragment = inflater.inflate(R.layout.fragment_settings, container, false);

        mSignOutBtn = fragment.findViewById(R.id.sign_out_btn);
        mAddFundsBtn = fragment.findViewById(R.id.addfunds_btn);
        mRegisterBtBtn = fragment.findViewById(R.id.registerbt_btn);
        mMapBtn = fragment.findViewById(R.id.btnMap);

        mMapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapButton();
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");

        

        mSignOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Sign out user
                signOut();
            }
        });

        mAddFundsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addfundPopup();
            }
        });

        mRegisterBtBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.btAddrPopup(getContext(),user,users);
            }
        });

        return fragment;
    }


    private void signOut(){
        AuthUI.getInstance().signOut(getContext()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "##########################Sign out succeeded!");

                    Toast.makeText(getContext(),"User was signed out!", Toast.LENGTH_LONG);

                    Intent targetView = new Intent(getActivity(), LoginActivity.class);
                    startActivity(targetView);
                }else{
                    Log.d(TAG, "########################Sign out failed!");
                }
            }
        });
    }

    public void addfundPopup()
    {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Account balance");
        alertDialog.setMessage("add balance to your account");

        final EditText input = new EditText(getContext());
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

    public static InputFilter fundfilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            String blockCharacterSet = "+-:;,.#*Nn/() ";
            if (source != null && blockCharacterSet.contains(("" + source))) {
                Log.d("LOG", "filter: ");
                return "";

            }
            return null;
        }
    };



    private void MapButton(){
            Intent intent = new Intent(getContext(), MapActivity.class);
            startActivity(intent);
    }

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext());

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occurred but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(getContext(), "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }



}
