package com.example.myfirebasetest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import dto.DbBooks;
import dto.DbStudentGroups;

public class BookInfoActivity extends AppCompatActivity {


    List<DbBooks> dbBooks;
    List<DbStudentGroups> dbStudentGroups;

    private Spinner bookTitleSpinner;
    private Spinner groupTitleSpinner;
    private SeekBar seekBar;

    private FirebaseFirestore db;
    private String type;
    private Spinner bookTypeInfoSpinner;
    private TextView issuedToGroupsTextView;


    private Spinner bookNameInfoSpinner;

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();

    }








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);


        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        getBooks();



        bookTypeInfoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (bookTypeInfoSpinner.getSelectedItemPosition() == AdapterView.INVALID_POSITION){
                    return;

                }
                String bookType = bookTypeInfoSpinner.getSelectedItem().toString();
                List<DbBooks> book = dbBooks.stream()
                        .filter(a -> Objects.equals(a.type, bookType))
                        .collect(Collectors.toList());
                DbBooks array = book.get(0);
                List<String> bookNameInfoSpinnerDataList = new ArrayList<>();
                ArrayAdapter<String> spinnerAdapter2 = new ArrayAdapter<>(BookInfoActivity.this, android.R.layout.simple_spinner_item, bookNameInfoSpinnerDataList);
                spinnerAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                bookNameInfoSpinner.setAdapter(spinnerAdapter2);


                for (DbBooks innerArray: book) {

                    bookNameInfoSpinnerDataList.add(innerArray.title);

                }

                TextView availableTextView = findViewById(R.id.availableBooksInfoTextView);
                TextView totalTextView = findViewById(R.id.totalAmountInfoTextView);
                availableTextView.setText(Integer.toString(array.available));
                totalTextView.setText(Integer.toString(array.total));
                issuedToGroupsTextView.setText(array.issuedToGroups);

                spinnerAdapter2.notifyDataSetChanged();
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bookNameInfoSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String bookName = bookNameInfoSpinner.getSelectedItem().toString();
                List<DbBooks> book = dbBooks.stream()
                        .filter(a -> Objects.equals(a.title, bookName))
                        .collect(Collectors.toList());
                DbBooks array = book.get(0);

                TextView availableTextView = findViewById(R.id.availableBooksInfoTextView);
                TextView totalTextView = findViewById(R.id.totalAmountInfoTextView);
                availableTextView.setText(Integer.toString(array.available));
                totalTextView.setText(Integer.toString(array.total));
                issuedToGroupsTextView.setText(array.issuedToGroups);
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getBooks() {
        bookTypeInfoSpinner = findViewById(R.id.bookTypeInfoSpinner);
        List<String> bookTypeInfoSpinnerDataList = new ArrayList<>();
        ArrayAdapter<String> spinnerAdapter1 = new ArrayAdapter<>(BookInfoActivity.this, android.R.layout.simple_spinner_item, bookTypeInfoSpinnerDataList);
        spinnerAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bookTypeInfoSpinner.setAdapter(spinnerAdapter1);

        bookNameInfoSpinner = findViewById(R.id.bookNameInfoSpinner);
        List<String> bookNameInfoSpinnerDataList = new ArrayList<>();
        ArrayAdapter<String> spinnerAdapter2 = new ArrayAdapter<>(BookInfoActivity.this, android.R.layout.simple_spinner_item, bookNameInfoSpinnerDataList);
        spinnerAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bookNameInfoSpinner.setAdapter(spinnerAdapter2);

        TextView availableTextView = findViewById(R.id.availableBooksInfoTextView);

        TextView totalTextView = findViewById(R.id.totalAmountInfoTextView);
        issuedToGroupsTextView = findViewById(R.id.issuedToGroupsTextView);

        db.collection("Book")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String bookName = document.getString("title");
                            int available = Math.toIntExact(document.getLong("available"));
                            String bookType = document.getString("type");
                            int amount = Math.toIntExact(document.getLong("total"));
                            String forms = document.getString("issuedToGroups");

                            if (!bookTypeInfoSpinnerDataList.contains(bookType)){
                                bookTypeInfoSpinnerDataList.add(bookType);
                            }
                            bookNameInfoSpinnerDataList.add(bookName);
                            availableTextView.setText(Integer.toString(available));
                            totalTextView.setText(Integer.toString(amount));

                            issuedToGroupsTextView.setText(forms);
                        }

                        dbBooks = task.getResult().toObjects(DbBooks.class);

                        spinnerAdapter1.notifyDataSetChanged();
                        spinnerAdapter2.notifyDataSetChanged();
                    }
                });
    }
}