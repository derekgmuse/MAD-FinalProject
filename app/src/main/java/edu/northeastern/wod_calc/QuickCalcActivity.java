package edu.northeastern.wod_calc;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class QuickCalcActivity extends AppCompatActivity {

    private List<SingleMovement> workout = new ArrayList<>();
    private RecyclerView workoutView;
    private CalculatorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_quick_calc);

        setUpRecyclerView();
        setUpAddButton();
        setUpCalculateButton();

    }

    private void setUpRecyclerView() {
        workoutView = findViewById(R.id.workoutRecyclerView);
        workoutView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CalculatorAdapter(QuickCalcActivity.this, workout, new CalculatorAdapter.MovementClickListener() {
            @Override
            public void onMovementClick(int position) {
                Toast.makeText(QuickCalcActivity.this,"Movement: "+ workout.get(position).getName() + " Reps: "+ workout.get(position).getReps(),Toast.LENGTH_SHORT).show();
            }
        });
        workoutView.setAdapter(adapter);
    }

    //custom spinner in dialog resource: https://www.youtube.com/watch?v=nlqtyfshUkc&ab_channel=CodingDemos
    private void setUpAddButton(){
        Button add = findViewById(R.id.button_add);
        //getting the list of movements for the drop down menu
        Movement_Data allMovements = new Movement_Data();
        ArrayList<String> all_mvmt = allMovements.getMovementNames();

        add.setOnClickListener(view -> {
            AlertDialog.Builder enter_mvmt = new AlertDialog.Builder(QuickCalcActivity.this);

            View calc_layout = getLayoutInflater().inflate(R.layout.dialogue_calc, null);

            Spinner select_mvmt = calc_layout.findViewById(R.id.spinner_mvmt);
            ArrayAdapter<String> mvmt_adapter =
                    new ArrayAdapter<String>(
                            QuickCalcActivity.this,
                            android.R.layout.simple_spinner_item,
                            all_mvmt);
            mvmt_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            select_mvmt.setAdapter(mvmt_adapter);

            EditText repText = calc_layout.findViewById(R.id.editText_repetitions);

            enter_mvmt.setView(calc_layout);


            //Setting Enter/Cancel buttons on AlertDialog
            //Set positive button very important here --> need to create/add new
            //item card to the RecyclerView
            enter_mvmt.setPositiveButton("Enter", (dialogInterface, i) -> {

                String name = select_mvmt.getSelectedItem().toString();
                int reps = Integer.parseInt(repText.getText().toString());

                //adding the item to RecyclerView
                workout.add(new SingleMovement(name, reps));
                adapter.notifyDataSetChanged();
            });

            enter_mvmt.setNegativeButton("Cancel", (dialogInterface, i) -> {
                dialogInterface.cancel();
            });

            enter_mvmt.show();

        });
    }

    private void setUpCalculateButton(){
        Button calculate = findViewById(R.id.button_calculate);
        calculate.setOnClickListener(v->{
            Movement_Data MovementData = new Movement_Data();
            double total_time = 0;
            for(SingleMovement mvmt : workout){
                String movement_name = mvmt.getName();
                double movement_reps = mvmt.getReps();
                double reps_per_minute = MovementData.getReps(movement_name); //number of reps in a minute
                double added_time = movement_reps / reps_per_minute;
                total_time += added_time;
            }

            Toast.makeText(QuickCalcActivity.this,"Total time in minutes: " + total_time,Toast.LENGTH_SHORT).show();

        });
    }

}