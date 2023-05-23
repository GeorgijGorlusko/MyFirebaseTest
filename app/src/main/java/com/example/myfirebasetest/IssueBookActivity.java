package com.example.myfirebasetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class IssueBookActivity extends AppCompatActivity {

    private TextInputLayout BookTitleSpinner;

    private TextInputLayout StudentGrSpinner;

    private TextInputLayout unitsEt;

    private Button paskrstytiButton;

    private boolean res1, res2;

    private FirebaseFirestore db;

    private StudentGroup S = new StudentGroup();

    private ProgressDialog p;

    private Book B = new Book();

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_book);
        FirebaseApp.initializeApp(this);

        p = new ProgressDialog(this);

        p = new ProgressDialog(IssueBookActivity.this);

        BookTitleSpinner = (TextInputLayout) findViewById(R.id.BookTitleSpinner);

        StudentGrSpinner = (TextInputLayout) findViewById(R.id.StudentGrSpinner);

        unitsEt = (TextInputLayout)findViewById(R.id.unitsEt);

        paskrstytiButton = (Button)findViewById(R.id.paskrstytiButton);

        db = FirebaseFirestore.getInstance();

        paskrstytiButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                issueBook();
            }
        });

    }


    private boolean verifyCard() {
        String t = StudentGrSpinner.getEditText().getText().toString().trim();
        if (t.isEmpty()) {
            StudentGrSpinner.setErrorEnabled(true);
            StudentGrSpinner.setError("Card No. Required");
            return true;
        } else {
            StudentGrSpinner.setErrorEnabled(false);
            return false;
        }
    }


    private boolean verifyBid() {
        String t = BookTitleSpinner.getEditText().getText().toString().trim();
        if (t.isEmpty()) {
            BookTitleSpinner.setErrorEnabled(true);
            BookTitleSpinner.setError("Book Id Required");
            return true;
        } else {
            BookTitleSpinner.setErrorEnabled(false);
            return false;
        }
    }

    private boolean getUser() {
        db.collection("User").whereEqualTo("card", Integer.parseInt(StudentGrSpinner.getEditText().getText().toString().trim())).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    if (task.getResult().size() == 1) {
                        res1 = true;
                        for (QueryDocumentSnapshot doc : task.getResult())
                            S = doc.toObject(StudentGroup.class);
                    } else {

                        res1 = false;
                        p.cancel();
                        Toast.makeText(IssueBookActivity.this, "No Such User !", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    res1 = false;
                    p.cancel();
                    Toast.makeText(IssueBookActivity.this, "Try Again !", Toast.LENGTH_SHORT).show();
                }


            }
        });

        return res1;
    }

    private boolean getBook() {

        db.document("Book/" + Integer.parseInt(BookTitleSpinner.getEditText().getText().toString().trim()) / 100).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        res2 = true;
                        B = task.getResult().toObject(Book.class);
                    } else {
                        res2 = false;
                        p.cancel();
                        Toast.makeText(IssueBookActivity.this, "No Such Book !", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    res2 = false;
                    p.cancel();
                    Toast.makeText(IssueBookActivity.this, "Try Again !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return res2;
    }

    private void issueBook() {

        Log.d("abc","invoked");
        if (verifyBid() | verifyCard()) {
            return;
        }
        p.setMessage("Please Wait !");
        p.show();
        if (getBook()&getUser())
        {


            if (B.getAvailable() == 0) {
                p.cancel();
                Toast.makeText(IssueBookActivity.this, "No Units of this Book Available !", Toast.LENGTH_SHORT).show();
                return;
            }
            if (B.getUnit().contains(Integer.parseInt(BookTitleSpinner.getEditText().getText().toString().trim()) % 100)) {
                p.cancel();
                Toast.makeText(IssueBookActivity.this, "This Unit is Already Issued !", Toast.LENGTH_SHORT).show();
                return;
            }
            List<Integer> l = new ArrayList<Integer>();
          //  l = S.getTitle();
            l.add(Integer.parseInt(BookTitleSpinner.getEditText().getText().toString().trim()));
           // S.setTitle(l);








            db.document("StudentGroup/" +S.getTitle()).set(S).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {

                        B.setAvailable(B.getAvailable()-1);
                        List<Integer> l1=new ArrayList<>();
                        l1=B.getUnit();
                        l1.add(Integer.parseInt(BookTitleSpinner.getEditText().getText().toString().trim()) % 100);
                        B.setUnit(l1);

                        db.document("Book/" + B.getId()).set(B).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    p.cancel();
                                    Toast.makeText(IssueBookActivity.this, "Book Issued Successfully !", Toast.LENGTH_SHORT).show();

                                } else {
                                    p.cancel();
                                    Toast.makeText(IssueBookActivity.this, "Try Again !", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        p.cancel();
                        Toast.makeText(IssueBookActivity.this, "Try Again !", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }



}


/*  db.collection("Books")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<String> spinnerDataList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            HashMap<String, Object> dbBooks = (HashMap<String, Object>) document.getData();
                            // Assuming your HashMap contains a "name" key, change it accordingly
                            String bookName = dbBooks.get("name").toString();
                            spinnerDataList.add(bookName);
                        }

                        BookTitleSpinner = (Spinner)findViewById(R.id.BookTitleSpinner);
                        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, spinnerDataList);
                        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        BookTitleSpinner.setAdapter(spinnerAdapter);
                    }
                });*/




      /*  db.collection("StudentGroup")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });*/

