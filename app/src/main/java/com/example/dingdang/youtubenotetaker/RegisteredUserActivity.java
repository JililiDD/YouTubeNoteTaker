package com.example.dingdang.youtubenotetaker;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;


// Following code for embeding youtube player is referenced from https://www.youtube.com/watch?v=W4hTJybfU7s
// Need to extends YouTubeBaseActivity to use youtube API
public class RegisteredUserActivity extends AppCompatActivity implements NoteModeFragment.OnFragmentInteractionListener, VideoSearchFragment.OnFragmentInteractionListener{
    private YouTubePlayerSupportFragment youTubePlayerFragment;
    private LinearLayout tabView;
    private TabLayout tabs;
    private ViewPager pager;
    private PagerAdapter pagerAdapter;
    private long elapsedTime = 0;
    private static final String TAG = "Uri parse: ";
    private String userType;


    private Button btnPlay, btnTakeNote, btnInitialize;
    private YouTubePlayer.OnInitializedListener mOnInitializedListener;
    private YouTubePlayer player;
    private ListView lvNotes;
    private ArrayAdapter<String> lvNotesItemAdapter;





    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered_user);

        userType = getIntent().getStringExtra("USER_TYPE"); // Get user type to provide different functions to guest and member users

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

    public long getElapsedTime(){
        return this.elapsedTime;
    }

    public String getUserType(){
        return this.userType;
    }
}