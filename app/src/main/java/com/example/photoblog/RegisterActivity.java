package com.example.photoblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    //declare the variables
    private EditText reg_email_field;
    private EditText reg_pass_field;
    private EditText reg_confirm_pass_field;
    private Button reg_btn;
    private Button reg_login_button;
    private ProgressBar reg_progress;

    //declare the Firebase authentication variable.
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
       //initialize the firebase variable
       mAuth = FirebaseAuth.getInstance();

     //get the input from the fields by calling them with their ids
    reg_email_field = (EditText)findViewById(R.id.reg_email);
    reg_pass_field = (EditText)findViewById(R.id.reg_pass);
    reg_confirm_pass_field = (EditText)findViewById(R.id.reg_confirm_pass);
    reg_btn= (Button)findViewById(R.id.reg_btn);
    reg_login_button = (Button)findViewById(R.id.reg_login_btn);
    reg_progress = (ProgressBar)findViewById(R.id.reg_progress);

    //show the progress bar to show that you are fetching the data from the database.


    reg_login_button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    });

    reg_btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //these are variable that will store the data that is obtained from the registration field
            // these variables will then be used to insert the data in the database.

            String email=reg_email_field.getText().toString();
            String pass = reg_pass_field.getText().toString();
            String confirm_pass = reg_confirm_pass_field.getText().toString();

            //this If statement is to check if the any of the fields are empty
            if (!TextUtils.isEmpty(email) &&!TextUtils.isEmpty(pass) & !TextUtils.isEmpty(confirm_pass))
            {
                //once all the fields have been filled check if the password and the confirm password match
                if (pass.equals(confirm_pass)){
                    reg_progress.setVisibility(View.VISIBLE);
                    //if they match add the user to the database
                mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // if the account has been created successfully
                        if(task.isSuccessful())
                        {
                            //send the user to the Account Setup Activity
                            Intent setupIntent = new Intent(RegisterActivity.this,SetupActivity.class);
                            startActivity(setupIntent);
                            finish();

                        }else
                            {
                                //if the registration failed show an error message as a toast
                        String errorMessage =task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this, "Error Message: " + errorMessage,Toast.LENGTH_LONG).show();

                            }

                        reg_progress.setVisibility(View.INVISIBLE);
                    }
                });

                }else
                    {
                        //else display toast that the passwords don't match
                        Toast.makeText(RegisterActivity.this, "confrim password and password doesn't match",Toast.LENGTH_LONG).show();
                    }
            }

        }
    });

    }

    @Override
    protected void onStart() {
        super.onStart();

        //on start of this activity check if the user is logged in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // if the user is already logged in then execute the sendToMain method;
        if (currentUser != null)
        {
            sendToMain();
        }
        
    }

    private void sendToMain() {
        //send the user to the main page if they already logged in
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
