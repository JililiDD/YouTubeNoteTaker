package com.example.dingdang.youtubenotetaker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class AddNotebookNameActivity extends AppCompatActivity {

    private EditText etNotebookNameInput;
    private Button btnOpen, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notebook_name);

        etNotebookNameInput = findViewById(R.id.etNotebookNameInput);
        btnOpen = findViewById(R.id.btnOpen);
        btnCancel = findViewById(R.id.btnCancel);

        btnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}
