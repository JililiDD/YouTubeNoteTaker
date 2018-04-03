package com.example.dingdang.youtubenotetaker;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


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
    private String userType;
    private String youtubeId;
    FloatingActionButton floatbtn;
    private String linkExist, previousActivity;
    private FirebaseUser user;
    private String useruid;

    private EditText searchText;

    private Button btnPlay, btnTakeNote, btnInitialize;
    private YouTubePlayer.OnInitializedListener mOnInitializedListener;
    private YouTubePlayer player;
    private ListView lvNotes;
    private ArrayAdapter<String> lvNotesItemAdapter;





    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest2);

        userType = getIntent().getStringExtra("USER_TYPE"); // Get user type to provide different functions to guest and member users
        youtubeId = getIntent().getStringExtra("VIDEO_ID"); // Get youtube video id (YIWEI)
        linkExist=getIntent().getStringExtra("LINK_EXIST");
        previousActivity = getIntent().getStringExtra("FROM");

        //Remove back button on the title bar
        //Code referenced from: https://stackoverflow.com/a/22313897
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        initializeYoutubePlayer();

        tabView = (LinearLayout) findViewById(R.id.tabView);

        // Initiate a tab layout and add tabs
        tabs = (TabLayout) findViewById(R.id.guestTabLayout);
//        tabs.addTab(tabs.newTab().setText("Search"));
        tabs.addTab(tabs.newTab().setText("Note"));
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);

        pager = (ViewPager) findViewById(R.id.guestTabViewPager);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabs.getTabCount());
        pager.setAdapter(pagerAdapter);
        pager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));

        floatbtn=(FloatingActionButton) findViewById(R.id.btnFloat);
        floatbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showpopup();
            }
        });
        searchText = (EditText)findViewById(R.id.search_input);
        searchText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Main_Search.class);
                intent.putExtra("USER_TYPE", userType);
                startActivity(intent);
            }
        });
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

    private void showpopup() {
        android.app.AlertDialog.Builder popup = new android.app.AlertDialog.Builder(GuestActivity.this);
        popup.setMessage("<Enter Notes..>");
        popup.setTitle("Notes");
        popup.setCancelable(true);
        android.app.AlertDialog alt=popup.create();
        alt.show();
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
            public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer,
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
                        //youtube ID
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
            String noteTime = uriStrList[1];
            int msNoteTime = Integer.valueOf(noteTime);

            //play the video back to the note time
            player.seekToMillis(msNoteTime);
        }
    }

    @Override
    public void onBackPressed()
    {
        Log.i(TAG, "SHENOY BACK BUTTON PRESSED!! ");

    }
    public boolean isRegisteredUser(){
        return userType.equals("REGISTERED");
    }

    public long getElapsedTime(){
        return this.elapsedTime;
    }

    public String getUserType(){
        return this.userType;
    }

    public String getLinkExistType(){
        return this.linkExist;
    }

    public void SetLinkExistType(String s){
        this.linkExist=s;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    @Override
    public void onBackPressed() {
        if(isRegisteredUser() && previousActivity != null) {
            Intent intent = new Intent(getApplicationContext(), UserNoteBooks.class);
            intent.putExtra("USER_TYPE", "REGISTERED");
            startActivity(intent);
        } else if(isRegisteredUser() && previousActivity == null){
            Intent intent = new Intent(getApplicationContext(), AfterLoginActivity.class);
            intent.putExtra("USER_TYPE", "REGISTERED");
            startActivity(intent);
        } else if (!isRegisteredUser()){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }
    }
}