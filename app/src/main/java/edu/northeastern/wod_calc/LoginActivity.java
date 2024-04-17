package edu.northeastern.wod_calc;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    //Firebase authentication
    private FirebaseAuth auth;

    //EditTexts for user email and password
    private EditText email, password;


    //Button for user to login
    private Button button_login;

    //Text to allow user to navigate to signup if they don't already have an account
    private TextView onClick_signup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Authentication instance
        auth = FirebaseAuth.getInstance();

        //Gathering the UI components
        email = findViewById(R.id.editText_login_email);
        password = findViewById(R.id.editText_login_password);
        button_login = findViewById(R.id.button_login);
        onClick_signup = findViewById(R.id.onClick_signup);

        //setting onClick for the sign up button
        button_login.setOnClickListener(v -> {
            String user_email = email.getText().toString().trim();
            String user_password = password.getText().toString().trim();


            // Perform user registration with Firebase Authentication.
            LoginUser(user_email, user_password);
        });

        //setting onClick for the text view to navigate to login activity
        onClick_signup.setOnClickListener(v->{
            Intent intent_signup = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent_signup);
        });
    }

    /**
     * Logs in a user using Firebase Authentication
     *
     * Sources - https://www.youtube.com/watch?v=TStttJRAPhE&ab_channel=AndroidKnowledge
     *        - https://www.geeksforgeeks.org/user-authentication-using-firebase-in-android/
     *
     * @param email     User's email.
     * @param password  User's password.
     */
    private void LoginUser(String email, String password){
        if(!email.isEmpty()){
            auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(t->{
                Toast.makeText(LoginActivity.this, "User Successfully Logged In", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, UserCalcActivity.class);
                startActivity(intent);
                finish();
            }).addOnFailureListener(t->{
                Toast.makeText(LoginActivity.this, "Log in Failed.  Try again", Toast.LENGTH_SHORT).show();
            });
        }
        else {
            //accessing editText
            this.email.setText("Email cannot be empty");
        }
    }
}