package com.example.dingdang.youtubenotetaker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button guestLogin, login;
    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        guestLogin = (Button) findViewById(R.id.btnGuestLogin);
        login = (Button) findViewById(R.id.btnLogin);

        guestLogin.setOnClickListener(new View.OnClickListener() { // Navigate to GuestActivity
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GuestActivity.class);
                intent.putExtra("USER_TYPE", "GUEST");
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FirebaseLoginActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onActivityResult( int requestCode, int resultCode, Intent data) {
        if(requestCode == RC_SIGN_IN) {
            if(resultCode == RESULT_OK) {
                Toast.makeText(MainActivity.this, "Signed In !!",Toast.LENGTH_SHORT).show();
            }
            else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(MainActivity.this, "TOTTAL CANCELL !!",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
