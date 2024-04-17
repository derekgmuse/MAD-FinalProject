package edu.northeastern.wod_calc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;


public class SignupActivity extends AppCompatActivity {

    //Firebase authentication
    private FirebaseAuth auth;

    //EditTexts for user email, password and username
    private EditText email, password, username;

    //Button for user to sign up
    private Button button_signup;

    //Text to allow user to navigate to login if they already have signed up
    private TextView onClick_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        // Initialize Firebase Authentication instance
        auth = FirebaseAuth.getInstance();

        // Gathering the UI components
        email = findViewById(R.id.editText_signup_email);
        password = findViewById(R.id.editText_signup_password);
        username = findViewById(R.id.editText_username);
        button_signup = findViewById(R.id.button_signup);
        onClick_login = findViewById(R.id.onClick_login);

        //setting onClick for the sign up button
        button_signup.setOnClickListener(v -> {
            String user_email = email.getText().toString().trim();
            String user_password = password.getText().toString().trim();
            String user_name = username.getText().toString().trim();


            // Perform user registration with Firebase Authentication.
            RegisterUser(user_email, user_password, user_name);
        });

        //setting onClick for the text view to navigate to login activity
        onClick_login.setOnClickListener(v->{
            Intent intent_login = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent_login);
        });
    }

    /**
     * Registers a user using Firebase Authentication
     * source - https://www.youtube.com/watch?v=TStttJRAPhE&ab_channel=AndroidKnowledge
     *
     * @param email     User's email
     * @param password  User's password
     * @param username  User's username
     */
    private void RegisterUser(String email, String password, String username) {
        // Validation and Firebase Authentication logic
        if (!email.isEmpty() && !password.isEmpty()) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    User newUser = new User(userId, username);
                    FirebaseAPI.addUserToDatabase(this, newUser);
                    Toast.makeText(SignupActivity.this, "Sign up successful!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignupActivity.this, "Sign up unsuccessful." + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else if (email.isEmpty()) {
            this.email.setError("Please enter an email here");
        } else {
            this.password.setError("Please enter a password here");
        }
    }

}