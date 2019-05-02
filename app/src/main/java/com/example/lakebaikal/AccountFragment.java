package com.example.lakebaikal;


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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.lakebaikal.LoginActivity.bt_addr;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment {

    public static String TAG = "AccountFragment";

    View fragment;

    ImageView mProfileImage;
    TextView mAccountInfoText;
    Button mAddFundsBtn, mRegisterBtBtn;

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
        mAddFundsBtn = fragment.findViewById(R.id.addfunds_btn);
        mRegisterBtBtn = fragment.findViewById(R.id.registerbt_btn);

        user = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();
        users = database.getReference("Users");

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

        getaccountinfo();

        // Inflate the layout for this fragment
        return fragment;
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

    //temp function to get account information
    public void getaccountinfo() {

        new Thread(new Runnable() {
            @Override
            public void run() {

            }
        }).start();

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