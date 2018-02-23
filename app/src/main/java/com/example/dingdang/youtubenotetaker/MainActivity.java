package com.example.dingdang.youtubenotetaker;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button guestLogin, login;

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
                startActivity(intent);
            }
        });
    }
}
