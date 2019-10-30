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

public class Login_Activity extends AppCompatActivity {

    //initialize the fields that were made in the login layout
    private EditText loginEmailText;
    private EditText loginPassText;
    private Button loginBtn;
    private Button loginRegBtn;

    //make the firebase auth into a variable to avoid repetition
    private FirebaseAuth mAuth;

    private ProgressBar loginProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initialize the mAuth variable
        mAuth = FirebaseAuth.getInstance();

        //get the fields data using their ID
        loginEmailText= (EditText)findViewById(R.id.login_email);
        loginPassText = (EditText)findViewById(R.id.login_password);
        loginBtn = (Button)findViewById(R.id.login_btn);
        loginRegBtn= (Button)findViewById(R.id.login_reg_btn);
        loginProgress = (ProgressBar)findViewById(R.id.login_progress);

        loginRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent regIntent = new Intent(Login_Activity.this,RegisterActivity.class);
                startActivity(regIntent);
            }
        });


        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                //the login button is pressed get the data from the field and convert it to strings.
                String loginEmail = loginEmailText.getText().toString();
                String loginPass = loginPassText.getText().toString();

                //using an If statement to check if any of the login or password fields are empty
                if (!TextUtils.isEmpty(loginEmail) && !TextUtils.isEmpty(loginPass)){

                    //if bothe login and the password fields are not empty then show the progress bar
                    loginProgress.setVisibility(View.VISIBLE);

                    //after showing the progress bar start the sign-in method
                   mAuth.signInWithEmailAndPassword(loginEmail, loginPass) .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task) {

                           //this method is to make sure that the task is complete
                          if (task.isSuccessful())
                          {
                              //if the task is successful send the user to the main page
                              sendToMain();
                          }else {
                              //else if the task is nto successful then do this
                              String errorMessage = task.getException().getMessage();

                              //place the error in a toast and display the error
                              Toast.makeText(Login_Activity.this,"Error: "+ errorMessage, Toast.LENGTH_LONG).show();

                          }

                           loginProgress.setVisibility(View.INVISIBLE);

                       }
                   });

                }

            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
           sendToMain();
        }
    }
    //method to send the user to the main page after they have successfully logged in.

    private void sendToMain() {

        //if the user is already logged in take them to the main activity
        Intent mainIntent = new Intent(Login_Activity.this,MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
