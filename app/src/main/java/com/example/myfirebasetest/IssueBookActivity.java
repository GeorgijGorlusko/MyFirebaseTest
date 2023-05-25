package com.example.myfirebasetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class IssueBookActivity extends AppCompatActivity {    List<DbBooks> dbBooks;
    List<DbStudentGroups> dbStudentGroups;

    private Spinner bookTitleSpinner;
    private Spinner groupTitleSpinner;
    private SeekBar seekBar;

    private FirebaseFirestore db;

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_book);

        TextView seekBarTooltip = findViewById(R.id.seekBarTooltip);
        Button submitButton = findViewById(R.id.paskrstytiButton);

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        getBooks();
        getStudentGroups();

        submitButton.setOnClickListener(v -> {
            handleSubmitReservedBooks();
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                seekBarTooltip.setText(Integer.toString(i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                seekBarTooltip.setVisibility(View.VISIBLE);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBarTooltip.setVisibility(View.GONE);
            }
        });

        bookTitleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                DbBooks bookArray = dbBooks.get(position);

                seekBar.setMax(bookArray.available);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
    }

    private void handleSubmitReservedBooks() {
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

        if (bookArray.available - reservedBooksAmount < 0) {
            return;
        }

        Map<String, Object> reservedBooksData = new HashMap<>();
        reservedBooksData.put("bookTitle", bookTitle);
        reservedBooksData.put("formName", formName);
        reservedBooksData.put("reservedBooksAmount", reservedBooksAmount);
        reservedBooksData.put("availableBooksAmount", bookArray.available - reservedBooksAmount);
        reservedBooksData.put("totalBooksAmount", bookArray.total);
        reservedBooksData.put("bookId", bookArray.id);
        reservedBooksData.put("bookType", bookArray.type);
        reservedBooksData.put("formId", studentGroupsArray.id);
        reservedBooksData.put("isHidden", false);

        Map<String, Object> booksData = new HashMap<>();
        booksData.put("title", bookTitle);
        booksData.put("available", bookArray.available - reservedBooksAmount);
        booksData.put("total", bookArray.total);
        booksData.put("type", bookArray.type);
        reservedBooksData.put("isHidden", false);

        db.collection("ReservedBooks")
                .add(reservedBooksData)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        db.collection("Book").document(String.valueOf(bookArray.id))
                                .update(booksData)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        getBooks();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w("aaaa", "Error updating document", e);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("aaaaa", "Error adding document", e);
                    }
                });


    }

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
        dbStudentGroups = new ArrayList<>();

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

                        spinnerAdapter.notifyDataSetChanged();
                    }
                });
    }
}




