package com.example.dingdang.youtubenotetaker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddNotebookNameActivity extends AppCompatActivity {

    private EditText etNotebookNameInput;
    private Button btnOpen, btnCancel;
    private FirebaseDatabase database;
    private FirebaseUser user;
    private DatabaseReference myRef;
    private DatabaseReference myReNoteBook;
    private String userType, youtubeId,useruid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notebook_name);

        etNotebookNameInput = findViewById(R.id.etNotebookNameInput);
        btnOpen = findViewById(R.id.btnOpen);
        btnCancel = findViewById(R.id.btnCancel);

        Intent theIntent = getIntent();
        userType=theIntent.getStringExtra("USER_TYPE");
        youtubeId=theIntent.getStringExtra("VIDEO_ID");
        database=FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        //get the current userId
        useruid=user.getUid();
        myReNoteBook = database.getReference("notebook").child(useruid);

        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isRegisteredUser()){
                    String notebookName=etNotebookNameInput.getText().toString();
                    if (notebookName.equals("")){
                        Toast.makeText(AddNotebookNameActivity.this, "Notebook name cannot be empty!",Toast.LENGTH_SHORT).show();
                    } else {
                        myReNoteBook.child(youtubeId).setValue(notebookName);
                        Intent intent = new Intent(getApplicationContext(), CoreNotebookActivity.class);
                        intent.putExtra("VIDEO_ID", youtubeId);
                        intent.putExtra("USER_TYPE", userType);
                        startActivity(intent);
                    }
                }


            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AfterLoginActivity.class);
                intent.putExtra("VIDEO_ID", youtubeId);
                intent.putExtra("USER_TYPE", userType);
                startActivity(intent);

            }
        });
    }
    public boolean isRegisteredUser(){
        return userType.equals("REGISTERED");
    }
}
