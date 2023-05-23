package com.example.myfirebasetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddStudentGroupActivity extends AppCompatActivity {

    private TextInputLayout enterTitle;


    private FirebaseFirestore db;

    Button buttonAddStGr;

    String type;


    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();

    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student_group);
        FirebaseApp.initializeApp(this);
        enterTitle=(TextInputLayout)findViewById(R.id.titleEt);
        buttonAddStGr=(Button)findViewById(R.id.buttonAddStGr);
        db= FirebaseFirestore.getInstance();
        //buttonAddStGr.setOnClickListener(addStudentGroup);

        buttonAddStGr.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                addStudentGroup();
            }
        });

    }

    private boolean verifyTitle() {


        String t=enterTitle.getEditText().getText().toString().trim();
        if(t.isEmpty())
        {   enterTitle.setErrorEnabled(true);
            enterTitle.setError("Reikalingas pavadinimas");
            return true;
        }
        else
        {
            enterTitle.setErrorEnabled(false);
            return false;
        }
    }







    private void addStudentGroup() {
        final String title=enterTitle.getEditText().getText().toString().trim();

        Map<String, Object> data = new HashMap<>();
        data.put("form", title);

        db.collection("StudentGroup")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("bbbb", "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("aaaaa", "Error adding document", e);
                    }
                });
    }
}