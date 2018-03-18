package com.example.dingdang.youtubenotetaker;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by dinesh on 17/03/18.
 */

public class UserNoteBooks extends AppCompatActivity {

    // String to store the username of current user
    private String mUsername;
    private static final String TAG = "MyActivity";

    // Firebase related varaibles
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mMessagesDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private ChildEventListener mChildEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate Firebase varaibles
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference().child("testuser");
        FirebaseUser user = mFirebaseAuth.getCurrentUser();

        if(user != null) {
            // User logged in
            onSignedInInitialize(user.getDisplayName(), user.getUid());


        }



    }

    private void onSignedInInitialize(String username1, String username2) {
        mUsername = username1;
        String mUsername1 = username2;

        //attachDatabaseReadListener();

        Log.i(TAG, "DIN onSignedInInitialize, username = " + mUsername);
        Log.i(TAG, "DIN Username2 (id) = " + mUsername1);
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
                            Map<String, Object> value = (Map<String, Object>) ds.getValue();
                            Log.i(TAG, "DIN notebook1 reference = " + value);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError

                    }
                });



        /**if(mChildEventListener == null) {
            mChildEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.i(TAG, "DIN Inside attachDatabaseReadListener ");
                    long count = dataSnapshot.getChildrenCount();
                    Log.i(TAG, "DIN Total Children count =  " + count);
                    //FriendlyMessage friendlyMessage = dataSnapshot.getValue(FriendlyMessage.class);
                    //mMessageAdapter.add(friendlyMessage);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            };
            mMessagesDatabaseReference.addChildEventListener(mChildEventListener);
        } else {
            Log.i(TAG, "DIN Inside attachDatabaseReadListener else part ");
            //long count = dataSnapshot.getChildrenCount();
            //Log.i(TAG, "DIN Total Children count =  " + count);
        }*/
    }

    private void detachDatabaseReadListener() {
        /*if(mChildEventListener != null) {
            mMessagesDatabaseReference.removeEventListener(mChildEventListener);
            mChildEventListener = null;
        }*/
    }


}
