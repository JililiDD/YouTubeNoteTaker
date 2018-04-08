package com.example.dingdang.youtubenotetaker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;


// Following code for embedding youtube player is referenced from https://www.youtube.com/watch?v=W4hTJybfU7s
// Need to extends YouTubeBaseActivity to use youtube API
public class GuestActivity extends AppCompatActivity implements NoteModeFragment.OnFragmentInteractionListener{
    private YouTubePlayerSupportFragment youTubePlayerFragment;
    private LinearLayout tabView;
    private TabLayout tabs;
    private ViewPager pager;
    private PagerAdapter pagerAdapter;
    private static final String TAG = "Uri parse: ";
    private String linkExist, previousActivity;
    private android.app.AlertDialog alt;

    private EditText searchText;

    private Button btnPlay, btnInitialize;
    private YouTubePlayer.OnInitializedListener mOnInitializedListener;
    private YouTubePlayer player;
    private int playerRestoredTime;

    private int currentOrientation;

    // Components
    private Button btnEmail, btnTakeNote, btnSave, btnCancel,btnClose,
            btnEditNoteCancel, btnEditNoteSave, btnEditDelete,btnShowNoteCancel,btnShwoEdit,btnShwoReplay;
    private ListView lvNotes;
    private LinearLayout llNoteList,LL_showNote;
    private RelativeLayout rlNotepad, rlEditNote;
    private EditText etUserNoteInput, etUserSubjectInput, etEditSubject, etEditNote;
    private TextView tvTimeAtPause, tvEditNoteTime,ShowNoteElapsedTime,ShowNoteSubject,ShowNoteUsrNoteInputText;
    private long elapsedTime = 0;
    private NoteItem selectedNote;
    private String userType, youtubeId;
    private FirebaseUser user;
    private String useruid;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    public NoteItem getSelectedNote() {
        return selectedNote;
    }

    public void setSelectedNote(NoteItem selectedNote) {
        this.selectedNote = selectedNote;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest2);

        // currentOrientation is suppose to be equal to Configuration.ORIENTATION_PORTRAIT
        currentOrientation = getResources().getConfiguration().orientation;

        userType = getIntent().getStringExtra("USER_TYPE"); // Get user type to provide different functions to guest and member users
        youtubeId = getIntent().getStringExtra("VIDEO_ID"); // Get youtube video id (YIWEI)
        linkExist=getIntent().getStringExtra("LINK_EXIST");
        previousActivity = getIntent().getStringExtra("FROM");

        //Remove back button on the title bar
        //Code referenced from: https://stackoverflow.com/a/22313897
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        if( savedInstanceState != null){ //b
            playerRestoredTime = savedInstanceState.getInt("ORIENTATIONTIME");

        }
        initializeYoutubePlayer();
        tabView = (LinearLayout) findViewById(R.id.tabView);

        // Initiate a tab layout and add tabs
        tabs = (TabLayout) findViewById(R.id.guestTabLayout);
        tabs.addTab(tabs.newTab().setText("Note"));
        tabs.setTabGravity(TabLayout.GRAVITY_FILL);

