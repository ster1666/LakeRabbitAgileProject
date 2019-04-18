package com.example.lakebaikal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.SignInButton;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private SignInButton googleSignInButton;

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "LoginActivity";

    private FirebaseDatabase database;
    private DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
        Log.d("LOG", "##################createSignInIntent: ");
        // [END auth_fui_create_intent]
    }

    // [START auth_fui_result]
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        if (requestCode == RC_SIGN_IN) {
            Log.d("LOG", "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!onActivityResult: ");
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
               final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d(TAG, user.getDisplayName());

                users.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(!dataSnapshot.hasChild(user.getUid())){
                            users.child(user.getUid())
                                    .setValue(new User(user.getUid(), user.getDisplayName(), user.getEmail()));

                            Intent homeActivity = new Intent(LoginActivity.this,MainActivity.class);

                            //Pass User id to MainActivity
                            homeActivity.putExtra("userId", user.getUid());

                            startActivity(homeActivity);
                            finish();

                        }else{
                            Intent homeActivity = new Intent(LoginActivity.this,MainActivity.class);

                            //Pass User id to MainActivity
                            homeActivity.putExtra("userId", user.getUid());

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
}

