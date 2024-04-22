package edu.northeastern.wod_calc;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

/**
 * The Main activity of the application.  Provides users with the option to Login, Signup or utilize
 * the Quick calc function of the application.  Users without accounts or users who do not want
 * accounts still have the ability to calculate workout durations by selecting Quick calc.
 */

public class MainActivity extends AppCompatActivity {

    /**
     * The onCreate sets up the UI of the activity.  In this case, each of the three methods are
     * attached to the buttons in activity_main.xml, thus we need not do anything besides setting
     * the content view in onCreate.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
    }

    /**
     * Creates an intent to allow user to navigate to the Login activity.  Starts the new activity.
     *
     * @param view The UI of this activity.
     */
    public void Login(View view){
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * Creates an intent to allow user to navigate to the Signup activity.  Starts the new activity.
     *
     * @param view The UI of this activity.
     */
    public void SignUp(View view){
        Intent intent = new Intent(MainActivity.this, SignupActivity.class);
        startActivity(intent);
    }

    /**
     * Creates an intent to allow user to navigate to the Quick calc activity.  Starts the new activity.
     *
     * @param view The UI of this activity.
     */
    public void QuickCalc(View view){
        Intent intent = new Intent(MainActivity.this, QuickCalcActivity.class);
        startActivity(intent);
    }
}