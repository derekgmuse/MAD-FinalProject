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
import androidx.viewpager.widget.ViewPager;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

/**
 * The Signup activity of the application.  Allows user to sign up and create an account within the
 * application.  User enter their email, password and desired username.  After they sign up,
 * the user will be sent to the login page to verify their information before logging in.  This
 * activity uses Firebase Authentication as well as the {@link FirebaseAPI} to add a User object
 * to the database with specified user id and username.
 */

public class SignupActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText email, password, username;
    private Button button_signup;
    private TextView onClick_login;

    /**
     * The onCreate initializes the FirebaseAuth object and sets up the UI of the activity.  Users
     * are able to enter their information in editTexts as well as navigate to the Log in activity
     * if they have not yet created an account.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();

        email = findViewById(R.id.editText_signup_email);
        password = findViewById(R.id.editText_signup_password);
        username = findViewById(R.id.editText_username);
        button_signup = findViewById(R.id.button_signup);
        onClick_login = findViewById(R.id.onClick_login);

        button_signup.setOnClickListener(v -> {
            String user_email = email.getText().toString().trim();
            String user_password = password.getText().toString().trim();
            String user_name = username.getText().toString().trim();
            RegisterUser(user_email, user_password, user_name);
        });

        onClick_login.setOnClickListener(v->{
            Intent intent_login = new Intent(SignupActivity.this, LoginActivity.class);
            startActivity(intent_login);
        });
    }

    /**
     * Registers a user using Firebase Authentication.  If the email or password fields are empty,
     * error texts are provided to prompt the user to enter information in the appropriate
     * editText.  If signing up does not meet Firebase's criteria, a toast will be provided to
     * notify the user that sign up was unsuccessful.  This method also makes use of the FirebaseAPI
     * object's addUserToDatabase method, adding the user's username and userID to firebase, such
     * that this information can be used when a user is authenticated and logged into the app.
     *
     * @param email     User's email
     * @param password  User's password
     * @param username  User's username
     */
    private void RegisterUser(String email, String password, String username) {
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