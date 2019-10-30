package com.example.photoblog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    //initialize the tool bar
    private Toolbar mainToolbar;
    private FloatingActionButton addPostBtn;
    //make a firebase variable for the logout function
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private String current_user_id;

    private BottomNavigationView mainbottomNav;

    //instantiate the fragments
    private HomeFragment homeFragment;
    private  NotificationFragment notificationFragment;
    private  AccountFragment accountFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize the variable
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        // Declare the tool bar
        mainToolbar = (Toolbar)findViewById(R.id.main_toolbar);
       setSupportActionBar(mainToolbar);
        //set the tittle  for the toolbar
        getSupportActionBar().setTitle("Photo Blog");

        addPostBtn = findViewById(R.id.add_post_btn);
        mainbottomNav = findViewById(R.id.mainBottomNav);

        if (mAuth.getCurrentUser() != null) {

            //initialize the fragments that have been instantiated.
            homeFragment = new HomeFragment();
            notificationFragment = new NotificationFragment();
            accountFragment = new AccountFragment();

            replaceFragment(homeFragment);

            mainbottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                    switch (menuItem.getItemId()) {

                        case R.id.bottom_action_home:
                            replaceFragment(homeFragment);
                            return true;

                        case R.id.bottom_action_account:
                            replaceFragment(accountFragment);
                            return true;

                        case R.id.bottom_action_notif:
                            replaceFragment(notificationFragment);
                            return true;

                        default:
                            return false;
                    }


                }
            });


            addPostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent newPostIntent = new Intent(MainActivity.this, NewPostActivity.class);
                    startActivity(newPostIntent);
                }
            });

        }

    }
    //implement callback in case the app has been minimised

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if(currentUser == null){

            sendToLogin();

        } else {

            current_user_id = mAuth.getCurrentUser().getUid();

            firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if(task.isSuccessful()){

                        if(!task.getResult().exists()){

                            Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
                            startActivity(setupIntent);
                            finish();

                        }

                    } else {

                        String errorMessage = task.getException().getMessage();
                        Toast.makeText(MainActivity.this, "Error : " + errorMessage, Toast.LENGTH_LONG).show();


                    }

                }
            });

        }

    }



    //import the menu that we created to be displayed on the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    //function that captures what item in the menu is selected.


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        //using a switch case to check the different ids of the items in the menu

        switch (item.getItemId()){
            case R.id.action_logout_btn:
                //if the logout button i pressed then execute the logout method.
                logout();
                return true;
            case R.id.action_settings_btn:
                Intent settingsIntent = new Intent(MainActivity.this,SetupActivity.class);
                startActivity(settingsIntent);
                return true;

                default:
                    return false;

        }


    }

    private void logout() {

        mAuth.signOut();
        sendToLogin();
        
    }

// method sends the user to login page
    private void sendToLogin() {
        //take the user to the login page
        Intent loginIntent = new Intent(MainActivity.this,Login_Activity.class);
        startActivity(loginIntent);

        //make sure the user doesn't come back using the back button once logged in.
        finish();

    }
    //method to change the transaction when one of the buttons is pressed in the bottom nav
    private void  replaceFragment(Fragment fragment){

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();


    }
}
