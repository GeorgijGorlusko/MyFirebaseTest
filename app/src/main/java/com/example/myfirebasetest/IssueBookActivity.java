package com.example.myfirebasetest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
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

    private TextView seekBarValueTextView;

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

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        getBooks();
        getStudentGroups();

        Button submitButton = findViewById(R.id.paskrstytiButton);

        submitButton.setOnClickListener(v -> {
            handleSubmitReservedBooks();
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

        Map<String, Object> data = new HashMap<>();
        data.put("bookTitle", bookTitle);
        data.put("formName", formName);
        data.put("reservedBooksAmount", reservedBooksAmount);
        data.put("availableBooksAmount", bookArray.available-reservedBooksAmount);
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
    }

    private void getBooks() {
        bookTitleSpinner = findViewById(R.id.BookTitleSpinner);
        List<String> spinnerDataList = new ArrayList<>();
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(IssueBookActivity.this, android.R.layout.simple_spinner_item, spinnerDataList);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bookTitleSpinner.setAdapter(spinnerAdapter);

        seekBar = findViewById(R.id.seekBar);
        seekBar.setEnabled(false);
        seekBarValueTextView = findViewById(R.id.seekBarValueTextView);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Update the TextView with the current SeekBar value
                seekBarValueTextView.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // No implementation needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // No implementation needed
            }
        });
        db.collection("Book")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String bookName = document.getString("title");
                            int available = Math.toIntExact(document.getLong("available"));

                            spinnerDataList.add(bookName);

                            seekBar.setEnabled(true);
                            seekBar.setMax(50); // Set the maximum value of the SeekBar
                            seekBar.setProgress(0); // Set the initial progress value
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




