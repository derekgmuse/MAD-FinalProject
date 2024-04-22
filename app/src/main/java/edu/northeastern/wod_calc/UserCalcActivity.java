package edu.northeastern.wod_calc;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents the functionality of using the workout duration calculator while being logged
 * in as a user.  A recycler view is set up to display SingleMovements (movements and repetitions)
 * that make up a workout.  Users can add to the workout, edit parts of the workout with a click
 * and delete parts of the workout by swiping.  Clicking the calculate button displays an alert
 * dialog with workout duration estimate.  The user can do one of three things from here: log the
 * workout (taking them to their user log), reset the calculator (clearing recycler view), or
 * edit the existing workout.
 */
public class UserCalcActivity extends AppCompatActivity {

    private List<SingleMovement> workout = new ArrayList<>();
    private RecyclerView workoutView;
    private CalculatorAdapter adapter;
    private Movement_Data allMovements;
    private ArrayList<String> all_mvmt;

    /**
     * Initializes the allMovements array list that contains movement durations, as well as all_mvmt
     * that contains the names of all movements that will be used in creating a drop down Spinner.
     * The toolbar is set up to include the user's username as well as the ability to sign out or
     * navigate to the user log. The recycler view is set, as well as the functionality of the add
     * and calculate buttons.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_calc);

        allMovements = new Movement_Data();
        all_mvmt = allMovements.getMovementNames();

        setUpToolBar();
        setUpRecyclerView();
        setUpAddButton();
        setUpCalculateButton();
    }

    /**
     * Setting up the toolbar by first getting the currently authenticated user's username from
     * firebase and setting the title of the toolbar.  Next, setting up the sign out and log buttons
     * to allow for the user to sign out or navigate to the log.
     */
    private void setUpToolBar(){
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userID);
        DatabaseReference usernameRef = userRef.child("username");

        usernameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String username = dataSnapshot.getValue().toString();
                TextView title = findViewById(R.id.userCalc_title);
                title.setText(username + "'s Calculator");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Firebase", "Value event listener cancelled for getting username");
            }
        });

        Button log = findViewById(R.id.button_log);

        log.setOnClickListener(view -> {
            Intent intent = new Intent(UserCalcActivity.this, UserLogActivity.class);
            startActivity(intent);
        });

        Button sign_out = findViewById(R.id.button_calc_sign_out);
        sign_out.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(UserCalcActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Sets up the recycler view by setting the layout manager and attaching the adapter.  Onclick
     * callback makes use of the setUpSelectDialog helper function.  Utilizes the ItemTouchHelper in
     * order to implement onSwipe functionality to delete movements from the workout.
     */
    private void setUpRecyclerView() {
        workoutView = findViewById(R.id.user_wod_RV);
        workoutView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CalculatorAdapter(UserCalcActivity.this, workout, new CalculatorAdapter.MovementClickListener() {
            @Override
            public void onMovementClick(int position) {
                setUpSelectDialog(false, position);
            }
        });
        workoutView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                SingleMovement toDelete = workout.get(pos);
                workout.remove(pos);
                adapter.notifyItemRemoved(pos);
                setUpDeleteDialog(pos, toDelete);
            }
        }).attachToRecyclerView(workoutView);
    }

    /**
     * Setting up the button to allow for user to add a movement to the workout.  Makes use of the
     * setUpSelectDialog helper function.
     */
    private void setUpAddButton(){
        Button add = findViewById(R.id.button_add_mvmt);

        add.setOnClickListener(view -> {
            setUpSelectDialog(true, 0);
        });
    }

    /**
     * Setting up the calculator button.  Creates a new alert dialog displaying an estimated time
     * it would take to complete the workout entered by the user.  The user can choose from one of
     * three options when a workout is calculated.  They can log the workout, which adds the workout
     * to Firebase with the workout movements formatted as a string, the estimated time, userid and
     * workout id set with UUID.randomUUID().  Otherwise, the user can reset the calc, clearing the
     * recycler view or edit the workout, which preserves the current recycler view.
     */
    private void setUpCalculateButton(){
        Button calculate = findViewById(R.id.button_user_calculate);
        calculate.setOnClickListener(v->{
            String result = calculateWOD(true);

            AlertDialog.Builder calc_result = new AlertDialog.Builder(UserCalcActivity.this);

            View result_layout = getLayoutInflater().inflate(R.layout.dialogue_result, null);

            TextView timeText = result_layout.findViewById(R.id.textView_time);
            timeText.setText(result);

            calc_result.setView(result_layout);

            //Setting Enter/Cancel buttons on AlertDialog
            calc_result.setPositiveButton("Log Workout", (dialogInterface, i) -> {

                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                String workoutId = UUID.randomUUID().toString();
                UserWorkout newWOD = new UserWorkout(wodToString(), calculateWOD(false), userId, workoutId);
                FirebaseAPI.addWorkout(newWOD);

                workout.clear();
                adapter.notifyDataSetChanged();
                Intent intent = new Intent(UserCalcActivity.this, UserLogActivity.class);
                startActivity(intent);
            });

            calc_result.setNeutralButton("Reset Calc", (dialogInterface, i) -> {
                //resetting the recyclerview
                workout.clear();
                adapter.notifyDataSetChanged();
            });

            calc_result.setNegativeButton("Edit Workout", (dialogInterface, i) -> {
                dialogInterface.cancel();
            });

            calc_result.show();

        });
    }

    /**
     * Creating a string of movements and repetitions to be stored in firebase and later displayed
     * in the user log.
     * @return a formatted string of movements and respective repetitions
     */
    private String wodToString(){
        String result = "";
        for(SingleMovement wod : workout){
            result += (wod.toString() + "\n");
        }
        return result;
    }

    /**
     * Calculates the current workout based upon SingleMovements within the recycler view.  Iterates
     * through each movement and divides the number of repetitions by the number of reps that can
     * be completed in a minute producing the double total time it takes to complete the workout.
     *
     * @return a formatted String with the total time it takes to complet a specific workout
     */
    private String calculateWOD(boolean formatted){

        double total_time = 0;
        for(SingleMovement mvmt : workout){
            String movement_name = mvmt.getName();
            double movement_reps = mvmt.getReps();
            double reps_per_minute = allMovements.getReps(movement_name); //number of reps in a minute
            double added_time = movement_reps / reps_per_minute;
            total_time += added_time;
        }
        int minutes = (int)Math.floor(total_time);
        double seconds = (total_time - minutes)*60;
        int sec = (int)Math.round(seconds);

        String time;
        if(formatted){
            time = minutes + " minutes, " + sec + " seconds";
        }
        else{
            time = minutes + " m " + sec + " s";
        }
        return time;
    }

    /**
     * Setting up the select dialiog for adding a movement to the workout.  The user is able to
     * select a movement from a custom drop down spinner containing all possible movements included
     * in the all_mvmt array list. This dialog is used for cases when users are entering new movements
     * or editing existing movements.
     *
     * @param adding a boolean value that represents if the user is adding a new movement (true) or
     *               editing an existing movement (false)
     * @param pos the position of the movement in the recycler view if the user is editing an existing
     *            movement
     */
    private void setUpSelectDialog(boolean adding, int pos){
        AlertDialog.Builder enter_mvmt = new AlertDialog.Builder(UserCalcActivity.this);

        View calc_layout = getLayoutInflater().inflate(R.layout.dialogue_calc, null);

        Spinner select_mvmt = calc_layout.findViewById(R.id.spinner_mvmt);
        ArrayAdapter<String> mvmt_adapter =
                new ArrayAdapter<String>(
                        UserCalcActivity.this,
                        android.R.layout.simple_spinner_item,
                        all_mvmt);
        mvmt_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        select_mvmt.setAdapter(mvmt_adapter);

        EditText repText = calc_layout.findViewById(R.id.editText_repetitions);

        //in this case we are editing an existing movement in the recyclerview
        if (!adding){
            String selection = workout.get(pos).getName();
            int selected_reps = (int)workout.get(pos).getReps();

            select_mvmt.setSelection(mvmt_adapter.getPosition(selection));
            repText.setText(Integer.toString(selected_reps));
        }

        enter_mvmt.setView(calc_layout);

        enter_mvmt.setPositiveButton("Enter", (dialogInterface, i) -> {

            String name = select_mvmt.getSelectedItem().toString();
            int reps = Integer.parseInt(repText.getText().toString());

            //adding the item to RecyclerView
            if(adding){
                workout.add(new SingleMovement(name, reps));
            }
            //editing element in RecyclerView
            else{
                workout.set(pos, new SingleMovement(name, reps));
            }

            adapter.notifyDataSetChanged();

        });

        enter_mvmt.setNegativeButton("Cancel", (dialogInterface, i) -> {
            dialogInterface.cancel();
        });

        enter_mvmt.show();
    }

    /**
     * Prompting the user if they are positive they want to delete a movement from the workout
     * calculator.  The item is deleted onSwipe to the left, but in the case the user wants to undo
     * this deletion, the SingleMovement is added back to the recycler view in the position it was
     * previously in.
     *
     * @param pos the position of the deleted movement
     * @param deletedMovement the SingleMovement deleted from the recycler view
     */
    private void setUpDeleteDialog(int pos, SingleMovement deletedMovement){
        AlertDialog.Builder delete_mvmt = new AlertDialog.Builder(UserCalcActivity.this);
        delete_mvmt.setTitle("You have deleted " + deletedMovement.getName() + " for " + deletedMovement.getReps() + " reps");

        //Setting Yes/Cancel buttons on AlertDialog
        delete_mvmt.setPositiveButton("Confirm", (dialogInterface, i) -> {
            dialogInterface.cancel();
        });

        delete_mvmt.setNegativeButton("Undo Delete", (dialogInterface, i) -> {
            workout.add(pos, deletedMovement);
            adapter.notifyItemInserted(pos);
        });

        delete_mvmt.show();
    }
}