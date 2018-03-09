package com.example.dingdang.youtubenotetaker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AfterLoginActivity extends AppCompatActivity {

    Button btnOpenNotebook, btnMyNotebooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_login);

        btnMyNotebooks = (Button) findViewById(R.id.btnMyNotebooks);
        btnOpenNotebook = (Button) findViewById(R.id.btnOpenNotebook);

        // Lead the user to the activity for taking notes.
        btnOpenNotebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GuestActivity.class);
                intent.putExtra("USER_TYPE", "REGISTERED");
                startActivity(intent);
            }
        });


        btnMyNotebooks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Fetch users notebooks from Firebase and display them in UserNotebooksActivity

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference();
            }
        });

    }
}
