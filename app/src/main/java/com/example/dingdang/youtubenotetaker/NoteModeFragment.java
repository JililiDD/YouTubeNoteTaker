package com.example.dingdang.youtubenotetaker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubePlayer;


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
    private Button btnEmail, btnTakeNote, btnSave, btnReplay, btnCancel;
    private ListView lvNotes;
    private ArrayAdapter<String> lvNotesItemAdapter;
    private LinearLayout llNoteList;
    private RelativeLayout rlNotepad;
    private EditText etUserNoteInput, etUserSubjectInput;
    private TextView tvTimeAtPause;
    private long elapsedTime = 0;

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
        btnReplay = (Button) view.findViewById(R.id.btnReplay);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        lvNotes = (ListView) view.findViewById(R.id.lvNotes);
        lvNotesItemAdapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1);;
        llNoteList = (LinearLayout) view.findViewById(R.id.ll_noteList);
        rlNotepad = (RelativeLayout) view.findViewById(R.id.RL_notepad);
        etUserNoteInput = (EditText) view.findViewById(R.id.usrNoteInput);
        etUserSubjectInput = (EditText) view.findViewById(R.id.subject);
        tvTimeAtPause = (TextView) view.findViewById(R.id.elapsedTime);

        rlNotepad.setVisibility(View.GONE);



        btnTakeNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                llNoteList.setVisibility(View.GONE);
                rlNotepad.setVisibility(View.VISIBLE);


                /**
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
                 */
            }
        });



        // Inflate the layout for this fragment
        return view;
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
