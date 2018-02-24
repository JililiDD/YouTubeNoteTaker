package com.example.dingdang.youtubenotetaker;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;

import java.util.ArrayList;


// Following code for embeding youtube player is referenced from https://www.youtube.com/watch?v=W4hTJybfU7s
// Need to extends YouTubeBaseActivity to use youtube API
public class GuestActivity extends AppCompatActivity implements NoteModeFragment.OnFragmentInteractionListener, VideoSearchFragment.OnFragmentInteractionListener{
    private YouTubePlayerSupportFragment youTubePlayerFragment;
    private LinearLayout tabView;
    private TabLayout tabs;
    private ViewPager pager;
    private PagerAdapter pagerAdapter;
    private long elapsedTime = 0;
    private static final String TAG = "Uri parse: ";


    private Button btnPlay, btnTakeNote, btnInitialize;
    private YouTubePlayer.OnInitializedListener mOnInitializedListener;
    private YouTubePlayer player;
    private ListView lvNotes;
    private ArrayAdapter<String> lvNotesItemAdapter;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest2);
        initializeYoutubePlayer();

        tabView = (LinearLayout) findViewById(R.id.tabView);

        // Initiate a tab layout and add tabs
        tabs = (TabLayout) findViewById(R.id.guestTabLayout);
        tabs.addTab(tabs.newTab().setText("Search"));
        tabs.addTab(tabs.newTab().setText("Note"));
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);

        pager = (ViewPager) findViewById(R.id.guestTabViewPager);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabs.getTabCount());
        pager.setAdapter(pagerAdapter);
        pager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));

        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    /**
     * initialize youtube player via Fragment and get instance of YoutubePlayer
     * Referred from http://www.androhub.com/implement-youtube-player-fragment-android-app/
     */
    private void initializeYoutubePlayer() {

        youTubePlayerFragment = (YouTubePlayerSupportFragment) getSupportFragmentManager()
                .findFragmentById(R.id.youtubeplayerFragment);

        if (youTubePlayerFragment == null)
            return;

        youTubePlayerFragment.initialize(YouTubeConfig.getApiKey(), new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer,
                                                boolean wasRestored) {
                if (!wasRestored) {
                    //youTubePlayer.loadVideo("W4hTJybfU7s"); //load and autoplay the video

                    // When youtube is initialized successfully, set player to youTubePlayer
                    // Then, player can be used for playing, pausing videos, etc.
                    player = youTubePlayer;

                    if(getIntent() == null){
                        player.cueVideo("W4hTJybfU7s"); //load but doesn't autoplay the video
                    }
                    else{
                        player.loadVideo(getIntent().getStringExtra("VIDEO_ID"));
                    }
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider arg0, YouTubeInitializationResult arg1) {

            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        String uriStr = uri.toString();
        String[] uriStrList = uriStr.split(" ");
        if(uri != null && uriStrList[0].equals("pause")){
            // Pause the video if TakeNote button is pressed in NoteModeFragment.
            // Referred from: http://blog.csdn.net/fengge34/article/details/46391453
            player.pause();

            // Take the elapsedTime
            elapsedTime = player.getCurrentTimeMillis();
        }
        else if(uri != null && uriStrList[0].equals("replay")){
            // Convert the passed time (in uriStrList[1]) from string to int
            String noteItem = uriStrList[1];
            int hr = Integer.parseInt(noteItem.substring(0, 2));
            int min = Integer.parseInt(noteItem.substring(3, 5));
            int sec = Integer.parseInt(noteItem.substring(6, 8));
            int msNoteTime = (hr*3600 + min*60 + sec) * 1000;

            //play the video back to the note time
            player.seekToMillis(msNoteTime);
        }
    }

    public long getElapsedTime(){
        return this.elapsedTime;
    }





    /**
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest2);

        btnPlay = (Button) findViewById(R.id.btnplay);
        btnTakeNote = (Button) findViewById(R.id.btnTakeNote);
        btnInitialize = (Button) findViewById(R.id.btnInitialize);
        mYouTubePlayerView = (YouTubePlayerView) findViewById(R.id.youtubeplayer);

        lvNotes = (ListView) findViewById(R.id.lvNotes);
        //sets the layout for each item of the list view
        lvNotesItemAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        //binds data from the Adapter to the ListView
        lvNotes.setAdapter(lvNotesItemAdapter);
        //set onItemClickListener to the lvNotes to allow user going back to the time point when the
        //note was taken
        //This is referenced from: https://www.youtube.com/watch?v=XyxT8IQoZkc
        lvNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //TODO: use note class object to get the actual elapsed time at note taken
                //below is a temporary way to calculate the note time
                String noteItem = lvNotesItemAdapter.getItem(i).trim();
                int hr = Integer.parseInt(noteItem.substring(0, 2));
                int min = Integer.parseInt(noteItem.substring(3, 5));
                int sec = Integer.parseInt(noteItem.substring(6, 8));
                int msNoteTime = (hr*3600 + min*60 + sec) * 1000;

                //play the video back to the note time
                player.seekToMillis(msNoteTime);
            }
        });

        mOnInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                //youTubePlayer.loadVideo("W4hTJybfU7s"); //load and autoplay the video

                // When youtube is initialized successfully, set player to youTubePlayer
                // Then, player can be used for playing, pausing videos, etc.
                player = youTubePlayer;
                player.cueVideo("W4hTJybfU7s"); //load but doesn't autoplay the video
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };

        // A Initialize button is for initializing the youtube player
        btnInitialize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mYouTubePlayerView.initialize(YouTubeConfig.getApiKey(), mOnInitializedListener);
            }
        });

        // A play button for playing the video
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                player.play();
            }
        });

        // A TakeNote button automatically stops the video to allow user taking notes
        btnTakeNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.pause();

                // Take elapsed time (milliseconds) when pause and convert it to hh:mm:ss format
                long elapsedTime = player.getCurrentTimeMillis();
                int hour = (int) (elapsedTime/(1000*3600));
                int min = (int) (elapsedTime/60000) % 60;
                int second = (int) (elapsedTime/1000) % 60;
                final String elapsedTimeString = String.format("%02d:%02d:%02d", hour, min, second);


                // Opens up a EditText for taking notes
                // Codes are referenced from: https://www.youtube.com/watch?v=BXTanDpOTVU
                View view = (LayoutInflater.from(GuestActivity.this)).inflate(R.layout.usr_note_input1,null);


                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(GuestActivity.this);
                alertBuilder.setView(view);
                final EditText userNoteInput = (EditText) view.findViewById(R.id.usrNoteInput);
                final EditText userSubjectInput = (EditText) view.findViewById(R.id.subject);

                // Add the elapsed time to the TextView in the popped up note-taking view by default
                TextView timeAtPause = (TextView) view.findViewById(R.id.elapsedTime);
                timeAtPause.setText(elapsedTimeString);

                alertBuilder.setCancelable(true).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //TODO: need a note class to store every note's info. A note class object is
                        //TODO: needed to store the user input notes.

                        String noteItemString = String.format("%s   %s", elapsedTimeString, userSubjectInput.getText().toString());
                        lvNotesItemAdapter.add(noteItemString);
                    }
                });

                Dialog dialog = alertBuilder.create();
                dialog.show();
            }
        });

    }
    */
}