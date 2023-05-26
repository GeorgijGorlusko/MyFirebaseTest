package com.example.myfirebasetest;


import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import androidx.annotation.NonNull;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateBookActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout editTitle2;

    private TextInputLayout editUnits2;
    private Spinner spinner2;
    private FirebaseFirestore db;
    private Button button2;
    private String type;
    private ProgressDialog p1;

    private Book book=new Book();
    private int qtity;

    private boolean verifyTitle()
    {
        String t=editTitle2.getEditText().getText().toString().trim();
        if(t.isEmpty())
        {
            return false;
        }
        else
        {
            return true;
        }
    }



    private boolean verifyUnits()
    {
        String u=editUnits2.getEditText().getText().toString().trim();
        if(u.isEmpty())
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    private boolean verifyCategory()
    {
        if (type.equals("Select Book Category"))
        {
            return false;
        }
        return true;
    }

    private void updateBook()
    {


        if(!(verifyCategory()|verifyTitle()|verifyUnits()))
        {
            Toast.makeText(this, "Select something to Update !", Toast.LENGTH_SHORT).show();
            return;
        }

        p1.setMessage("Updating ...");
        p1.show();

        db.document("Book/"+Integer.parseInt(editTitle2.getEditText().getText().toString().trim())).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    if(task.getResult().exists()==true)
                    {
                        book=task.getResult().toObject(Book.class);
                        if(verifyCategory()){
                            book.setType(type);
                        }

                        if(verifyUnits()){
                            int temp1=book.getTotal();
                            book.setTotal(Integer.parseInt(editUnits2.getEditText().getText().toString().trim()));
                            qtity=book.getAvailable()-temp1+book.getTotal();
                            book.setAvailable(qtity);
                        }
                        if(verifyTitle())
                        {
                            book.setTitle(editTitle2.getEditText().getText().toString().trim().toUpperCase());
                        }
                        if(qtity>=0) {
                            db.document("Book/" + editTitle2.getEditText().getText().toString().trim()).set(book).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        p1.cancel();
                                        Toast.makeText(UpdateBookActivity.this, "Informacija sėkmingai atnaujinta !", Toast.LENGTH_SHORT).show();
                                    } else {
                                        p1.cancel();
                                        Toast.makeText(UpdateBookActivity.this, "Try Again !", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        else {
                            p1.cancel();
                            Toast.makeText(UpdateBookActivity.this, "Can't Reduce No. of Units \ndue to issued units !", Toast.LENGTH_SHORT).show();
                        }

                    }
                    else
                    {
                        p1.cancel();
                        Toast.makeText(UpdateBookActivity.this, "No Such Book !", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    p1.cancel();
                    Toast.makeText(UpdateBookActivity.this, "Try Again !", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_book);
        FirebaseApp.initializeApp(this);

        editTitle2=(TextInputLayout)findViewById(R.id.titleEt);

        editUnits2=(TextInputLayout)findViewById(R.id.unitsEt);
        spinner2=(Spinner)findViewById(R.id.spinner1);
        button2=(Button)findViewById(R.id.button1);
        p1=new ProgressDialog(this);
        p1.setCancelable(false);
        db=FirebaseFirestore.getInstance();

        String A[]=getResources().getStringArray(R.array.list1);
        ArrayAdapter adapter =new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,A);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        button2.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        updateBook();
    }
}