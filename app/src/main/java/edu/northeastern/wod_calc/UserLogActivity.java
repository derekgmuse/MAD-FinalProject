package edu.northeastern.wod_calc;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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

public class UserLogActivity extends AppCompatActivity {

    private List<UserWorkout> allWorkouts  = new ArrayList<>();
    private RecyclerView logView;
    private UserLogAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_log);

        setUpToolBar();
        loadUserWorkouts();
        setUpRecyclerView();

    }

    private void setUpToolBar(){
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userID);
        DatabaseReference usernameRef = userRef.child("username");

        usernameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String username = dataSnapshot.getValue().toString();
                TextView title = findViewById(R.id.userLog_title);
                title.setText("Welcome " + username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        Button log = findViewById(R.id.button_calc);

        log.setOnClickListener(view -> {
            Intent intent = new Intent(UserLogActivity.this, UserCalcActivity.class);
            startActivity(intent);
        });
    }

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

    private void setUpRecyclerView(){
        logView = findViewById(R.id.user_log_RV);
        logView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new UserLogAdapter(UserLogActivity.this, allWorkouts, new UserLogAdapter.WorkoutClickListener() {
            @Override
            public void onWorkoutClick(int position) {
                setUpWorkoutDialog(true, position);
            }
        });
        logView.setAdapter(adapter);
    }

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
                currentWOD.setDifficulty(diff);
                adapter.notifyDataSetChanged();
                FirebaseAPI.updateWorkout(currentWOD);

            });

            view_wod.setNegativeButton("Cancel", (dialogInterface, i) -> {
                dialogInterface.cancel();
            });
        }
        else{
            view_wod.setTitle("Workout");
            View log_layout = getLayoutInflater().inflate(R.layout.dialogue_wod, null);
            TextView wodList = log_layout.findViewById(R.id.wod_list);
            String selectedWOD = allWorkouts.get(pos).getMovements();
            wodList.setText(selectedWOD);
            view_wod.setView(log_layout);
            view_wod.setNegativeButton("Cancel", (dialogInterface, i) -> {
                dialogInterface.cancel();
            });
        }

        view_wod.show();
    }
}