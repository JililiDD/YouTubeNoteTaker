package com.example.dingdang.youtubenotetaker;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

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
    private String userId;
    private ArrayAdapter<String> itemsAdapter;
    private String videoId;
    private ArrayList<String> myVideoList1 = new ArrayList<String>();
    public static final int RC_SIGN_IN = 1;
    public int count_of_notes_per_user = 0;
    private boolean login_once = false;

    // Firebase related varaibles
    private FirebaseAuth mFirebaseAuth;
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
        notesList.setLongClickable(true);

        // Instantiate Firebase varaibles
        mFirebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();

        if(user != null) {
            // User logged in
            onSignedInInitialize(user.getDisplayName(), user.getUid());


        }

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
                    // User signed out, so put in sign in flow
                    if(login_once == false) {
                            onSignedOutCleanup();
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
                        login_once = false;
                        Intent intent = new Intent(UserNoteBooks.this, MainActivity.class);
                        intent.putExtra("USER_TYPE", "GUEST");
                        startActivity(intent);
                    }
                }
            }
        };

        notesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                int count = count_of_notes_per_user, counter=0;
                for(counter=0; counter < count_of_notes_per_user; counter++) {
                    if(position == counter) {
                        //Load the video along with notes for selected video by user
                        Intent intent = new Intent (view.getContext() , GuestActivity.class);
                        intent.putExtra("VIDEO_ID", myVideoList1.get(position));
                        intent.putExtra("USER_TYPE", "REGISTERED");
                        intent.putExtra("FROM", "USERNOTEBOOKS");
                        startActivityForResult(intent,0);
                    }
                }
            }
        });

        // Reference : https://stackoverflow.com/questions/23195208/how-to-pop-up-a-dialog-to-confirm-delete-when-user-long-press-on-the-list-item
        notesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                // Alert the user to make sure user actually wants to delete the note book.
                AlertDialog.Builder alert = new AlertDialog.Builder(UserNoteBooks.this);
                alert.setTitle("Alert!!");
                alert.setMessage("Delete Notebook ?");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                        //function to delete from firebase database
                        final String videoId = myVideoList1.get(position);
                        deleteFromDatabase(position, videoId);

                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Just dismiss the dialogue, don't do anything.
                        dialog.dismiss();
                    }
                });

                alert.show();
                return true;
            }
        });
    }

    public void deleteFromDatabase(final int position, final String videoId) {

        final DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("notebook");
        final DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("user");

        ref1.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int deleted=0;
                        ListView notesListToUpdate;
                        for(DataSnapshot ds: dataSnapshot.getChildren()) {
                            // Retrieve the key and go through list of users to match userId
                            String key = ds.getKey();
                            Map<String, Object> value = (Map<String, Object>) ds.getValue();
                            if(key.equals(userId)) {
                                for(Map.Entry<String, Object> entry : value.entrySet()) {

                                    String noteBookName = entry.getValue().toString();
                                    String videoId1 = entry.getKey().toString();

                                    //videoId1 contains exact video which user wants to delete
                                    if(videoId1.equals(videoId)) {
                                        // found match, delete from database
                                        ref1.child(userId).child(videoId1).removeValue();

                                        // Update the entry as well on screen !
                                        itemsAdapter.remove(noteBookName);

                                        // Remove from the list as well
                                        myVideoList1.remove(videoId1);

                                        //Notify adapter about deletion
                                        itemsAdapter.notifyDataSetChanged();

                                        // set the flag about notes being deleted, to break from for loop.
                                        deleted = 1;
                                    }
                                    if(deleted == 1)
                                        break;
                                }
                            }
                            if(deleted == 1)
                                break;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // On cancelled, do not do anything
                    }
                }
        );

        ref2.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int deleted1 = 0;
                        for(DataSnapshot ds1: dataSnapshot.getChildren()) {
                            String key1 = ds1.getKey().toString();
                            Map<String, Object> value1 = (Map<String, Object>) ds1.getValue();
                            if(key1.equals(userId.toString())) {
                                for(Map.Entry<String, Object> entry1 : value1.entrySet()) {

                                    String noteBookName = entry1.getValue().toString();
                                    String videoId2 = entry1.getKey().toString();

                                    if(videoId2.equals(videoId)) {
                                        // found match, delete from database
                                        ref2.child(userId).child(videoId2).removeValue();
                                        deleted1 = 1;
                                        break;
                                    }
                                    if (deleted1 == 1)
                                        break;
                                }
                            }
                            if(deleted1 == 1)
                                break;
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Do not do anything currently, if database error comes as cancelled
                    }
                }
        );
    }

    // Reference : https://github.com/udacity/and-nd-firebase
    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_SIGN_IN) {
            if(resultCode == RESULT_OK) {
                // Sign in is successful. Do not do anything
            }
            else if (resultCode == RESULT_CANCELED) {
                // Load Main Activity for guest user
                Intent intent = new Intent(UserNoteBooks.this, MainActivity.class);
                intent.putExtra("USER_TYPE", "GUEST");
                startActivity(intent);
            }
        }
    }

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

    private void onSignedOutCleanup() {
        // clear all entries of notes displayed
        itemsAdapter.clear();
    }

    // Reference : https://github.com/udacity/and-nd-firebase
    private void onSignedInInitialize(String username1, String username2) {

        // Initialize userId and call functions to attach DatabaseReadListener
        userId = username2;
        attachDatabaseReadListener();

    }

    private void attachDatabaseReadListener() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("notebook");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        Long count_of_children = dataSnapshot.getChildrenCount();

                        for(DataSnapshot ds: dataSnapshot.getChildren()) {
                            String key = ds.getKey();
                            Map<String, Object> value = (Map<String, Object>) ds.getValue();

                            if(key.equals(userId)) {

                                // List notes only from this user id
                                for(Map.Entry<String, Object> entry : value.entrySet()) {
                                    String noteBookName = entry.getValue().toString();
                                    videoId = entry.getKey().toString();
                                    myVideoList1.add(videoId);
                                    itemsAdapter.add(noteBookName);
                                    count_of_notes_per_user++;
                                }
                            }
                        }


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //Do not do anything for database error

                    }
                });

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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), AfterLoginActivity.class);
        intent.putExtra("USER_TYPE", "REGISTERED");
        startActivity(intent);
    }

}