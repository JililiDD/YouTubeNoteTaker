package com.example.dingdang.youtubenotetaker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by dinesh on 17/03/18.
 */

public class UserNoteBooks extends AppCompatActivity {

    // String to store the username of current user
    private String mUsername, userId;
    private static final String TAG = "MyActivity";
    private ArrayAdapter<String> itemsAdapter;
    private String videoId;
    //private ArrayList<String> myVideoList = new ArrayList<String>();
    String[] myVideoList = new String[10];
    public static final int RC_SIGN_IN = 1;

    // Firebase related varaibles
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private ChildEventListener mChildEventListener;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_note_books_list);

        // List view variable
        ListView notesList;

        // Set layout for each item of the list view
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        // Initialize ListView variable & bind the adapter to list view
        notesList = findViewById(R.id.lvNotesList);
        notesList.setAdapter(itemsAdapter);

        // Instantiate Firebase varaibles
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("testuser");
        FirebaseUser user = mFirebaseAuth.getCurrentUser();

        if(user != null) {
            // User logged in
            onSignedInInitialize(user.getDisplayName(), user.getUid());


        }

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // firebaseAuth variable is guaranteed to contain user sign-in or not information
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null) {
                    // User logged in
                    //onSignedInInitialize(user.getDisplayName());

                }
                else {
                    // User signed out, so put in sign in flow
                    onSignedOutCleanup();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };

        notesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    Intent intent = new Intent (view.getContext() , GuestActivity.class);
                    intent.putExtra("VIDEO_ID", myVideoList[0]);
                    //intent.putExtra("USER_TYPE", userType);
                    intent.putExtra("USER_TYPE", "GUEST");
                    startActivityForResult(intent,0);
                }
                if (position == 1) {
                    Intent intent = new Intent (view.getContext() , GuestActivity.class);
                    intent.putExtra("VIDEO_ID", myVideoList[1]);
                    //intent.putExtra("USER_TYPE", userType);
                    intent.putExtra("USER_TYPE", "GUEST");
                    startActivityForResult(intent,0);

                }
                if (position == 2) {
                    Intent intent = new Intent (view.getContext() , GuestActivity.class);
                    intent.putExtra("VIDEO_ID", myVideoList[2]);
                    //intent.putExtra("USER_TYPE", userType);
                    intent.putExtra("USER_TYPE", "GUEST");
                    startActivityForResult(intent,0);

                }
                if( position == 3) {
                    Intent intent = new Intent (view.getContext() , GuestActivity.class);
                    intent.putExtra("VIDEO_ID", myVideoList[3]);
                    //intent.putExtra("USER_TYPE", userType);
                    intent.putExtra("USER_TYPE", "GUEST");
                    startActivityForResult(intent,0);
                }
            }
        });
    }

    @Override
    protected void onPause () {
        super.onPause();
        // remove AuthStateListener when activity goes out of picture
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        //detachDatabaseReadListener();
        itemsAdapter.clear();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // add AuthStateListener when activity comes into the picture
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    private void onSignedOutCleanup() {
        // clear all entries of notes displayed
        itemsAdapter.clear();
    }

    private void onSignedInInitialize(String username1, String username2) {
        mUsername = username1;
        userId = username2;

        //attachDatabaseReadListener();

        Log.i(TAG, "DIN onSignedInInitialize, username = " + mUsername);
        Log.i(TAG, "DIN Username2 (id) = " + userId);
        attachDatabaseReadListener();
        detachDatabaseReadListener();

    }

    private void attachDatabaseReadListener() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("testuser");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        Long count_of_children = dataSnapshot.getChildrenCount();
                        Log.i(TAG, "DIN count of children = " + count_of_children);
                        //List<String> td = (ArrayList<String>) dataSnapshot.getValue();
                        for(DataSnapshot ds: dataSnapshot.getChildren()) {
                            String key = ds.getKey();
                            Log.i(TAG, "DIN key = " + key);
                            Log.i(TAG, "DIN userId = " + userId);
                            Map<String, Object> value = (Map<String, Object>) ds.getValue();
                            Log.i(TAG, "DIN notebook1 reference = " + value);

                            if(key.equals(userId)) {
                                // List notes only from this user id
                                Log.i(TAG, "DIN Inside For, size = " + value.size());
                                int i = 1;
                                for(Map.Entry<String, Object> entry : value.entrySet()) {
                                    String a = "MyNoteBook"+i;
                                    videoId = entry.getKey();
                                    //myVideoList.add(videoId);
                                    myVideoList[i-1]=videoId;
                                    Log.i(TAG, "DIN PRINT = " + videoId + "/" + entry.getValue());
                                    itemsAdapter.add(a);
                                    i++;
                                }
                            }
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError

                    }
                });

    }

    private void detachDatabaseReadListener() {
        /*if(mChildEventListener != null) {
            mMessagesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }*/

        //wipe resources
        //itemsAdapter.clear();
    }

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
