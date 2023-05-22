package com.example.myfirebasetest;


import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class RemoveBookActivity extends AppCompatActivity implements View.OnClickListener {

    private Button findBook;

    private TextInputLayout enterBid;

    FirebaseFirestore db;

    private ProgressDialog progressDialog;

    Book b1;
    @Override
    public void onBackPressed(){
        finish();
        super.onBackPressed();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_book);
        findBook=(Button)findViewById(R.id.findBook);
        enterBid=(TextInputLayout)findViewById(R.id.idEt);
        FirebaseApp.initializeApp(this);
        db=FirebaseFirestore.getInstance();
        findBook.setOnClickListener(this);
        progressDialog=new ProgressDialog(this);
        progressDialog.setCancelable(false);
        b1=new Book();

        findBook.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        progressDialog.setMessage("Palaukite");
        progressDialog.show();
        if(v==findBook) {
            if (enterBid.getEditText().getText().toString().trim().isEmpty()) {

                progressDialog.cancel();
                enterBid.setError("Reikalingas vadovėlio ID ");
                enterBid.setErrorEnabled(true);
                return;
            }

            String id = enterBid.getEditText().getText().toString().trim();
            db.document("Book/" + id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().exists()) {
                            AlertDialog.Builder alert= new AlertDialog.Builder(RemoveBookActivity.this);
                            b1 = task.getResult().toObject(Book.class);
                            String temp = "Title : " + b1.getTitle() + "\nCategory : " + b1.getType() + "\nNo. of Units : " + b1.getTotal();
                            progressDialog.cancel();
                            alert.setMessage(temp).setTitle("Patvirtinkite !").setCancelable(false).setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    dialog.cancel();
                                    progressDialog.setMessage("Šalinama ... ");
                                    progressDialog.show();
                                    if(b1.getAvailable()==b1.getTotal())
                                    {
                                        db.document("Book/"+enterBid.getEditText().getText().toString().trim()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                progressDialog.cancel();
                                                Toast.makeText(RemoveBookActivity.this, "Vadovėlis Pašalintas", Toast.LENGTH_SHORT).show();

                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressDialog.cancel();
                                                Toast.makeText(RemoveBookActivity.this, "Bandykite dar kartą !", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                    else
                                    {   progressDialog.cancel();
                                        Toast.makeText(RemoveBookActivity.this, "Šitas vadovėlis išduotas skaitytojui  !\nPašalinti galima grąžinus vadovėlį į fondus.", Toast.LENGTH_LONG).show();

                                    }

                                }
                            }).setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            AlertDialog alertDialog=alert.create();
                            alertDialog.show();


                        } else {
                            progressDialog.cancel();
                            Toast.makeText(RemoveBookActivity.this, "Vadovėlis nerastas !", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressDialog.cancel();
                        Toast.makeText(RemoveBookActivity.this, "Bandykite dar kartą !", Toast.LENGTH_SHORT).show();
                    }

                }
            });

        }
    }


}