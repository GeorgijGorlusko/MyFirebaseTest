package com.example.myfirebasetest;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddBookActivity extends AppCompatActivity implements View.OnClickListener {



    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();

    }

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        FirebaseApp.initializeApp(this);
        //enterId=(TextInputLayout)findViewById(R.id.idEt);
        enterTitle=(TextInputLayout)findViewById(R.id.titleEt);
        spinner1=(Spinner)findViewById(R.id.spinner1);
        enterEgzNum=(TextInputLayout)findViewById(R.id.unitsEt);
        button1=(Button)findViewById(R.id.button1);
        p1=new ProgressDialog(this);
        p1.setCancelable(false);
        db= FirebaseFirestore.getInstance();

        String A[]=getResources().getStringArray(R.array.list1);
        ArrayAdapter adapter =new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,A);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(adapter);
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        button1.setOnClickListener(this);

    }

    private boolean verifyTitle()
    {
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

  /*  private boolean verifyBid()
    {
        String b=enterId.getEditText().getText().toString().trim();
        if(b.isEmpty())
        {   enterId.setErrorEnabled(true);
            enterId.setError("Reikalingas ID");
            return true;
        }
        else
        {
            enterId.setErrorEnabled(false);
            return false;
        }
    }*/

    private boolean verifyUnits()
    {
        String u=enterEgzNum.getEditText().getText().toString().trim();
        if(u.isEmpty())
        {   enterEgzNum.setErrorEnabled(true);
            enterEgzNum.setError("Reikalingas egzempliorių skaičius");
            return true;
        }
        else
        {
            enterEgzNum.setErrorEnabled(false);
            return false;
        }
    }

    private boolean verifyCategory()
    {
        if (type.equals("Pasirinkti vadovėlių kategoriją"))
        {
            Toast.makeText(this, "Pasirinkite vadovėlių kategoriją !", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void addBook()
    {

        boolean res=(/*verifyBid()|*/verifyTitle()|verifyUnits()|verifyCategory());
        if(res==true)
            return;
        else
        {

            p1.setMessage("Pridedamas vadovėlis");
            p1.show();
            final String title=enterTitle.getEditText().getText().toString().trim();


            String title1=enterTitle.getEditText().getText().toString().trim();
            db.document("Book/"+title1).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if((task.isSuccessful())&&(task.getResult().exists()==false))
                    {   /*String id=enterId.getEditText().getText().toString().trim();*/
                        String title=enterTitle.getEditText().getText().toString().trim();
                        String units=enterEgzNum.getEditText().getText().toString().trim();

                      /* int id1=Integer.parseInt(id),*/int unit1=Integer.parseInt(units);
                        Book b = new Book(title.toUpperCase(),type,unit1/*,id1*/);
                        db.document("Book/"+title1).set(b).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {   p1.cancel();
                                    Toast.makeText(AddBookActivity.this, "Vadovėlis pridėtas !", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {   p1.cancel();
                                    Toast.makeText(AddBookActivity.this, "Bandykite dar kartą !", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else
                    {
                        p1.cancel();
                        Toast.makeText(AddBookActivity.this, "Šitas vadovėlis jau pridėtas \n arba Blogas ryšys !", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }
    }


    private TextInputLayout enterTitle;

    private TextInputLayout enterId;

    private TextInputLayout enterEgzNum;

    private Spinner spinner1;

    private FirebaseFirestore db;

    Button button1;

    String type;

    ProgressDialog p1;

    @Override
    public void onClick(View v) {

        addBook();

    }

}
