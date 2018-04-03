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
    private String mUsername, userId;
    private static final String TAG = "MyActivity";
    private ArrayAdapter<String> itemsAdapter;
    private ArrayList<String> listAdapter;
    private String videoId;
    private ArrayList<String> myVideoList1 = new ArrayList<String>();
    //String[] myVideoList = new String[100];
    public static final int RC_SIGN_IN = 1;
    public int count_of_notes_per_user = 0;
    private boolean login_once = false;

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
        ListView listNotesList;

        // Set layout for each item of the list view
        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        // set layout for each item of list view to arrayList
        //listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,);
        // Initialize ListView variable & bind the adapter to list view
        notesList = findViewById(R.id.lvNotesList);
        notesList.setAdapter(itemsAdapter);
        notesList.setLongClickable(true);

        listNotesList = findViewById(R.id.lvNotesList);


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
                        Log.i(TAG, "DIN ArrayList content " + myVideoList1.get(position));
                        //Log.i(TAG, "DIN ArrayList content " + myVideoList[counter]);
                        Intent intent = new Intent (view.getContext() , GuestActivity.class);
                        //intent.putExtra("VIDEO_ID", myVideoList[counter]);
                        intent.putExtra("VIDEO_ID", myVideoList1.get(position));
                        //intent.putExtra("USER_TYPE", userType);
                        intent.putExtra("USER_TYPE", "REGISTERED");
                        intent.putExtra("FROM", "USERNOTEBOOKS");
                        startActivityForResult(intent,0);
                    }
                }



                /**if (position == 0) {
                    Intent intent = new Intent (view.getContext() , GuestActivity.class);
                    intent.putExtra("VIDEO_ID", myVideoList[0]);
                    //intent.putExtra("USER_TYPE", userType);
                    intent.putExtra("USER_TYPE", "REGISTERED");
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
                }*/
            }
        });

        // Reference : https://stackoverflow.com/questions/23195208/how-to-pop-up-a-dialog-to-confirm-delete-when-user-long-press-on-the-list-item
        notesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                Toast.makeText(UserNoteBooks.this, "Long pressed", Toast.LENGTH_SHORT).show();

                AlertDialog.Builder alert = new AlertDialog.Builder(UserNoteBooks.this);
                alert.setTitle("Alert!!");
                alert.setMessage("Are you sure to delete record");
                alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //do your work here
                        dialog.dismiss();
                        //Log.i(TAG, "DIN Yes pressed for video ID " + myVideoList1.get(position));
                        //delete from arrayList
                        //myVideoList1.remove(position);
                        //function to delete from firebase database
                        final String videoId = myVideoList1.get(position);
                        Log.i(TAG, "DIN YES pressed videoId captured to be deleted is " + videoId);

                        deleteFromDatabase(position, videoId);

                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

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
                        for(DataSnapshot ds: dataSnapshot.getChildren()) {
                            String key = ds.getKey();
                            Log.i(TAG, "DIN deleteFromDatbaase ref1 key = " + key);
                            Log.i(TAG, "DIN deleteFromDatabase ref1 userId = " + userId);
                            Map<String, Object> value = (Map<String, Object>) ds.getValue();
                            if(key.equals(userId)) {
                                for(Map.Entry<String, Object> entry : value.entrySet()) {
                                    String noteBookName = entry.getValue().toString();
                                    String videoId1 = entry.getKey().toString();
                                    Log.i(TAG, "DIN deleteFromDatbaase ref1 received videoID= " + videoId);
                                    Log.i(TAG, "DIN deleteFromDatbaase ref1 to be checked videoID= " + videoId1);
                                    if(videoId1.equals(videoId)) {
                                        // found match
                                        Log.i(TAG, "DIN deleteFromDatbaase match found ! ref1 ID1= " + videoId1);
                                        // delete from database
                                        ref1.child(userId).child(videoId1).removeValue();
                                        Log.i(TAG, "DIN deleteFromDatbaase ref1 - Deleted from DB ");
                                        // Update the entry as well on screen !
                                        itemsAdapter.remove(noteBookName);
                                        Log.i(TAG, "DIN deleteFromDatbaase ref1 - removed from itemsAdapter " + itemsAdapter.getPosition(noteBookName));
                                        itemsAdapter.notifyDataSetChanged();
                                        Log.i(TAG, "DIN deleteFromDatbaase ref1 - Notify datasetChanged() ");
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
                            Log.i(TAG, "DIN deleteFromDatbaase ref2 key = " + key1);
                            Log.i(TAG, "DIN deleteFromDatabase ref2 userId = " + userId);
                            Map<String, Object> value1 = (Map<String, Object>) ds1.getValue();
                            if(key1.equals(userId.toString())) {
                                for(Map.Entry<String, Object> entry1 : value1.entrySet()) {
                                    String noteBookName = entry1.getValue().toString();
                                    String videoId2 = entry1.getKey().toString();

                                    Log.i(TAG, "DIN deleteFromDatbaase ref2 received videoID= " + videoId);
                                    Log.i(TAG, "DIN deleteFromDatbaase ref2 to be checked videoID2= " + videoId2);
                                    if(videoId2.equals(videoId)) {
                                        // found match

                                        // delete from database
                                        ref2.child(userId).child(videoId2).removeValue();
                                        Log.i(TAG, "DIN deleteFromDatbaase ref2 - Deleted from DB ");
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

                    }
                }
        );
    }

    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_SIGN_IN) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(UserNoteBooks.this, "Signed In !!",Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(UserNoteBooks.this, "Sign In Cancel !!",Toast.LENGTH_SHORT).show();
                finish();
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
        //detachDatabaseReadListener();
        //itemsAdapter.clear();
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

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("notebook");
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
                                    String noteBookName = entry.getValue().toString();
                                    videoId = entry.getKey().toString();
                                    myVideoList1.add(videoId);
                                    //myVideoList[i-1]=videoId;
                                    Log.i(TAG, "DIN PRINT = " + videoId + "/" + entry.getValue());
                                    itemsAdapter.add(noteBookName);
                                    i++;
                                    count_of_notes_per_user++;
                                }
                                Log.i(TAG, "DIN count of notes per user =  " + count_of_notes_per_user);
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), AfterLoginActivity.class);
        intent.putExtra("USER_TYPE", "REGISTERED");
        startActivity(intent);
    }

}