package com.example.dingdang.youtubenotetaker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class FirebaseLoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    private static final String TAG = "FirebaseLoginActivity";
    private boolean login_once = false;

    // Firebase related variables
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_login);

        mFirebaseAuth = FirebaseAuth.getInstance();

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build(),
                new AuthUI.IdpConfig.Builder(AuthUI.TWITTER_PROVIDER).build());

        // Create and launch sign-in intent
        /** startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);


        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
        */

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // firebaseAuth variable is guaranteed to contain user sign-in or not information
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    // User logged in
                    //onSignedInInitialize(user.getDisplayName());
                    Log.i(TAG, "SHENOY user has signed in");
                    //FirebaseUser logged_in_user = FirebaseAuth.getInstance().getCurrentUser();
                    Intent intent = new Intent(FirebaseLoginActivity.this, AfterLoginActivity.class);
                    intent.putExtra("USER_TYPE", "REGISTERED");
                    startActivity(intent);

                }
                else {
                    // User signed out, so put in sign in flow
                    //onSignedOutCleanup();
                    //Log.i(TAG, "SHENOY Putting in user to login again login_once = " + login_once);
                    if(login_once == false) {
                            startActivityForResult(
                                    AuthUI.getInstance()
                                            .createSignInIntentBuilder()
                                            .setIsSmartLockEnabled(false)
                                            .setAvailableProviders(Arrays.asList(
                                                    new AuthUI.IdpConfig.EmailBuilder().build()))
                                            .build(),
                                    RC_SIGN_IN);
                            login_once = true;
                    }
                    else if(login_once == true) {
                        //Log.i(TAG, "SHENOY calling onBackPressed function!! ");
                        onBackPressed();
                    }
                }
            }
        };




    }

    /**
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, "SHENOY onActivityResult - resultCode = " + resultCode);
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Intent intent = new Intent(FirebaseLoginActivity.this, AfterLoginActivity.class);
                intent.putExtra("USER_TYPE", "REGISTERED");
                startActivity(intent);
                // ...
            } else if(resultCode == RESULT_CANCELED) {
                Toast.makeText(FirebaseLoginActivity.this, "LETS EXIT !!", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "LETS EXIT");

                // new code
                AlertDialog.Builder alert = new AlertDialog.Builder(FirebaseLoginActivity.this);
                alert.setTitle("Alert!!");
                alert.setMessage("Logout ? Yes for Logout, No for exit ");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                        Log.i(TAG, "SHENOY YES pressed ");
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);

                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        Log.i(TAG, "SHENOY NO pressed ");
                        moveTaskToBack(true);
                    }
                });

                alert.show();
            }
            {
                // Sign in failed, check response for error code
                // ...
            }
        }
    }*/

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(FirebaseLoginActivity.this, MainActivity.class);
        //intent.putExtra("USER_TYPE", "REGISTERED");
        startActivity(intent);

    }

    @Override
    protected void onPause () {
        super.onPause();
        // remove AuthStateListener when activity goes out of picture
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        //detachDatabaseReadListener();
        //mMessageAdapter.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // add AuthStateListener when activity comes into the picture
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}
