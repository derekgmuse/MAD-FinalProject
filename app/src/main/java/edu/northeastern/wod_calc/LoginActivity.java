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

/**
 * The Login activity of the application.  Allows user to enter their email and password to log in
 * to their account where they will have the ability to calculate workouts and view their workout
 * log.  Utilizes Firebase's email and password authentication.
 */

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText email, password;
    private Button button_login;
    private TextView onClick_signup;

    /**
     * The onCreate initializes the FirebaseAuth object and sets up the UI of the activity.  Users
     * are able to enter their information in editTexts as well as navigate to the Sign up activity
     * if they have not yet created an account.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.editText_login_email);
        password = findViewById(R.id.editText_login_password);
        button_login = findViewById(R.id.button_login);
        onClick_signup = findViewById(R.id.onClick_signup);

        button_login.setOnClickListener(v -> {
            String user_email = email.getText().toString().trim();
            String user_password = password.getText().toString().trim();
            LoginUser(user_email, user_password);
        });

        onClick_signup.setOnClickListener(v->{
            Intent intent_signup = new Intent(LoginActivity.this, SignupActivity.class);
            startActivity(intent_signup);
        });
    }

    /**
     * Logs in a user using Firebase Authentication.  In the case login fails, a toast message is
     * set to alert the user that login has failed.  If the user leaves the email field blank, the
     * text will be set within the email editText to alert the user that it cannot be blank.
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
            this.email.setText("Email cannot be empty");
        }
    }
}