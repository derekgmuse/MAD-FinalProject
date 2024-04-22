package edu.northeastern.wod_calc;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * Represents a log of workouts calculated and possibly completed by an authenticated user.  The
 * log is a recycler view of UserWorkouts.  Each item displays the date the workout was logged,
 * the estimated time to complete, the actual time to complete and the difficulty of the workout.
 * Clicking the workout opens a dialog that displays all movements and reps of the workout.
 * Long clicking the workout allows users to edit the actual time and difficulty of the workout.
 */

public class UserLogActivity extends AppCompatActivity {

    private List<UserWorkout> allWorkouts  = new ArrayList<>();
    private RecyclerView logView;
    private UserLogAdapter adapter;

    /**
     * First, the toolbar is set up to include the user's username as well as the ability to sign out or
     * navigate to the user calculator.  Next, the workouts are loaded from firebase by iterating through
     * all workouts and adding workouts with the currently authenticated user's id to the allWorkouts array.
     * Lastly the recycler view is set including all workouts loaded.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_log);

        setUpToolBar();
        loadUserWorkouts();
        setUpRecyclerView();
    }

    /**
     * Setting up the toolbar by first getting the currently authenticated user's username from
     * firebase and setting the title of the toolbar.  Next, setting up the sign out and calc buttons
     * to allow for the user to sign out or navigate to the user calc.
     */
    private void setUpToolBar(){
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userID);
        DatabaseReference usernameRef = userRef.child("username");
        usernameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String username = dataSnapshot.getValue().toString();
                TextView title = findViewById(R.id.userLog_title);
                title.setText(username + "'s Log");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("Firebase", "Value event listener cancelled for getting username");
            }
        });

        Button log = findViewById(R.id.button_calc);

        log.setOnClickListener(view -> {
            Intent intent = new Intent(UserLogActivity.this, UserCalcActivity.class);
            startActivity(intent);
        });

        Button sign_out = findViewById(R.id.button_log_sign_out);
        sign_out.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(UserLogActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Loading user workouts utilizing the WorkoutDataCallback set up within firebase such that
     * user workouts can be loaded up asynchronously.  The workouts have been loaded within the
     * FirebaseAPI, sent here through the onWorkoutsLoaded method, and then added to the allWorkouts
     * array list before notifying the adapter that a change has been made to the recycler view.
     */
    private void loadUserWorkouts(){
        FirebaseAPI.loadWorkouts(new FirebaseAPI.WorkoutDataCallback() {
            @Override
            public void onWorkoutsLoaded(List<UserWorkout> workouts) {
                allWorkouts.clear();
                allWorkouts.addAll(workouts);
                Log.d("workouts", allWorkouts.toString());
                if(adapter != null) {
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    /**
     * Sets up the recycler view by setting the layout manager and attaching the adapter.  Onclick
     * and onLongClick callbacks make use of the setUpSelectDialog helper function.  Utilizes the
     * ItemTouchHelper in order to implement onSwipe functionality to delete workouts from the log.
     */
    private void setUpRecyclerView(){
        logView = findViewById(R.id.user_log_RV);
        logView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserLogAdapter(UserLogActivity.this, allWorkouts,
                new UserLogAdapter.WorkoutClickListener() {
                    @Override
                    public void onWorkoutClick(int position) { setUpWorkoutDialog(false, position); }
                },
                new UserLogAdapter.WorkoutLongClickListener() {
                    @Override
                    public void onWorkoutLongClick(int position) { setUpWorkoutDialog(true, position); }
                });
        logView.setAdapter(adapter);

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getAdapterPosition();
                UserWorkout deletedWorkout = allWorkouts.get(pos);
                allWorkouts.remove(pos);
                adapter.notifyItemRemoved(pos);
                setUpDeleteDialog(pos, deletedWorkout);
            }
        }).attachToRecyclerView(logView);
    }

    /**
     * Sets up the dialog used when a workout in the log is either clicked or long clicked.  In the
     * case when it is clicked, the user will be presented with a dialog displaying the workout as well
     * as date, estimated time, actual time and difficulty.  In the case of a long click, the user is able
     * to edit the actual time and difficulty of a specific workout
     * @param editing is true if the user long clicks and false if it is simply a click
     * @param pos the position of the workout in the recycler view
     */
    private void setUpWorkoutDialog(boolean editing, int pos){
        AlertDialog.Builder view_wod = new AlertDialog.Builder(UserLogActivity.this);

        if(editing) {
            View wod_layout = getLayoutInflater().inflate(R.layout.dialogue_log, null);

            EditText minText = wod_layout.findViewById(R.id.editText_minutes);
            EditText secText = wod_layout.findViewById(R.id.editText_seconds);
            EditText difficultyText = wod_layout.findViewById(R.id.editText_difficulty);

            view_wod.setView(wod_layout);

            view_wod.setPositiveButton("Enter", (dialogInterface, i) -> {
                String mins = minText.getText().toString();
                String secs = secText.getText().toString();
                String diff = difficultyText.getText().toString();
                UserWorkout currentWOD = allWorkouts.get(pos);
                currentWOD.setActualTime(mins + " m " + secs + " s");
                currentWOD.setDifficulty(diff+"/10");
                adapter.notifyDataSetChanged();
                FirebaseAPI.updateWorkout(currentWOD);
            });

            view_wod.setNegativeButton("Cancel", (dialogInterface, i) -> {
                dialogInterface.cancel();
            });
        }
        else{
            TextView title = new TextView(this);
            title.setText("Workout of the Day");
            title.setTextSize(21);
            title.setTypeface(Typeface.DEFAULT_BOLD);
            title.setGravity(Gravity.CENTER);
            view_wod.setCustomTitle(title);
            View log_layout = getLayoutInflater().inflate(R.layout.dialogue_wod, null);
            TextView wodList = log_layout.findViewById(R.id.wod_list);
            String selectedWOD = allWorkouts.get(pos).toString();
            wodList.setText(selectedWOD);
            view_wod.setView(log_layout);
            view_wod.setNegativeButton("Cancel", (dialogInterface, i) -> {
                dialogInterface.cancel();
            });
        }

        view_wod.show();
    }

    /**
     * Prompting the user if they are positive they want to delete a workout from the log.
     * The item is deleted onSwipe to the left, but in the case the user wants to undo
     * this deletion, the UserWorkout is added back to the recycler view in the position it was
     * previously in.  If the user is sure they want to delete the workout, the workout is also
     * deleted from the firebase realtime database with the call to deleteWorkout.
     *
     * @param pos the position of the deleted workout
     * @param deletedWorkout the UserWorkout deleted from the recycler view
     */
    private void setUpDeleteDialog(int pos, UserWorkout deletedWorkout){
        AlertDialog.Builder delete_mvmt = new AlertDialog.Builder(UserLogActivity.this);
        delete_mvmt.setTitle("Workout Deleted");

        //Setting Yes/Cancel buttons on AlertDialog
        delete_mvmt.setPositiveButton("Confirm", (dialogInterface, i) -> {
            FirebaseAPI.deleteWorkout(deletedWorkout);
            dialogInterface.cancel();
        });

        delete_mvmt.setNegativeButton("Undo Delete", (dialogInterface, i) -> {
            allWorkouts.add(pos, deletedWorkout);
            adapter.notifyItemInserted(pos);
        });
        delete_mvmt.show();
    }
}