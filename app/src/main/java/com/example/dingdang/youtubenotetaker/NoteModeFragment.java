package com.example.dingdang.youtubenotetaker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NoteModeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NoteModeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoteModeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    // Components
    private Button btnEmail, btnTakeNote, btnSave, btnReplay, btnCancel, btnEditNoteCancel, btnEditNoteSave, btnEditDelete;
    private ListView lvNotes;
    private ArrayAdapter<NoteItem> lvNotesItemAdapter;
    private LinearLayout llNoteList;
    private RelativeLayout rlNotepad, rlEditNote;
    private EditText etUserNoteInput, etUserSubjectInput, etEditSubject, etEditNote;
    private TextView tvTimeAtPause, tvEditNoteTime;
    private long elapsedTime = 0;
    private List<NoteItem> noteList;
    private NoteItem selectedNote;
    private String userType, youtubeId;
    //firebase user get
    private FirebaseUser user;
    private String useruid;
    private ValueEventListener theFireListener;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private Boolean deleteChecker,listChecker;
    private int finalcount,finalcountdelete,finalcountlist;



    public NoteModeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NoteModeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NoteModeFragment newInstance(String param1, String param2) {
        NoteModeFragment fragment = new NoteModeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_mode, container, false);
        btnEmail = (Button) view.findViewById(R.id.btnEmailNote);
        btnTakeNote = (Button) view.findViewById(R.id.btnTakeNote);
        btnSave = (Button) view.findViewById(R.id.btnSave);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        btnReplay = (Button) view.findViewById(R.id.btnEditNoteReplay);
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
        deleteChecker=false;
        listChecker=false;
        finalcount=0;
        finalcountdelete=0;
        finalcountlist=0;


        database = FirebaseDatabase.getInstance();






        noteList = new ArrayList<>();

        // Get userType and youtube video ID from current GuestActivity
        GuestActivity guestActivity = (GuestActivity) getActivity();
        userType = guestActivity.getUserType();
        //linkExist=guestActivity.


        //the youtubeID
        youtubeId = guestActivity.getYoutubeId(); //YIWEI
        Toast.makeText(getContext(),youtubeId,Toast.LENGTH_SHORT).show(); // YIWEI

        // Hide rlNotepad and rlEditNote UIs by default
        rlNotepad.setVisibility(View.GONE);
        rlEditNote.setVisibility(View.GONE);



        if(isRegisteredUser()){
            user = FirebaseAuth.getInstance().getCurrentUser();
            //get the current userId
            useruid=user.getUid();
            myRef = database.getReference("user").child(useruid);
            Toast.makeText(getContext(),"REGISTERED USER",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getContext(),"GUEST",Toast.LENGTH_SHORT).show();
        }
        int main=0;
        if(isRegisteredUser()){
            main++;
            user = FirebaseAuth.getInstance().getCurrentUser();
            //get the current userId
            useruid=user.getUid();
            myRef = database.getReference("user").child(useruid);


            theFireListener=new ValueEventListener() {
                String theselectid ="";
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){
                        if (childDataSnapshot.getKey().toString().equals(youtubeId)){
                            final DatabaseReference myChildrenRef = myRef.child(youtubeId);

                            myChildrenRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    noteList.clear();
                                    ArrayAdapter<NoteItem> lvNotesItemAdapter1 = new ArrayAdapter<>(getActivity().getApplicationContext(),
                                            R.layout.item_black, noteList);

                                    int i=0;
                                    int k=0;
                                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {

                                        HashMap<String, String> newItem = (HashMap<String, String>) childDataSnapshot.getValue();
                                        if (newItem.get("Selected").equals("true")){
                                            k++;
                                            theselectid=newItem.get("NoteId");
                                            DatabaseReference myRefr = myRef.child(youtubeId).child(theselectid);
                                            finalcount++;
                                            Log.i("FINALCOUNT",""+finalcount);
                                            myRefr.removeValue();
                                            break;


                                        }

                                    }

                                    for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                        HashMap<String, String> newItem = (HashMap<String, String>) childDataSnapshot.getValue();
                                        if (newItem.get("Selected").equals("false")) {
                                            NoteItem newNoteItem = new NoteItem(0, newItem.get("Time"), newItem.get("Subject"), newItem.get("Note"));
                                            newNoteItem.setNoteId(newItem.get("NoteId"));
                                            newNoteItem.setNotebookName(newItem.get("NotebookName"));
                                            lvNotesItemAdapter1.add(newNoteItem);

                                        }

                                    }


                                    lvNotes.setAdapter(lvNotesItemAdapter1);
                                    llNoteList.setVisibility(View.VISIBLE);  // Display llNoteList UI
                                    rlNotepad.setVisibility(View.GONE); // Hide rlNotepad UI
                                    rlEditNote.setVisibility(View.GONE); // Hide rlEditNote UIi=0;



                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            break;

                        }

                    }



                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            };


            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){
                        if(childDataSnapshot.getKey().toString().equals(youtubeId)){
                            noteList.clear();
                            DatabaseReference myChildrenRef = myRef.child(youtubeId);
                            myChildrenRef.addValueEventListener(new ValueEventListener() {

                                ArrayAdapter<NoteItem> lvNotesItemAdapter1 = new ArrayAdapter<>(getActivity().getApplicationContext(),
                                        R.layout.item_black, noteList);
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for(DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){
                                        HashMap<String,String> newItem= (HashMap<String, String>) childDataSnapshot.getValue();

                                        NoteItem newNoteItem=new NoteItem(0,newItem.get("Time"),newItem.get("Subject"),newItem.get("Note"));
                                        newNoteItem.setNoteId(newItem.get("NoteId"));
                                        newNoteItem.setNotebookName(newItem.get("NotebookName"));
                                        newNoteItem.setSelected("false");
                                        lvNotesItemAdapter1.add(newNoteItem);
                                        lvNotes.clearChoices();
                                        lvNotes.setAdapter(lvNotesItemAdapter1);
                                        llNoteList.setVisibility(View.VISIBLE);

                                    }

                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                        break;

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }



        /** ListView UI buttons*/
        btnEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(noteList.isEmpty()){
                    Toast.makeText(getContext(),"No notes taken",Toast.LENGTH_SHORT).show();
                }
                else{
                    // Create email message content from notes taken
                    StringBuilder emailContent = new StringBuilder();
                    for(NoteItem notes : noteList){
                        emailContent.append(notes.toEmailFormat());
                    }

                    // Create intent to evoke EmailActivity and pass the message in an Extra
                    Intent intent = new Intent(getActivity().getApplicationContext(), EmailActivity.class);
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

                // Referred from: http://blog.csdn.net/fengge34/article/details/46391453
                mListener.onFragmentInteraction(Uri.parse("pause")); // Pass to GuestActivity to pause the video

                // Take elapsed time (milliseconds) when pause from GuestActivity using a getter method in GuestActivity
                // Using Bundle to pass data from GuestActivity to NoteModeFragment doesn't work in this case
                // Referenced from: https://stackoverflow.com/a/22065903
                GuestActivity guestActivity = (GuestActivity) getActivity();
                elapsedTime = guestActivity.getElapsedTime();

                // Convert elapsedTime to hh:mm:ss format
                int hour = (int) (elapsedTime/(1000*3600));
                int min = (int) (elapsedTime/60000) % 60;
                int second = (int) (elapsedTime/1000) % 60;
                final String elapsedTimeString = String.format("%02d:%02d:%02d", hour, min, second);

                tvTimeAtPause.setText(elapsedTimeString);
            }
        });

        /** Note taking UI buttons*/
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an ArrayAdapter<NoteItem> for storing NoteItem objects
                ArrayAdapter<NoteItem> lvNotesItemAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(),
                        R.layout.item_black, noteList);
                NoteItem noteItem = new NoteItem(elapsedTime, tvTimeAtPause.getText().toString(), etUserSubjectInput.getText().toString(), etUserNoteInput.getText().toString());

                if(isRegisteredUser()){
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference("user").child(useruid).child(youtubeId);
                    String theNoteId =myRef.push().getKey();
                    noteItem.setNoteId(theNoteId);
                    Map<String,NoteItem> theData=noteItem.putInToFireBase();

                    //Map<String, String> userData=tempItem.putInToFireBase();
                    myRef.child(theNoteId).setValue(theData);

                    DatabaseReference myRef1 = database.getReference("user").child(useruid);
                    myRef1.addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){
                                if(childDataSnapshot.getKey().toString().equals(youtubeId)){
                                    FirebaseDatabase database1 = FirebaseDatabase.getInstance();

                                    DatabaseReference myChildrenRef = database1.getReference("user").child(useruid).child(youtubeId);
                                    myChildrenRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            noteList.clear();
                                            ArrayAdapter<NoteItem> lvNotesItemAdapter1 = new ArrayAdapter<>(getActivity().getApplicationContext(),
                                                    R.layout.item_black, noteList);

                                            for(DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){
                                                HashMap<String,String> newItem= (HashMap<String, String>) childDataSnapshot.getValue();

                                                NoteItem newNoteItem=new NoteItem(0,newItem.get("Time"),newItem.get("Subject"),newItem.get("Note"));
                                                newNoteItem.setNoteId(newItem.get("NoteId"));
                                                newNoteItem.setNotebookName(newItem.get("NotebookName"));
                                                newNoteItem.setSelected("false");
                                                lvNotesItemAdapter1.add(newNoteItem);
                                            }
                                            lvNotes.setAdapter(lvNotesItemAdapter1);
                                            llNoteList.setVisibility(View.VISIBLE);  // Display llNoteList UI
                                            rlNotepad.setVisibility(View.GONE); // Hide rlNotepad UI
                                            rlEditNote.setVisibility(View.GONE); // Hide rlEditNote UIi=0;


                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    break;

                                }

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }else{
                    lvNotesItemAdapter.add(noteItem);
                    lvNotes.setAdapter(lvNotesItemAdapter);

                    llNoteList.setVisibility(View.VISIBLE);  // Display llNoteList UI
                    rlNotepad.setVisibility(View.GONE); // Hide rlNotepad UI
                    rlEditNote.setVisibility(View.GONE); // Hide rlEditNote UI
                }





                /**lvNotesItemAdapter.add(noteItem);
                lvNotes.setAdapter(lvNotesItemAdapter);

                llNoteList.setVisibility(View.VISIBLE);  // Display llNoteList UI
                rlNotepad.setVisibility(View.GONE); // Hide rlNotepad UI
                rlEditNote.setVisibility(View.GONE); // Hide rlEditNote UI*/

                // Clear user inputs
                etUserNoteInput.getText().clear();
                etUserSubjectInput.getText().clear();
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
            }
        });

        /** Note editing UI buttons*/
        btnEditNoteSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getSelectedNote().setSubject(etEditSubject.getText().toString());
                getSelectedNote().setNote(etEditNote.getText().toString());

                // Update the ListView
                ArrayAdapter<NoteItem> lvNotesItemAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.item_black, noteList);
                lvNotes.setAdapter(lvNotesItemAdapter);

                llNoteList.setVisibility(View.VISIBLE);  // Display llNoteList UI
                rlNotepad.setVisibility(View.GONE); // Hide rlNotepad UI
                rlEditNote.setVisibility(View.GONE); // Hide rlEditNote UI
            }
        });

        btnEditNoteCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                llNoteList.setVisibility(View.VISIBLE);  // Display llNoteList UI
                rlNotepad.setVisibility(View.GONE); // Hide rlNotepad UI
                rlEditNote.setVisibility(View.GONE); // Hide rlEditNote UI
            }
        });

        btnReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String parseString = "replay " + Long.toString(getSelectedNote().getCurrentTime()); // Pass the note time to GuestActivity as well
                // Referred from: http://blog.csdn.net/fengge34/article/details/46391453
                mListener.onFragmentInteraction(Uri.parse(parseString)); // Pass to GuestActivity to replay the video at the note time

            }
        });
        btnEditDelete.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN){
                    finalcountdelete++;
                    Log.i("deletecount"," "+finalcountdelete);
                    myRef.addValueEventListener(theFireListener);
                }else if (event.getAction()==MotionEvent.ACTION_UP){
                    myRef.removeEventListener(theFireListener);

                }
                return false;
            }
        });

       /** btnEditDelete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (isRegisteredUser()) {
                    Log.i("delete btn info","  "+deleteChecker.toString());
                    myRef.addValueEventListener(theFireListener);
                    myRef.removeEventListener(theFireListener);

                    deleteChecker=true;


                    //deleteChecker=false;
                   //myRef.addValueEventListener(theFireListener);
                    /**ValueEventListener thelistener=new ValueEventListener() {
                        String theselectid ="";
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){
                                if (childDataSnapshot.getKey().toString().equals(youtubeId)){
                                    FirebaseDatabase database1 = FirebaseDatabase.getInstance();
                                    final DatabaseReference myChildrenRef = database1.getReference("user").child(useruid).child(youtubeId);

                                    myChildrenRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                            noteList.clear();
                                            ArrayAdapter<NoteItem> lvNotesItemAdapter1 = new ArrayAdapter<>(getActivity().getApplicationContext(),
                                                    R.layout.item_black, noteList);

                                            int i=0;
                                            int k=0;
                                            for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {

                                                HashMap<String, String> newItem = (HashMap<String, String>) childDataSnapshot.getValue();
                                                if (newItem.get("Selected").equals("true")){
                                                    k++;
                                                    Log.i("count true","  "+k);
                                                    theselectid=newItem.get("NoteId");
                                                    FirebaseDatabase databaser = FirebaseDatabase.getInstance();
                                                    DatabaseReference myRefr = databaser.getReference("user").child(useruid).child(youtubeId).child(theselectid);
                                                    Log.i("selectedinfo inside",""+myRefr.toString()); //displays the key for the node
                                                    myRefr.removeValue();
                                                    break;


                                                }

                                            }

                                            for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                                HashMap<String, String> newItem = (HashMap<String, String>) childDataSnapshot.getValue();
                                                if (newItem.get("Selected").equals("false")) {
                                                    NoteItem newNoteItem = new NoteItem(0, newItem.get("Time"), newItem.get("Subject"), newItem.get("Note"));
                                                    newNoteItem.setNoteId(newItem.get("NoteId"));
                                                    newNoteItem.setNotebookName(newItem.get("NotebookName"));
                                                    lvNotesItemAdapter1.add(newNoteItem);
                                                    i++;
                                                    Log.i("count false","  "+i);

                                                }

                                            }


                                            lvNotes.setAdapter(lvNotesItemAdapter1);
                                            llNoteList.setVisibility(View.VISIBLE);  // Display llNoteList UI
                                            rlNotepad.setVisibility(View.GONE); // Hide rlNotepad UI
                                            rlEditNote.setVisibility(View.GONE); // Hide rlEditNote UIi=0;



                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    break;

                                }

                            }



                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };*/


                    /**ValueEventListener thesecondlistener=new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            noteList.clear();
                            ArrayAdapter<NoteItem> lvNotesItemAdapter1 = new ArrayAdapter<>(getActivity().getApplicationContext(),
                                    R.layout.item_black, noteList);

                            int i=0;
                            int k=0;
                            for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                HashMap<String, String> newItem = (HashMap<String, String>) childDataSnapshot.getValue();
                                if (newItem.get("Selected").equals("true")){
                                    k++;
                                    Log.i("count true","  "+k);
                                    String theselectid=newItem.get("NoteId");
                                    FirebaseDatabase databaser = FirebaseDatabase.getInstance();
                                    DatabaseReference myRefr = databaser.getReference("user").child(useruid).child(youtubeId).child(theselectid);
                                    Log.i("selectedinfo inside",""+myRefr.toString()); //displays the key for the node
                                    myRefr.removeValue();
                                    break;


                                }

                            }

                            for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                HashMap<String, String> newItem = (HashMap<String, String>) childDataSnapshot.getValue();
                                if (newItem.get("Selected").equals("false")) {
                                    NoteItem newNoteItem = new NoteItem(0, newItem.get("Time"), newItem.get("Subject"), newItem.get("Note"));
                                    newNoteItem.setNoteId(newItem.get("NoteId"));
                                    newNoteItem.setNotebookName(newItem.get("NotebookName"));
                                    lvNotesItemAdapter1.add(newNoteItem);
                                    i++;
                                    Log.i("count false","  "+i);

                                }

                            }


                            lvNotes.setAdapter(lvNotesItemAdapter1);
                            llNoteList.setVisibility(View.VISIBLE);  // Display llNoteList UI
                            rlNotepad.setVisibility(View.GONE); // Hide rlNotepad UI
                            rlEditNote.setVisibility(View.GONE); // Hide rlEditNote UIi=0;



                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    };*/
                   // myRef1.addValueEventListener(thelistener);
                    //myRef1.removeEventListener(thelistener);





                    /**myRef1.addValueEventListener(new ValueEventListener() {
                        String theselectid ="";
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                int i = 0;
                                if (childDataSnapshot.getKey().toString().equals(youtubeId)) {
                                    FirebaseDatabase database1 = FirebaseDatabase.getInstance();

                                    final DatabaseReference myChildrenRef = database1.getReference("user").child(useruid).child(youtubeId);


                                    myChildrenRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            noteList.clear();
                                            ArrayAdapter<NoteItem> lvNotesItemAdapter1 = new ArrayAdapter<>(getActivity().getApplicationContext(),
                                                    R.layout.item_black, noteList);

                                            int i=0;
                                            int k=0;
                                            for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                                HashMap<String, String> newItem = (HashMap<String, String>) childDataSnapshot.getValue();
                                                if (newItem.get("Selected").equals("true")){
                                                    k++;
                                                    Log.i("count true","  "+k);
                                                    theselectid=newItem.get("NoteId");
                                                    FirebaseDatabase databaser = FirebaseDatabase.getInstance();
                                                    DatabaseReference myRefr = databaser.getReference("user").child(useruid).child(youtubeId).child(theselectid);
                                                    Log.i("selectedinfo inside",""+myRefr.toString()); //displays the key for the node
                                                    myRefr.removeValue();
                                                    break;


                                                }

                                            }

                                            for (DataSnapshot childDataSnapshot : dataSnapshot.getChildren()) {
                                                HashMap<String, String> newItem = (HashMap<String, String>) childDataSnapshot.getValue();
                                                if (newItem.get("Selected").equals("false")) {
                                                    NoteItem newNoteItem = new NoteItem(0, newItem.get("Time"), newItem.get("Subject"), newItem.get("Note"));
                                                    newNoteItem.setNoteId(newItem.get("NoteId"));
                                                    newNoteItem.setNotebookName(newItem.get("NotebookName"));
                                                    lvNotesItemAdapter1.add(newNoteItem);
                                                    i++;
                                                    Log.i("count false","  "+i);

                                                }

                                            }


                                            lvNotes.setAdapter(lvNotesItemAdapter1);
                                            llNoteList.setVisibility(View.VISIBLE);  // Display llNoteList UI
                                            rlNotepad.setVisibility(View.GONE); // Hide rlNotepad UI
                                            rlEditNote.setVisibility(View.GONE); // Hide rlEditNote UIi=0;



                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    break;

                                }

                            }
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }


                    });



                } else {
                    // Delete the selected note item
                    NoteItem selectedNoteItem = getSelectedNote();
                    String removeItemNoteId = selectedNoteItem.getNoteId();
                    noteList.remove(selectedNoteItem);
                    ArrayAdapter<NoteItem> lvNotesItemAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.item_black, noteList);
                    lvNotes.setAdapter(lvNotesItemAdapter);

                    llNoteList.setVisibility(View.VISIBLE);  // Display llNoteList UI
                    rlNotepad.setVisibility(View.GONE); // Hide rlNotepad UI
                    rlEditNote.setVisibility(View.GONE); // Hide rlEditNote UI
                }
               // FirebaseDatabase.getInstance().goOffline();

            }

        });*/

        lvNotes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_UP){
                    myRef.removeEventListener(theFireListener);
                }
                return false;
            }
        });





        lvNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int pos, long l) {
                finalcountlist++;
                Log.i("listcount"," "+finalcountlist);
                myRef.removeEventListener(theFireListener);
                /**rlEditNote.setVisibility(View.VISIBLE);
                llNoteList.setVisibility(View.GONE);
                rlNotepad.setVisibility(View.GONE);*/



                if(isRegisteredUser()){
                    //remove the noteItem from the database
                    myRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for(final DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){
                                if(childDataSnapshot.getKey().toString().equals(youtubeId)){

                                    DatabaseReference myChildrenRef = myRef.child(youtubeId);
                                    myChildrenRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            noteList.clear();
                                            ArrayAdapter<NoteItem> lvNotesItemAdapter1 = new ArrayAdapter<>(getActivity().getApplicationContext(),
                                                    R.layout.item_black, noteList);
                                            for(DataSnapshot childDataSnapshot : dataSnapshot.getChildren()){
                                                HashMap<String,String> newItem= (HashMap<String, String>) childDataSnapshot.getValue();
                                                NoteItem newNoteItem=new NoteItem(0,newItem.get("Time"),newItem.get("Subject"),newItem.get("Note"));
                                                newNoteItem.setNoteId(newItem.get("NoteId"));
                                                newNoteItem.setNotebookName(newItem.get("NotebookName"));
                                                noteList.add(newNoteItem);
                                            }

                                                   }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }

                                    });
                                  break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    setSelectedNote(noteList.get(pos));
                    NoteItem seItem=noteList.get(pos);
                    FirebaseDatabase database2 = FirebaseDatabase.getInstance();
                    lvNotes.clearChoices();
                    myRef.removeEventListener(theFireListener);

                    DatabaseReference myChildrenRef1 =myRef.child(youtubeId).child(seItem.getNoteId());
                    myChildrenRef1.child("Selected").setValue("true");


                    rlEditNote.setVisibility(View.VISIBLE);
                    llNoteList.setVisibility(View.GONE);
                    rlNotepad.setVisibility(View.GONE);
                    tvEditNoteTime.setText(selectedNote.getTime());
                    etEditSubject.setText(selectedNote.getSubject());
                    etEditNote.setText(selectedNote.getNote());


                }else{
                     //Get selected NoteItem object
                    setSelectedNote(noteList.get(pos));

                     //Set the text fields to the NoteItem object's corresponding values in edit UI
                    tvEditNoteTime.setText(selectedNote.getTime());
                    etEditSubject.setText(selectedNote.getSubject());
                    etEditNote.setText(selectedNote.getNote());
                }
             }
        });


        // Inflate the layout for this fragment
        return view;
    }

    public NoteItem getSelectedNote() {
        return selectedNote;
    }

    public void setSelectedNote(NoteItem selectedNote) {
        this.selectedNote = selectedNote;
    }

    public boolean isRegisteredUser(){
        return userType.equals("REGISTERED");
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
