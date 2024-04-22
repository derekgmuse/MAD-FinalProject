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

/**
 * This class includes methods used to manage the Firebase Realtime Database.  The database is set
 * up with two fields: users and workouts.  Each object within these fields contains characteristics
 * set up by the {@link User} and {@link UserWorkout} classes.  This class handles the adding of
 * users and workouts, as well as updating and deleting workouts from the database.  The loading of
 * workouts to the {@link UserLogActivity} is also handled here.
 */

public class FirebaseAPI {

    private static DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    /**
     * Adds a user to the database under the "users" field using the setValue() firebase method.
     * This method is used when a new user signs up for the app.  They will be sent to the login
     * page after sign up is complete.
     * @param context The context of the current activity, so an intent can be set to send the user
     *                to the LoginActivity
     * @param user The user object that will be entered into Firebase including username and user id
     */
    public static void addUserToDatabase(Context context, User user) {
        DatabaseReference usersRef = databaseReference.child("users").child(user.getId());
        usersRef.setValue(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "Signup failed, please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Adds a new workout to the database under the "workouts" field using the setValue() method.
     * This method will be used when a user calculates a workout and decides to log this workout.
     * @param newWorkout The UserWorkout object added to the database including workout details
     *                   as well as workout and user id, so a user's workouts can be loaded up
     *                   into a workout log or deleted from a workout log.
     */
    public static void addWorkout(UserWorkout newWorkout){
        DatabaseReference workoutsRef = databaseReference.child("workouts");
        workoutsRef.push().setValue(newWorkout).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d("add workout", "Workout Added successfully");
            } else {
                Log.d("add workout", "Unsuccessful in adding workout");
            }
        });
    }

    /**
     * Deletes a workout from a user's workout log.  Utilizes the addListenerForSingleValueEvent
     * to gather all workouts under the "workouts" field.  Then iterates through all of the workouts
     * looking for a the workout id of the workout to be deleted.  When we find it, we use the key
     * of the workout to identify the workout that will be deleted and remove the value from
     * firebase.  This method is used when a User deletes a workout from their workout log.
     * @param deletedWorkout the UserWorkout that will be deleted from the Firebase database.
     */
    public static void deleteWorkout(UserWorkout deletedWorkout){
        String workoutId = deletedWorkout.getWorkoutId();
        DatabaseReference workoutsRef = FirebaseDatabase.getInstance().getReference("workouts");
        workoutsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot instance : snapshot.getChildren()){
                    String key = instance.getKey();
                    UserWorkout workout = instance.getValue(UserWorkout.class);
                    if(workout.getWorkoutId().equals(workoutId)){
                        workoutsRef.child(key).removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Log.d("deleted", "workout deleted!");
                            } else {
                                Log.d("deleted", "Unsuccessful in deleting workout");
                            }
                        });
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Firebase", "Value event listener cancelled for deleting workout");
            }
        });
    }

    /**
     * Updates a Workout to include the actual time it took to complete as well as the difficulty.
     * Utilizes the addListenerForSingleValueEvent to gather all workouts under the "workouts" field.
     * Iterates through these workouts, when the specific workout is identified, we use the setters
     * for the UserWorkout class to set the actual time and difficulty before updating the workout
     * in firebase with the setValue() method.
     * @param updatedWorkout UserWorkout object representing an updated version of an existing
     *                       workout in Firebase.  This workout will have the same workoutId as the
     *                       workout we are looking to update in Firebase.
     */
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
                                Log.d("updated", "workout updated!");
                            } else {
                                Log.d("updated", "unable to update workout");
                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("Firebase", "Value event listener cancelled for updating workout");
            }
        });
    }

    /**
     * Utilizes the addListenerForSingleValueEvent to gather all workouts under the "workouts" field.
     * Iterates through the workouts and adds each workout with the currently authenticated user's id
     * to an arraylist of UserWorkouts.  Sends this workout back to the UserLogActivity through the
     * WorkoutDataCallback.
     * @param callback WorkoutDataCallback interface used to load workouts asynchronously within the
     *                 UserLogActivity class
     */
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
                Log.d("Firebase", "Value event listener cancelled for loading workouts");
            }
        });
    }

    /**
     * Callback interface for loading workout data into a User's log.
     */
    public interface WorkoutDataCallback {
        void onWorkoutsLoaded(List<UserWorkout> workouts);
    }
}
