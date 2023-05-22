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

public class AddStudentGroupActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputLayout enterTitle;


    private FirebaseFirestore db;

    Button buttonAddStGr;

    String type;

    ProgressDialog p1;

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
        p1=new ProgressDialog(this);
        p1.setCancelable(false);
        db= FirebaseFirestore.getInstance();
        buttonAddStGr.setOnClickListener(this);

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







    private void addStudentGroup()
    {

        boolean res=(verifyTitle());
        if(res==true)
            return;
        else
        {

            p1.setMessage("Pridedama klasė");
            p1.show();
            final String title=enterTitle.getEditText().getText().toString().trim();
            String title1 = new String(title);
            db.document("StudentGroup/"+title1).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if((task.isSuccessful())&&(task.getResult().exists()==false))
                    {
                        String title=enterTitle.getEditText().getText().toString().trim();


                        StudentGroup b = new StudentGroup(title.toUpperCase());
                        db.document("StudentGroup/"+title1).set(b).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {   p1.cancel();
                                    Toast.makeText(AddStudentGroupActivity.this, "Klasė pridėta !", Toast.LENGTH_SHORT).show();
                                }
                                else
                                {   p1.cancel();
                                    Toast.makeText(AddStudentGroupActivity.this, "Bandykite dar kartą !", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                    else
                    {
                        p1.cancel();
                        Toast.makeText(AddStudentGroupActivity.this, "Šita klasė jau pridėta \n arba Blogas ryšys !", Toast.LENGTH_SHORT).show();

                    }
                }
            });

        }}




    @Override
    public void onClick(View v) {

        addStudentGroup();

    }

}