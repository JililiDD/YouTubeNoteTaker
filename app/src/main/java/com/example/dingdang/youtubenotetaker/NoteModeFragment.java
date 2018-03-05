package com.example.dingdang.youtubenotetaker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
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


import java.util.ArrayList;
import java.util.List;


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
    private String userType;

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
        etEditNote = (EditText) view.findViewById(R.id.EditNoteUsrNoteInput);;

        noteList = new ArrayList<>();

        // Get userType from current GuestActivity
        GuestActivity guestActivity = (GuestActivity) getActivity();
        userType = guestActivity.getUserType();

        // Hide rlNotepad and rlEditNote UIs by default
        rlNotepad.setVisibility(View.GONE);
        rlEditNote.setVisibility(View.GONE);


        if(isRegisteredUser()){
            Toast.makeText(getContext(),"REGISTERED USER",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getContext(),"GUEST",Toast.LENGTH_SHORT).show();
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
                lvNotesItemAdapter.add(noteItem);
                lvNotes.setAdapter(lvNotesItemAdapter);

                llNoteList.setVisibility(View.VISIBLE);  // Display llNoteList UI
                rlNotepad.setVisibility(View.GONE); // Hide rlNotepad UI
                rlEditNote.setVisibility(View.GONE); // Hide rlEditNote UI

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

        btnEditDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Delete the selected note item
                NoteItem selectedNoteItem = getSelectedNote();
                noteList.remove(selectedNoteItem);

                // Update the ListView
                ArrayAdapter<NoteItem> lvNotesItemAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(), R.layout.item_black, noteList);
                lvNotes.setAdapter(lvNotesItemAdapter);

                llNoteList.setVisibility(View.VISIBLE);  // Display llNoteList UI
                rlNotepad.setVisibility(View.GONE); // Hide rlNotepad UI
                rlEditNote.setVisibility(View.GONE); // Hide rlEditNote UI
            }
        });



        lvNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                rlEditNote.setVisibility(View.VISIBLE);
                llNoteList.setVisibility(View.GONE);
                rlNotepad.setVisibility(View.GONE);

                // Get selected NoteItem object
                setSelectedNote(noteList.get(pos));

                // Set the text fields to the NoteItem object's corresponding values in edit UI
                tvEditNoteTime.setText(selectedNote.getTime());
                etEditSubject.setText(selectedNote.getSubject());
                etEditNote.setText(selectedNote.getNote());
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
