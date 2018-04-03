
package com.example.dingdang.youtubenotetaker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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
public class Main_Search extends AppCompatActivity {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    // TODO: Rename and change types of parameters

    private FirebaseDatabase database;

    private DatabaseReference myReNoteBook;

    // Components needed for youtube search
    // Refered from https://code.tutsplus.com/tutorials/create-a-youtube-client-on-android--cms-22858
    private EditText searchInput;
    private Button searchBtn;
    private ListView videosFound;
    private Handler handler;
    private List<VideoItem> searchResults;
    private String userType;
    private FirebaseUser user;
    private String useruid;



//    private OnFragmentInteractionListener mListener;

//    public VideoSearchFragment() {
//        // Required empty public constructor
//    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     //     * @param param1 Parameter 1.
     //     * @param param2 Parameter 2.
     * @return A new instance of fragment VideoSearchFragment.
     */
    // TODO: Rename and change types and number of parameters
//    public static VideoSearchFragment newInstance(String param1, String param2) {
//        VideoSearchFragment fragment = new VideoSearchFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__search);
        userType = getIntent().getStringExtra("USER_TYPE"); // Get user type to provide different functions to guest and member users
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);

//        }
//        View view = inflater.inflate(R.layout.activity_main__search, container, false);
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
//                    return false;
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

//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        //return inflater.inflate(R.layout.fragment_video_search, container, false);
//        View view = inflater.inflate(R.layout.activity_main__search, container, false);
//
//        // For youtube search
//        // Refered from https://code.tutsplus.com/tutorials/create-a-youtube-client-on-an View view = inflater.inflate(R.layout.activity_main__search, container, false);droid--cms-22858
//        searchInput = (EditText)view.findViewById(R.id.search_input);
//        videosFound = (ListView)view.findViewById(R.id.videos_found);
//
//        handler = new Handler();
//
//        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if(actionId == EditorInfo.IME_ACTION_DONE){
//                    searchOnYoutube(v.getText().toString());
//                    return false;
//                }
//                return true;
//            }
//        });
//        addClickListener();
//
//        return view;
//    }

    private void searchOnYoutube(final String keywords){
        new Thread(){
            public void run(){
        YoutubeConnector yc = new YoutubeConnector(Main_Search.this);
        searchResults = yc.search(keywords);
//        updateVideosFound();

                handler.post(new Runnable(){
                    public void run(){
                        updateVideosFound();
                    }
                });
            }
        }.start();
    }

    // Following youtube search functionality was referred from:
    // https://code.tutsplus.com/tutorials/create-a-youtube-client-on-an View view = inflater.inflate(R.layout.activity_main__search, container, false);droid--cms-22858
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

    // TODO: Rename method, update argument and hook method into UI event
//    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
//        }
//    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

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
//    public interface OnFragmentInteractionListener {
//        // TODO: Update argument type and name
//        void onFragmentInteraction(Uri uri);
//    }
    public boolean isRegisteredUser(){
        return userType.equals("REGISTERED");
    }
}



