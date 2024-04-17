package edu.northeastern.wod_calc;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseAPI {

    private static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    public static void addUserToDatabase(Context context, User user) {
        DatabaseReference usersRef = databaseReference.child("users").child(user.getId());
        usersRef.setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
            } else {
                // Handle failure
                Toast.makeText(context, "Signup failed, please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void addWorkout(UserWorkout newWorkout){

        DatabaseReference workoutsRef = FirebaseDatabase.getInstance().getReference().child("workouts");

        workoutsRef.push().setValue(newWorkout).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //go to the log
            } else {
                // Handle failure
            }
        });
    }

    public static void updateWorkout(UserWorkout updatedWorkout){
        String workoutId = updatedWorkout.getWorkoutId();
        DatabaseReference workoutsRef = FirebaseDatabase.getInstance().getReference("workouts");

        workoutsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot instance : snapshot.getChildren()){
                    String key = instance.getKey();
                    UserWorkout workout = instance.getValue(UserWorkout.class);
                    if(workout.getWorkoutId().equals(workoutId)){
                        workout.setActualTime(updatedWorkout.getActualTime());
                        workout.setDifficulty(updatedWorkout.getDifficulty());
                        workoutsRef.child(key).setValue(workout).addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                //go to the log
                            } else {
                                // Handle failure
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void loadWorkouts(WorkoutDataCallback callback){

        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference workoutsRef = FirebaseDatabase.getInstance().getReference("workouts");

        workoutsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<UserWorkout> workouts = new ArrayList<>();
                for (DataSnapshot instance : snapshot.getChildren()){
                    UserWorkout workout = instance.getValue(UserWorkout.class);
                    if(workout.getUserId().equals(currentUserId)){
                        workouts.add(workout);
                    }
                }
                if (callback != null) {
                    callback.onWorkoutsLoaded(workouts);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public interface WorkoutDataCallback {
        void onWorkoutsLoaded(List<UserWorkout> workouts);
    }


}
