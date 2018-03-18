package com.example.dingdang.youtubenotetaker;

import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPageActivity extends AppCompatActivity {
    private FirebaseAuth signInAuth;
    private EditText userEmail, userPassword;
    private TextView messageSign;
    private Button signInClick;
    private ImageView userImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);


        signInAuth = FirebaseAuth.getInstance();

        userEmail = findViewById(R.id.userEmailSignIn);
        userPassword = findViewById(R.id.thePassword);
        messageSign = findViewById(R.id.messageSign);
        signInClick = findViewById(R.id.signInBtn);
        userImage=findViewById(R.id.userImage);

        signInClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInEmail();

            }
        });

    }

    public void signInEmail() {
        String userName = userEmail.getText().toString();
        String password = userPassword.getText().toString();

        signInAuth.signInWithEmailAndPassword(userName, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    messageSign.setText("success");
                } else {
                    messageSign.setText("failed");
                }

            }
        });

    }
}
