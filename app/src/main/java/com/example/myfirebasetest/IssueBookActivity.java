package com.example.myfirebasetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class IssueBookActivity extends AppCompatActivity {

    private TextInputLayout BookTitleSpinner;

    private TextInputLayout StudentGrSpinner;

    private TextInputLayout unitsEt;

    private Button paskrstytiButton;

    private boolean res1, res2;
    List<DbBooks> dbBooks;
    List<DbStudentGroups> dbStudentGroups;

    private Spinner bookTitleSpinner;
    private Spinner groupTitleSpinner;
    private SeekBar seekBar;

    private FirebaseFirestore db;

   // private StudentGroup S = new StudentGroup();

   // private ProgressDialog p;

    //private Book B = new Book();

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
        db = FirebaseFirestore.getInstance();

        /*
        p = new ProgressDialog(this);

        p = new ProgressDialog(IssueBookActivity.this);

        BookTitleSpinner = (TextInputLayout) findViewById(R.id.BookTitleSpinner);

        StudentGrSpinner = (TextInputLayout) findViewById(R.id.StudentGrSpinner);

        unitsEt = (TextInputLayout)findViewById(R.id.unitsEt);

        paskrstytiButton = (Button)findViewById(R.id.paskrstytiButton);



         */

        getBooks();
        getStudentGroups();

        Button submitButton = findViewById(R.id.paskrstytiButton);

        submitButton.setOnClickListener(v -> {
            String bookTitle = bookTitleSpinner.getSelectedItem().toString();
            String formName = groupTitleSpinner.getSelectedItem().toString();
            int reservedBooksAmount = seekBar.getProgress();

            List<DbBooks> book = dbBooks.stream()
                    .filter(a -> Objects.equals(a.title, bookTitle))
                    .collect(Collectors.toList());

            List<DbStudentGroups> group = dbStudentGroups.stream()
                    .filter(a -> Objects.equals(a.title, formName))
                    .collect(Collectors.toList());

            DbBooks bookArray = book.get(0);
            DbStudentGroups studentGroupsArray = group.get(0);


            Map<String, Object> data = new HashMap<>();
            data.put("bookTitle", bookTitle);
            data.put("formName", formName);
            data.put("reservedBooksAmount", reservedBooksAmount);
            data.put("availableBooksAmount", bookArray.available);
            data.put("totalBooksAmount", bookArray.total);
            data.put("bookId", bookArray.id);
            data.put("bookType", bookArray.type);
            data.put("formId", studentGroupsArray.id);

            db.collection("ReservedBooks")
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
        });



        /*
        paskrstytiButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                issueBook();
            }
        });

         */

    }


    /*
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

     */

    /*
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

     */


    private void getBooks() {
        bookTitleSpinner = findViewById(R.id.BookTitleSpinner);
        List<String> spinnerDataList = new ArrayList<>();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(IssueBookActivity.this, android.R.layout.simple_spinner_item, spinnerDataList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bookTitleSpinner.setAdapter(spinnerAdapter);

        seekBar = findViewById(R.id.seekBar);

        db.collection("Book")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String bookName = document.getString("title");
                            int available = Math.toIntExact(document.getLong("available"));

                            spinnerDataList.add(bookName);

                            seekBar.setEnabled(true);
                            seekBar.setMax(available);
                        }

                        dbBooks = task.getResult().toObjects(DbBooks.class);

                        spinnerAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void getStudentGroups() {
        groupTitleSpinner = findViewById(R.id.GroupTitleSpinner);
        List<String> spinnerDataList = new ArrayList<>();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(IssueBookActivity.this, android.R.layout.simple_spinner_item, spinnerDataList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        groupTitleSpinner.setAdapter(spinnerAdapter);

        db.collection("StudentGroup")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String bookName = document.getString("form");

                            DbStudentGroups list = new DbStudentGroups();
                            list.id = document.getId();
                            list.title = bookName;

                            dbStudentGroups.add(list);

                            spinnerDataList.add(bookName);
                        }

                        dbStudentGroups = task.getResult().toObjects(DbStudentGroups.class);

                        spinnerAdapter.notifyDataSetChanged();
                    }
                });
    }
}




