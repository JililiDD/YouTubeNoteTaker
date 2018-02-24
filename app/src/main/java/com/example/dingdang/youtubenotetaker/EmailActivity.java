package com.example.dingdang.youtubenotetaker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

// Email function is referred to from: https://www.youtube.com/watch?v=x9VkNlB0Z6Y
public class EmailActivity extends AppCompatActivity {
    private EditText to, subject, message;
    private Button btnSendEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        to = (EditText)findViewById(R.id.etTo);
        subject = (EditText)findViewById(R.id.etEmailSubject);
        message = (EditText)findViewById(R.id.etEmailMessage);
        btnSendEmail = (Button) findViewById(R.id.btnSendEmail);

        message.setText(getIntent().getStringExtra("MESSAGE")); // get message from the intent

        btnSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String TO = to.getText().toString();
                String SUBJECT = subject.getText().toString();
                String MESSAGE = message.getText().toString();

                // Evoke a third-party app for sending the email
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plaint/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] {TO});
                intent.putExtra(Intent.EXTRA_SUBJECT, SUBJECT);
                intent.putExtra(Intent.EXTRA_TEXT, MESSAGE);

                startActivity(Intent.createChooser(intent, "Select an app to send email"));
            }
        });
    }
}
