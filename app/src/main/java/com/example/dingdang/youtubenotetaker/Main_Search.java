
package com.example.dingdang.youtubenotetaker;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 // * {@link VideoSearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 // * Use the {@link VideoSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */

/**
    This WHOLE search activity was referred from
    https://code.tutsplus.com/tutorials/create-a-youtube-client-on-android--cms-22858
 */
public class Main_Search extends AppCompatActivity {

    private FirebaseDatabase database;

    private DatabaseReference myReNoteBook;

    // Components needed for youtube search
    private EditText searchInput;
    private Button searchBtn;
    private ListView videosFound;
    private Handler handler;
    private List<VideoItem> searchResults;
    private String userType, previousActivity;
    private FirebaseUser user;
    private String useruid;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__search);
        userType = getIntent().getStringExtra("USER_TYPE"); // Get user type to provide different functions to guest and member users
        previousActivity = getIntent().getStringExtra("FROM");

        database= FirebaseDatabase.getInstance();

        searchBtn = (Button)findViewById(R.id.search_btn);
        searchInput = (EditText)findViewById(R.id.search_input);
        videosFound = (ListView)findViewById(R.id.videos_found);
                handler = new Handler();
        addClickListener();

        searchBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                searchOnYoutube(searchInput.getText().toString());
            }

//            @Override
//            public void onClick(TextView v, int actionId, KeyEvent event) {
//                if(actionId == EditorInfo.IME_ACTION_DONE){
//                    searchOnYoutube(v.getText().toString());
//                    return false;
//                }
//                return true;
//            }
        });

    }

    private void searchOnYoutube(final String keywords){
        new Thread(){
            public void run(){
        YoutubeConnector yc = new YoutubeConnector(Main_Search.this);
        searchResults = yc.search(keywords);
        handler.post(new Runnable(){
                    public void run(){
                        updateVideosFound();
                    }
                });
            }
        }.start();
    }

    // Following youtube search functionality was referred from:
    // https://code.tutsplus.com/tutorials/create-a-youtube-client-on-an-android--cms-22858
    private void updateVideosFound(){
        ArrayAdapter<VideoItem> adapter = new ArrayAdapter<VideoItem>(getApplicationContext(), R.layout.video_item, searchResults){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.video_item, parent, false);
                }
                ImageView thumbnail = (ImageView)convertView.findViewById(R.id.video_thumbnail);
                TextView title = (TextView)convertView.findViewById(R.id.video_title);
                TextView description = (TextView)convertView.findViewById(R.id.video_description);

                VideoItem searchResult = searchResults.get(position);
                Picasso.with(getApplicationContext()).load(searchResult.getThumbnailURL()).into(thumbnail);
                title.setText(searchResult.getTitle());
                description.setText(searchResult.getDescription());
                return convertView;
            }
        };
        videosFound.setAdapter(adapter);
    }

    private void addClickListener(){
        NoteModeFragment.noteList.clear();
        videosFound.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, final int pos,
                                    long id) {
                if(isRegisteredUser()) {
                    Intent intent = new Intent(getApplicationContext(), GuestActivity.class);
                    intent.putExtra("VIDEO_ID", searchResults.get(pos).getId());
                    final String youtubeID=searchResults.get(pos).getId();
                    user = FirebaseAuth.getInstance().getCurrentUser();
                    //get the current userId
                    useruid=user.getUid();
                    myReNoteBook=database.getReference("notebook").child(useruid);
                    myReNoteBook.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Boolean notebookChecker = false;
                            for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                if (childDataSnapshot.getKey().equals(youtubeID)) {
                                    Log.i("youtueb", youtubeID);
                                    notebookChecker = true;
                                }
                            }
                            if (!notebookChecker) {
                                Intent intent = new Intent(getApplicationContext(), AddNotebookNameActivity.class);
                                intent.putExtra("VIDEO_ID", youtubeID);
                                intent.putExtra("USER_TYPE", userType);
                                startActivity(intent);
                            } else if (notebookChecker) {
                                Dialog deleteConfirmBox = new android.support.v7.app.AlertDialog.Builder(Main_Search.this)
                                        .setMessage("You have already made a notebook on this video.\nDo you want to open the notebook?")
                                        .setPositiveButton("Open", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int whichButton) {
                                                Intent intent = new Intent(getApplicationContext(), GuestActivity.class);
                                                intent.putExtra("VIDEO_ID", youtubeID);
                                                intent.putExtra("USER_TYPE", userType);
                                                startActivity(intent);
                                                dialog.dismiss();
                                            }
                                        })
                                        .setNegativeButton("Cancel", null)
                                        .create();
                                deleteConfirmBox.show();
                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });

                }
                else{
                    Intent intent = new Intent(getApplicationContext(), GuestActivity.class);
                    intent.putExtra("VIDEO_ID", searchResults.get(pos).getId());
                    intent.putExtra("USER_TYPE", "GUEST");
                    startActivity(intent);
                }
            }

        });
    }

    public boolean isRegisteredUser(){
        return userType.equals("REGISTERED");
    }

    @Override
    public void onBackPressed() {
        if(previousActivity != null){
            super.onBackPressed();
        }
        else if (isRegisteredUser()) {
            Intent intent = new Intent(getApplicationContext(), AfterLoginActivity.class);
            intent.putExtra("USER_TYPE", "REGISTERED");
            startActivity(intent);
        } else{
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }
}



