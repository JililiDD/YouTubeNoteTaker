package com.example.dingdang.youtubenotetaker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class AfterLoginActivity extends AppCompatActivity {

    Button btnOpenNotebook, btnMyNotebooks;
    private static final int RC_SIGN_IN = 1;

    // Firebase variable
    private FirebaseAuth.AuthStateListener mAuthStateListener;

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
                Intent intent = new Intent(getApplicationContext(), UserNoteBooks.class);
                intent.putExtra("USER_TYPE", "REGISTERED");
                startActivity(intent);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.sign_out_menu :
                //sign out
                AuthUI.getInstance().signOut(this);
                return true;
            default :

                return super.onOptionsItemSelected(item);
        }
    }
}
