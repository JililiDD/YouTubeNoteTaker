package com.example.dingdang.youtubenotetaker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class AfterLoginActivity extends AppCompatActivity {

    Button btnOpenNotebook, btnMyNotebooks;
    private static final int RC_SIGN_IN = 1;
    private boolean login_once = false;

    // Firebase variable
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseAuth mFirebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_login);

        //Remove back button on the title bar
        //Code referenced from: https://stackoverflow.com/a/22313897
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        btnMyNotebooks = (Button) findViewById(R.id.btnMyNotebooks);
        btnOpenNotebook = (Button) findViewById(R.id.btnOpenNotebook);

        // Firebase auth variable initialization
        mFirebaseAuth = FirebaseAuth.getInstance();

        // Lead the user to the activity for taking notes.
        btnOpenNotebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Main_Search.class);
                intent.putExtra("USER_TYPE", "REGISTERED");
                startActivity(intent);
            }
        });

        btnMyNotebooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Fetch users notebooks from Firebase and display them in UserNotebooksActivity
                Intent intent = new Intent(getApplicationContext(), UserNoteBooks.class);
                intent.putExtra("USER_TYPE", "REGISTERED");
                startActivity(intent);
            }
        });

        // Reference : https://github.com/udacity/and-nd-firebase
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // firebaseAuth variable is guaranteed to contain user sign-in or not information
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    // User logged in, do not do anything

                }
                else {
                    // User signed out, so put in sign in flow only once if user is pressing back button
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
                        // Call function onBackPressed when back button is pressed
                        onBackPressed();
                    }

                }
            }
        };

    }

    // Prevent a logged in user going back to the home login page. Solution is referred from:
    // https://stackoverflow.com/a/26492794
    @Override
    public void onBackPressed()
    {
        login_once = false;
        moveTaskToBack(true);
    }

    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_SIGN_IN) {
            if(resultCode == RESULT_OK) {
                // User signed in successfully, do not do anything
            }
            else if (resultCode == RESULT_CANCELED) {
                // User pressed back button, clal function
                moveTaskToBack(true);
            }
        }
        else {

            Intent intent = new Intent(AfterLoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    // Reference : https://github.com/udacity/and-nd-firebase
    @Override
    protected void onPause () {
        super.onPause();
        // remove AuthStateListener when activity goes out of picture
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // add AuthStateListener when activity comes into the picture
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    //Reference : https://stackoverflow.com/questions/6439085/android-how-to-create-option-menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.sign_out_menu :
                //sign out
                AuthUI.getInstance().signOut(this);
                return true;
            default :
                return super.onOptionsItemSelected(item);
        }
    }

}
