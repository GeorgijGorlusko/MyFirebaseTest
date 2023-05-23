package com.example.myfirebasetest;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button logout;

    private Button addbook;

    private Button rembook;

    private Button updatebook;

    private Button searchbook;

    private Button addStGr;

    private Button issueBook;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addStGr = findViewById(R.id.addStGrButton);



        logout = findViewById(R.id.logoutButton);

        addbook = findViewById(R.id.addBookButton);

        rembook = findViewById(R.id.deleteBookButton);

        updatebook = findViewById(R.id.updateBookButton);

        searchbook = findViewById(R.id.searchBookButton);

        issueBook = findViewById(R.id.issueBookButton);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainActivity.this, "Atsijungta sekmingai!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this , StartActivity.class));
            }
        });

        addbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddBookActivity.class));
            }
        });

        rembook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RemoveBookActivity.class));
            }
        });

        updatebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, UpdateBookActivity.class));
            }
        });


        searchbook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SearchBookFormActivity.class));
            }
        });

        addStGr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddStudentGroupActivity.class));
            }
        });

        issueBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, IssueBookActivity.class));
            }
        });


    }
}