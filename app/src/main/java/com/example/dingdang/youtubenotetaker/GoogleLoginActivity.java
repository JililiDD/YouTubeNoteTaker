package com.example.dingdang.youtubenotetaker;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GoogleLoginActivity extends AppCompatActivity implements View.OnClickListener{
    private FirebaseAuth signInAuth;
    private EditText userEmailSignIn, thePassword,detail,status;
    private TextView messageSign;
    private Button signInBtn,signout,createaccount;
    private ImageView userImage;
    private static final String TAG = "EmailPassword";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);


        signInAuth = FirebaseAuth.getInstance();

        userEmailSignIn = findViewById(R.id.userEmailSignIn);
        thePassword = findViewById(R.id.thePassword);
        detail = findViewById(R.id.detail);
        status = findViewById(R.id.status);


        signInBtn = findViewById(R.id.signInBtn);
        signout = findViewById(R.id.signout);
        createaccount = findViewById(R.id.createaccount);



        messageSign = findViewById(R.id.messageSign);

        signInBtn.setOnClickListener(this);
        signout.setOnClickListener(this);
        createaccount.setOnClickListener(this);


        //mAuth = FirebaseAuth.getInstance();





    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.createaccount) {
            createAccount(userEmailSignIn.getText().toString(), thePassword.getText().toString());


        } else if (i == R.id.signInBtn) {
            signIn(userEmailSignIn.getText().toString(), thePassword.getText().toString());
            Intent intent = new Intent(GoogleLoginActivity.this, AfterLoginActivity.class);
            intent.putExtra("USER_TYPE", "REGISTERED");
            startActivity(intent);
        } else if (i == R.id.signout) {
           // signOut();
        }

    }


    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }
        // [START sign_in_with_email]
        signInAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = signInAuth.getCurrentUser();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(GoogleLoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            status.setText("auth_failed");
                        }
                        else{
                            status.setText("auth_successful");
                        }

                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = userEmailSignIn.getText().toString();
        if (TextUtils.isEmpty(email)) {
            userEmailSignIn.setError("Required.");
            valid = false;
        } else {
            userEmailSignIn.setError(null);
        }

        String password = thePassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            thePassword.setError("Required.");
            valid = false;
        } else {
            thePassword.setError(null);
        }

        return valid;
    }

    private void createAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        // [START create_user_with_email]
        signInAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = signInAuth.getCurrentUser();
                           // updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(GoogleLoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                           // updateUI(null);
                        }

                        // [START_EXCLUDE]
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }





}
