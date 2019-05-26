package com.example.lakebaikal;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Pattern;

import static com.example.lakebaikal.LoginActivity.bt_addr;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    public static String TAG = "AccountFragment";

    View fragment;

    ImageView mProfileImage;
    TextView mAccountInfoText;

    private FirebaseDatabase database;
    private DatabaseReference users;
    private static FirebaseUser user;


    public static AccountFragment newInstance(){
        AccountFragment accountFragment = new AccountFragment();
        return accountFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Initialize views here
        fragment = inflater.inflate(R.layout.fragment_account, container, false);
        mProfileImage = fragment.findViewById(R.id.profileImage);
        mAccountInfoText = fragment.findViewById(R.id.account_info);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");


        getaccountinfo();
        updateinfo();

        // Inflate the layout for this fragment
        return fragment;
    }

    public void updateinfo()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted())
                {
                    if(homeActivity.accountinfo)
                    {
                        try{
                            getaccountinfo();
                        }catch(Exception e)
                        {

                        }
                        homeActivity.accountinfo=false;
                    }
                }

            }
        }).start();
    }
    //temp function to get account information
    public void getaccountinfo() {

        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                {
                    //TODO CHANGE TO MATCH GUI LATER
                    String tempemail = String.valueOf(dataSnapshot.child(bt_addr).child("email").getValue());
                    String tempbalance = String.valueOf(dataSnapshot.child(bt_addr).child("balance").getValue());
                    String tempname = String.valueOf(dataSnapshot.child(bt_addr).child("fullName").getValue());
                    String tempaddress = String.valueOf(dataSnapshot.child(bt_addr).child("btaddr").getValue());
                    String temppasses = String.valueOf(dataSnapshot.child(bt_addr).child("passes").getValue());
                    String templastpayed = String.valueOf(dataSnapshot.child(bt_addr).child("lastPayed").getValue());

                    String fill = "\n\r";
                    String title = " Account Information" + fill;
                    String fname = " Name: " + tempname + fill;
                    String email = " Email: " + tempemail + fill;
                    String balance = " Balance: " + tempbalance + fill;
                    String address = " Address: " + tempaddress + fill;
                    String passes = " Number of passes: " + temppasses + fill;
                    String payed = " latest timestamp: " + templastpayed + fill;

                    String concatt = title + fname + email + balance + address + passes +payed;

                    mAccountInfoText.setText(concatt);
                    //Log.d(TAG, "onDataChange: " + concatt);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

}