        pager = (ViewPager) findViewById(R.id.guestTabViewPager);
        pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabs.getTabCount());
        pager.setAdapter(pagerAdapter);
        pager.setOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabs));



        searchText = (EditText)findViewById(R.id.search_input);
        searchText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Main_Search.class);
                intent.putExtra("USER_TYPE", userType);
                intent.putExtra("FROM", "NOTEMODE");
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

    private void showpopup() { //b
        android.app.AlertDialog.Builder popup = new android.app.AlertDialog.Builder(GuestActivity.this);
        View v=getLayoutInflater().inflate(R.layout.dialoglayout,null);

        popup.setView(v);

        intializeorientionHandler(v);
        popup.setCancelable(true);
        alt=popup.create();
        alt.show();
    } //b


    private void intializeorientionHandler(View view) { //b
        btnClose=(Button)view.findViewById(R.id.btnClose);
        btnEmail = (Button) view.findViewById(R.id.btnEmailNote);
        btnTakeNote = (Button) view.findViewById(R.id.btnTakeNote);
        btnSave = (Button) view.findViewById(R.id.btnSave);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnEditNoteSave = (Button) view.findViewById(R.id.btnEditNoteSave);
        btnEditNoteCancel = (Button) view.findViewById(R.id.btnEditNoteCancel);
        btnEditDelete = (Button) view.findViewById(R.id.btnEditDelete);
        lvNotes = (ListView) view.findViewById(R.id.lvNotes);
        llNoteList = (LinearLayout) view.findViewById(R.id.ll_noteList);
        rlNotepad = (RelativeLayout) view.findViewById(R.id.RL_notepad);
        rlEditNote = (RelativeLayout) view.findViewById(R.id.RL_editNote);
        etUserNoteInput = (EditText) view.findViewById(R.id.usrNoteInput);
        etUserSubjectInput = (EditText) view.findViewById(R.id.subject);
        tvTimeAtPause = (TextView) view.findViewById(R.id.elapsedTime);
        tvEditNoteTime = (TextView) view.findViewById(R.id.EditNoteElapsedTime);
        etEditSubject = (EditText) view.findViewById(R.id.EditNoteSubject);
        etEditNote = (EditText) view.findViewById(R.id.EditNoteUsrNoteInput);
        LL_showNote = (LinearLayout) view.findViewById(R.id.LL_showNote);
        btnShwoReplay = (Button) view.findViewById(R.id.btnShwoReplay);

        btnShowNoteCancel = (Button) view.findViewById(R.id.btnShowNoteCancel);
        btnShwoEdit = (Button) view.findViewById(R.id.btnShwoEdit);

        //ShowNoteElapsedTime,ShowNoteSubject,ShowNoteUsrNoteInputText
        ShowNoteElapsedTime = (TextView) view.findViewById(R.id.ShowNoteElapsedTime);
        ShowNoteSubject = (TextView) view.findViewById(R.id.ShowNoteSubject);
        ShowNoteUsrNoteInputText = (TextView) view.findViewById(R.id.ShowNoteUsrNoteInputText);

        database = FirebaseDatabase.getInstance();

        int currentOrientation = getResources().getConfiguration().orientation; //b
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE){
            btnClose.setVisibility(View.VISIBLE);
        }
        else {
            btnClose.setVisibility(View.GONE);
        } //b

        final List<NoteItem> noteList = NoteModeFragment.noteList;

        // Get userType and youtube video ID from current GuestActivity
        GuestActivity guestActivity = GuestActivity.this;
        userType = getUserType();

        //the youtubeID
        youtubeId = guestActivity.getYoutubeId(); //YIWEI
        Log.i("vedio id", youtubeId);

        // Hide rlNotepad and rlEditNote UIs by default
        rlNotepad.setVisibility(View.GONE);
        rlEditNote.setVisibility(View.GONE);
        LL_showNote.setVisibility(View.GONE);


        btnClose.setOnClickListener(new OnClickListener() { //b
            @Override
            public void onClick(View v) {
                if(player != null){
                    player.play();}

                alt.dismiss();}
            //   }
        }); //b

        if (isRegisteredUser()) {
            user = FirebaseAuth.getInstance().getCurrentUser();
            //get the current userId
            useruid = user.getUid();
            myRef = database.getReference("user").child(useruid);
            DatabaseReference myRefyouTube = database.getReference("user").child(useruid).child(youtubeId);
            myRefyouTube.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ArrayAdapter<NoteItem> lvNotesItemAdapter1 = new ArrayAdapter<>(GuestActivity.this.getApplicationContext(),
                            R.layout.item_black, noteList);
                    noteList.clear();
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                        HashMap<String, String> newItem = (HashMap<String, String>) childDataSnapshot.getValue();
                        NoteItem newNoteItem = new NoteItem(0, newItem.get("Time"), newItem.get("Subject"), newItem.get("Note"));
                        newNoteItem.setNoteId(newItem.get("NoteId"));
                        newNoteItem.setNotebookName(newItem.get("NotebookName"));
                        newNoteItem.setSelected("false");
                        lvNotesItemAdapter1.add(newNoteItem);
                        lvNotes.clearChoices();
                        lvNotes.setAdapter(lvNotesItemAdapter1);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });

        } else{
            ArrayAdapter<NoteItem> lvNotesItemAdapter = new ArrayAdapter<>(GuestActivity.this.getApplicationContext(),
                    R.layout.item_black, noteList);
            lvNotes.setAdapter(lvNotesItemAdapter);
        }

        btnShowNoteCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llNoteList.setVisibility(View.VISIBLE);
                rlNotepad.setVisibility(View.GONE);
                rlEditNote.setVisibility(View.GONE);
                LL_showNote.setVisibility(View.GONE);

                if (isRegisteredUser()) {
                    myRef = database.getReference("user").child(useruid).child(youtubeId);
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String theselectid = "";
                            for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                HashMap<String, String> newItem = (HashMap<String, String>) childDataSnapshot.getValue();
                                if (newItem.get("Selected").equals("true")) {
                                    theselectid = newItem.get("NoteId");
                                }

                            }
                            myRef.child(theselectid).child("Selected").setValue("false");

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    llNoteList.setVisibility(View.VISIBLE);  // Hide llNoteList UI
                    rlNotepad.setVisibility(View.GONE); // Display rlNotepad UI
                    rlEditNote.setVisibility(View.GONE); // Hide rlEditNote UI
                    LL_showNote.setVisibility(View.GONE);
                }


            }
        });

        btnShwoReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRegisteredUser()) {
                    myRef = database.getReference("user").child(useruid).child(youtubeId);
                    myRef.orderByChild("Selected").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String theselectid = "";
                            String timestr = "";
                            for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                HashMap<String, String> newItem = (HashMap<String, String>) childDataSnapshot.getValue();
                                if (newItem.get("Selected").equals("true")) {
                                    theselectid = newItem.get("NoteId");
                                    timestr = newItem.get("CurrentTime");
                                }
                            }
                            alt.dismiss();
                            player.seekToMillis(Integer.parseInt(timestr));
                            player.play();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });


                } else {
                    // Referred from: http://blog.csdn.net/fengge34/article/details/46391453
                    // Pass the note time to GuestActivity as well
                    alt.dismiss();
                    player.seekToMillis((int)getSelectedNote().getCurrentTime());
                    player.play();
                }

            }
        });


        btnShwoEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llNoteList.setVisibility(View.GONE);  // Hide llNoteList UI
                rlNotepad.setVisibility(View.GONE); // Display rlNotepad UI
                rlEditNote.setVisibility(View.VISIBLE); // Hide rlEditNote UI
                LL_showNote.setVisibility(View.GONE);
            }
        });


        /** ListView UI buttons*/
        btnEmail.setOnClickListener(new View.OnClickListener() {
            //https://www.youtube.com/watch?v=_
            @Override
            public void onClick(View view) {
                if (noteList.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "No notes taken", Toast.LENGTH_SHORT).show();
                } else {
                    String emailYoutubeURL = "https://www.youtube.com/watch?v=_" + youtubeId + "\n" + "\n";
                    // Create email message content from notes taken
                    StringBuilder emailContent = new StringBuilder();
                    emailContent.append(emailYoutubeURL);
                    for (NoteItem notes : noteList) {
                        emailContent.append(notes.toEmailFormat());
                    }

                    // Create intent to evoke EmailActivity and pass the message in an Extra
                    Intent intent = new Intent(GuestActivity.this.getApplicationContext(), EmailActivity.class);
                    intent.putExtra("MESSAGE", emailContent.toString());
                    startActivity(intent);
                }

            }
        });

        btnTakeNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llNoteList.setVisibility(View.GONE);  // Hide llNoteList UI
                rlNotepad.setVisibility(View.VISIBLE); // Display rlNotepad UI
                rlEditNote.setVisibility(View.GONE); // Hide rlEditNote UI
                LL_showNote.setVisibility(View.GONE);

                // Take elapsed time (milliseconds) when pause from GuestActivity using a getter method in GuestActivity
                // Using Bundle to pass data from GuestActivity to NoteModeFragment doesn't work in this case
                // Referenced from: https://stackoverflow.com/a/22065903
                GuestActivity guestActivity =  (GuestActivity) GuestActivity.this;
                elapsedTime = guestActivity.getElapsedTime();

                // Convert elapsedTime to hh:mm:ss format
                int hour = (int) (elapsedTime / (1000 * 3600));
                int min = (int) (elapsedTime / 60000) % 60;
                int second = (int) (elapsedTime / 1000) % 60;
                final String elapsedTimeString = String.format("%02d:%02d:%02d", hour, min, second);

                tvTimeAtPause.setText(elapsedTimeString);
            }
        });

        /** Note taking UI buttons*/
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etUserSubjectInput.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"Note subject cannot be empty",Toast.LENGTH_SHORT).show();
                }
                else{
                    // Create an ArrayAdapter<NoteItem> for storing NoteItem objects
                    ArrayAdapter<NoteItem> lvNotesItemAdapter = new ArrayAdapter<>(GuestActivity.this.getApplicationContext(),
                            R.layout.item_black, noteList);
                    NoteItem noteItem = new NoteItem(elapsedTime, tvTimeAtPause.getText().toString(), etUserSubjectInput.getText().toString(), etUserNoteInput.getText().toString());

                    if (isRegisteredUser()) {
                        myRef = database.getReference("user").child(useruid).child(youtubeId);
                        String theNoteId = myRef.push().getKey();
                        noteItem.setNoteId(theNoteId);
                        Map<String, NoteItem> theData = noteItem.putInToFireBase();

                        //Map<String, String> userData=tempItem.putInToFireBase();
                        myRef.child(theNoteId).setValue(theData);

                        //DatabaseReference myRef1 = database.getReference("user").child(useruid);
                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            ArrayAdapter<NoteItem> lvNotesItemAdapter1 = new ArrayAdapter<>(GuestActivity.this.getApplicationContext(),
                                    R.layout.item_black, noteList);

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                noteList.clear();
                                for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                    HashMap<String, String> newItem = (HashMap<String, String>) childDataSnapshot.getValue();

                                    NoteItem newNoteItem = new NoteItem(0, newItem.get("Time"), newItem.get("Subject"), newItem.get("Note"));
                                    newNoteItem.setNoteId(newItem.get("NoteId"));
                                    newNoteItem.setNotebookName(newItem.get("NotebookName"));
                                    newNoteItem.setSelected("false");
                                    lvNotesItemAdapter1.add(newNoteItem);
                                }
                                lvNotesItemAdapter1.notifyDataSetChanged();
                                lvNotes.setAdapter(lvNotesItemAdapter1);
                                llNoteList.setVisibility(View.VISIBLE);  // Display llNoteList UI
                                rlNotepad.setVisibility(View.GONE); // Hide rlNotepad UI
                                rlEditNote.setVisibility(View.GONE); // Hide rlEditNote UIi=0;
                                LL_showNote.setVisibility(View.GONE);
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    } else {
                        lvNotesItemAdapter.add(noteItem);
                        lvNotes.setAdapter(lvNotesItemAdapter);

                        llNoteList.setVisibility(View.VISIBLE);  // Display llNoteList UI
                        rlNotepad.setVisibility(View.GONE); // Hide rlNotepad UI
                        rlEditNote.setVisibility(View.GONE); // Hide rlEditNote UI
                        LL_showNote.setVisibility(View.GONE);
                    }
                    etUserNoteInput.getText().clear();
                    etUserSubjectInput.getText().clear();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Clear user inputs
                etUserNoteInput.getText().clear();
                etUserSubjectInput.getText().clear();

                llNoteList.setVisibility(View.VISIBLE);  // Display llNoteList UI
                rlNotepad.setVisibility(View.GONE); // Hide rlNotepad UI
                rlEditNote.setVisibility(View.GONE); // Hide rlEditNote UI
                LL_showNote.setVisibility(View.GONE);
            }
        });

        /** Note editing UI buttons*/
        btnEditNoteSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRegisteredUser()) {
                    myRef = database.getReference("user").child(useruid).child(youtubeId);
                    myRef.orderByChild("Selected").addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String theselectid = "";
                            for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                HashMap<String, String> newItem = (HashMap<String, String>) childDataSnapshot.getValue();
                                if (newItem.get("Selected").equals("true")) {
                                    theselectid = newItem.get("NoteId");
                                }

                            }
                            myRef.child(theselectid).child("Selected").setValue("false");
                            myRef.child(theselectid).child("Subject").setValue(etEditSubject.getText().toString());
                            myRef.child(theselectid).child("Note").setValue(etEditNote.getText().toString());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });
                    myRef = database.getReference("user").child(useruid).child(youtubeId);
                    myRef.addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            noteList.clear();
                            ArrayAdapter<NoteItem> lvNotesItemAdapter1 = new ArrayAdapter<>(GuestActivity.this.getApplicationContext(),
                                    R.layout.item_black, noteList);

                            String theselectid = "";
                            for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                HashMap<String, String> newItem = (HashMap<String, String>) childDataSnapshot.getValue();

                                NoteItem newNoteItem = new NoteItem(0, newItem.get("Time"), newItem.get("Subject"), newItem.get("Note"));
                                newNoteItem.setNoteId(newItem.get("NoteId"));
                                newNoteItem.setNotebookName(newItem.get("NotebookName"));
                                lvNotesItemAdapter1.add(newNoteItem);
                            }

                            lvNotes.setAdapter(lvNotesItemAdapter1);
                            llNoteList.setVisibility(View.VISIBLE);  // Display llNoteList UI
                            rlNotepad.setVisibility(View.GONE); // Hide rlNotepad UI
                            rlEditNote.setVisibility(View.GONE); // Hide rlEditNote UIi=0;
                            LL_showNote.setVisibility(View.GONE);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    getSelectedNote().setSubject(etEditSubject.getText().toString());
                    getSelectedNote().setNote(etEditNote.getText().toString());

                    // Update the ListView
                    ArrayAdapter<NoteItem> lvNotesItemAdapter = new ArrayAdapter<>(GuestActivity.this.getApplicationContext(), R.layout.item_black, noteList);
                    lvNotes.setAdapter(lvNotesItemAdapter);

                    llNoteList.setVisibility(View.VISIBLE);  // Display llNoteList UI
                    rlNotepad.setVisibility(View.GONE); // Hide rlNotepad UI
                    rlEditNote.setVisibility(View.GONE); // Hide rlEditNote UI
                    LL_showNote.setVisibility(View.GONE);
                }
            }
        });

        btnEditNoteCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llNoteList.setVisibility(View.VISIBLE);  // Display llNoteList UI
                rlNotepad.setVisibility(View.GONE); // Hide rlNotepad UI
                rlEditNote.setVisibility(View.GONE); // Hide rlEditNote UI
                LL_showNote.setVisibility(View.GONE);

            }
        });

        btnEditDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRegisteredUser()) {
                    rlEditNote.setVisibility(View.VISIBLE);
                    llNoteList.setVisibility(View.GONE);
                    rlNotepad.setVisibility(View.GONE);
                    LL_showNote.setVisibility(View.GONE);
                    AlertDialog.Builder delnote = new AlertDialog.Builder(GuestActivity.this);
                    delnote.setMessage("Delete note?");
                    delnote.setCancelable(true);

                    delnote.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    myRef = database.getReference("user").child(useruid).child(youtubeId);
                                    myRef.orderByChild("Selected").addListenerForSingleValueEvent(new ValueEventListener() {

                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            noteList.clear();
                                            ArrayAdapter<NoteItem> lvNotesItemAdapter1 = new ArrayAdapter<>(GuestActivity.this.getApplicationContext(),
                                                    R.layout.item_black, noteList);

                                            String theselectid = "";
                                            for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                                HashMap<String, String> newItem = (HashMap<String, String>) childDataSnapshot.getValue();
                                                if (newItem.get("Selected").equals("true")) {
                                                    theselectid = newItem.get("NoteId");
                                                } else if (newItem.get("Selected").equals("false")) {
                                                    NoteItem newNoteItem = new NoteItem(0, newItem.get("Time"), newItem.get("Subject"), newItem.get("Note"));
                                                    newNoteItem.setNoteId(newItem.get("NoteId"));
                                                    newNoteItem.setNotebookName(newItem.get("NotebookName"));
                                                    lvNotesItemAdapter1.add(newNoteItem);

                                                }

                                            }
                                            myRef.child(theselectid).removeValue();

                                            lvNotesItemAdapter1.notifyDataSetChanged();
                                            lvNotes.setAdapter(lvNotesItemAdapter1);
                                            llNoteList.setVisibility(View.VISIBLE);  // Display llNoteList UI
                                            rlNotepad.setVisibility(View.GONE); // Hide rlNotepad UI
                                            rlEditNote.setVisibility(View.GONE); // Hide rlEditNote UIi=0;
                                            LL_showNote.setVisibility(View.GONE);

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                    dialog.cancel();
                                    Toast.makeText(getApplicationContext(), "Note deleted", Toast.LENGTH_SHORT).show();
                                }
                            });

                    delnote.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alertdel = delnote.create();
                    alertdel.show();
                } else {
                    // Delete the selected note item
                    AlertDialog.Builder delnote = new AlertDialog.Builder(GuestActivity.this);
                    delnote.setMessage("Delete note?");
                    delnote.setCancelable(true);
                    delnote.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    NoteItem selectedNoteItem = getSelectedNote();
                                    String removeItemNoteId =selectedNoteItem.getNoteId();
                                    noteList.remove(selectedNoteItem);
                                    ArrayAdapter<NoteItem> lvNotesItemAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.item_black, noteList);
                                    lvNotes.setAdapter(lvNotesItemAdapter);

                                    llNoteList.setVisibility(View.VISIBLE);  // Display llNoteList UI
                                    rlNotepad.setVisibility(View.GONE); // Hide rlNotepad UI
                                    rlEditNote.setVisibility(View.GONE); // Hide rlEditNote UI
                                    LL_showNote.setVisibility(View.GONE);
                                    dialog.cancel();
                                    Toast.makeText(getApplicationContext(),"Note deleted",Toast.LENGTH_SHORT).show();
                                }
                            });

                    delnote.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alertdel = delnote.create();
                    alertdel.show();
                }

            }
        });
        lvNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int pos, long l) {
                if (isRegisteredUser()) {
                    myRef = database.getReference("user").child(useruid).child(youtubeId);
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ArrayAdapter<NoteItem> lvNotesItemAdapter1 = new ArrayAdapter<>(GuestActivity.this.getApplicationContext(),
                                    R.layout.item_black, noteList);
                            noteList.clear();

                            for (final DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                HashMap<String, String> newItem = (HashMap<String, String>) childDataSnapshot.getValue();
                                NoteItem newNoteItem = new NoteItem(0, newItem.get("Time"), newItem.get("Subject"), newItem.get("Note"));
                                newNoteItem.setNoteId(newItem.get("NoteId"));
                                newNoteItem.setNotebookName(newItem.get("NotebookName"));
                                noteList.add(newNoteItem);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    setSelectedNote(noteList.get(pos));
                    NoteItem seItem = noteList.get(pos);
                    lvNotes.clearChoices();
                    DatabaseReference myChildrenRef1 = myRef.child(seItem.getNoteId());
                    //String seID=seItem.getNoteId().toString();

                    myChildrenRef1.child("Selected").setValue("true");
                    myChildrenRef1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //ShowNoteElapsedTime,ShowNoteSubject,ShowNoteUsrNoteInputText
                            ShowNoteElapsedTime.setText(dataSnapshot.child("Time").getValue().toString());
                            ShowNoteSubject.setText(dataSnapshot.child("Subject").getValue().toString());
                            ShowNoteUsrNoteInputText.setText(dataSnapshot.child("Note").getValue().toString());
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });

                    tvEditNoteTime.setText(selectedNote.getTime());
                    etEditSubject.setText(selectedNote.getSubject());
                    etEditNote.setText(selectedNote.getNote());

                    LL_showNote.setVisibility(View.VISIBLE);
                    rlEditNote.setVisibility(View.GONE);
                    llNoteList.setVisibility(View.GONE);
                    rlNotepad.setVisibility(View.GONE);
                } else {
                    setSelectedNote(noteList.get(pos));
                    ShowNoteElapsedTime.setText(selectedNote.getTime());
                    ShowNoteSubject.setText(selectedNote.getSubject());
                    ShowNoteUsrNoteInputText.setText(selectedNote.getNote());

                    //Set the text fields to the NoteItem object's corresponding values in edit UI
                    tvEditNoteTime.setText(selectedNote.getTime());
                    etEditSubject.setText(selectedNote.getSubject());
                    etEditNote.setText(selectedNote.getNote());

                    rlEditNote.setVisibility(View.GONE);
                    llNoteList.setVisibility(View.GONE);
                    rlNotepad.setVisibility(View.GONE);
                    LL_showNote.setVisibility(View.VISIBLE);
                }
            }
        });
    } //b

    /**
     * initialize youtube player via Fragment and get instance of YoutubePlayer
     * Referred from http://www.androhub.com/implement-youtube-player-fragment-android-app/
     */
    private void initializeYoutubePlayer() {
        youTubePlayerFragment = (YouTubePlayerSupportFragment) getSupportFragmentManager()
                .findFragmentById(R.id.youtubeplayerFragment);
        youTubePlayerFragment.setRetainInstance(true);

        if (youTubePlayerFragment == null)
            return;

        youTubePlayerFragment.initialize(YouTubeConfig.getApiKey(), new YouTubePlayer.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, final YouTubePlayer youTubePlayer,
                                                boolean wasRestored) {
                if (!wasRestored) {
                    // When youtube is initialized successfully, set player to youTubePlayer
                    // Then, player can be used for playing, pausing videos, etc.
                    player = youTubePlayer;

                    if(getIntent() == null){
                        player.cueVideo("W4hTJybfU7s"); //load but doesn't autoplay the video
                    }
                    else{
                        //youtube ID
                        player.loadVideo(getIntent().getStringExtra("VIDEO_ID")); //b
                    }
                }else{
                    player = youTubePlayer;
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
            initializeYoutubePlayer(); //b
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



    public boolean isRegisteredUser(){
        return userType.equals("REGISTERED");
    }

    public long getElapsedTime(){
        return this.elapsedTime;
    }

    public String getUserType(){
        return this.userType;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) { //b

        int currentOrientation = getResources().getConfiguration().orientation;
        if (currentOrientation == Configuration.ORIENTATION_LANDSCAPE) {
            getMenuInflater().inflate(R.menu.land_menu, menu);
        }

        return super.onCreateOptionsMenu(menu);
    } //b

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { //b
        switch (item.getItemId()) {
            case R.id.btnFloat:

                if(player!=null) {
                    player.pause();
                    elapsedTime = player.getCurrentTimeMillis();
                }
                showpopup();
                return true;
            default:
                break;
        }
        return true;
    } //b

    public String getYoutubeId() {
        return youtubeId;
    }

    @Override
    public void onBackPressed() {
        Dialog deleteConfirmBox = new android.support.v7.app.AlertDialog.Builder(GuestActivity.this)
                .setMessage("Leave current notebook?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
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
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("No", null)
                .create();
        deleteConfirmBox.show();
    }
}