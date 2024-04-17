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

public class UserCalcActivity extends AppCompatActivity {

    private List<SingleMovement> workout = new ArrayList<>();
    private RecyclerView workoutView;
    private CalculatorAdapter adapter;
    private Movement_Data allMovements;
    private ArrayList<String> all_mvmt;

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

    private void setUpToolBar(){
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userID);
        DatabaseReference usernameRef = userRef.child("username");

        usernameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String username = dataSnapshot.getValue().toString();
                TextView title = findViewById(R.id.userCalc_title);
                title.setText("Welcome " + username);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        Button log = findViewById(R.id.button_log);

        log.setOnClickListener(view -> {
            Intent intent = new Intent(UserCalcActivity.this, UserLogActivity.class);
            startActivity(intent);
        });
    }

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

        //adding onSwipe to delete functionality using ItemTouchHelper
        //source: https://www.geeksforgeeks.org/swipe-to-delete-and-undo-in-android-recyclerview/#
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

    private void setUpAddButton(){
        Button add = findViewById(R.id.button_add_mvmt);

        add.setOnClickListener(view -> {
            setUpSelectDialog(true, 0);
        });
    }

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

    private String wodToString(){
        String result = "[";
        for(SingleMovement wod : workout){
            result += wod.toString();
        }
        result += "]";
        return result;
    }

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