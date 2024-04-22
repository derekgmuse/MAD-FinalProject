package edu.northeastern.wod_calc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter class for handling the a user's workout log.  Each item contains a workout's date logged,
 * Estimated duration, actual duration and difficulty rating.  This class also includes the
 * creation of a view holder object, as well as binding the view holders with data included in
 * UserWorkout objects.
 */
public class UserLogAdapter extends RecyclerView.Adapter<UserLogAdapter.UserLogViewHolder> {
    private final List<UserWorkout> allWorkouts;
    private final LayoutInflater inflater;
    private final WorkoutClickListener workoutClickListener;
    private final WorkoutLongClickListener workoutLongClickListener;

    /**
     * Constructor for creating the adapter with onClick as well as onLongClick functionality.
     * @param context of the activity where layouts will be inflated
     * @param allWorkouts a list of UserWorkouts designated to an authenticated user
     * @param workoutClickListener a listener for click events in the recycler view
     * @param workoutLongClickListener a listener for long click events in the recycler view
     */
    public UserLogAdapter(Context context, List<UserWorkout> allWorkouts, WorkoutClickListener workoutClickListener, WorkoutLongClickListener workoutLongClickListener){
        this.inflater = LayoutInflater.from(context);
        this.allWorkouts = allWorkouts;
        this.workoutClickListener = workoutClickListener;
        this.workoutLongClickListener = workoutLongClickListener;
    }

    /**
     * Used when the recycler view needs a new UserLogViewHolder to represent a user workout in the
     * user log.  This view holder will be used to show items of the adapter through the
     * onBindViewHolder method.
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return a new UserLogViewHolder
     */
    @Override
    public UserLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_workout, parent, false);
        return new UserLogViewHolder(itemView);
    }

    /**
     * Used by the recycler view to show workout data at a specified position in the view.  All
     * TextViews are set according to the workout at the given position in the allWorkouts
     * array list.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull UserLogViewHolder holder, int position) {
        UserWorkout workout = allWorkouts.get(position);
        holder.dateTextView.setText(workout.getDate());
        holder.est_timeTextView.setText(workout.getEstimatedTime());
        holder.real_timeTextView.setText(workout.getActualTime());
        holder.difficultyTextView.setText(workout.getDifficulty());
    }

    /**
     * Returns the total number of items in the workout list of the adapter
     * @return the number of workouts logged by a specific user
     */
    @Override
    public int getItemCount() {
        return allWorkouts.size();
    }

    /**
     * ViewHolder for the log of a user's workouts.  Holds the TextViews for the date a workout
     * was logged, the estimated time of the workout, the actual time it took to complete the workout
     * and the difficulty of the workout.
     */
    public class UserLogViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView, est_timeTextView, real_timeTextView, difficultyTextView;

        /**
         * Initializes a new ViewHolder for a logged workout.  Binds the TextViews to their
         * respective views in the layout.  Sets up the itemView's onClick as well as
         * onLongClick listeners.  Each of which takes in the position of the item and
         * resulting functionality is handled within the UserLogActivity using a callback
         *
         * @param itemView the view of the items layout
         */
        UserLogViewHolder(View itemView){
            super(itemView);
            dateTextView = itemView.findViewById(R.id.date_value);
            est_timeTextView = itemView.findViewById(R.id.est_time_value);
            real_timeTextView = itemView.findViewById(R.id.real_time_value);
            difficultyTextView = itemView.findViewById(R.id.difficulty_value);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (workoutClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            workoutClickListener.onWorkoutClick(position);
                        }
                    }
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(workoutLongClickListener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            workoutLongClickListener.onWorkoutLongClick(position);
                        }
                    }
                    return true;
                }
            });
        }
    }

    /**
     * Interface to set up a callback that will trigger when a user clicks a workout in the log
     */
    public interface WorkoutClickListener {
        void onWorkoutClick(int position);
    }

    /**
     * Interface to set up a callback that will trigger when a user long clicks a workout in the log
     */
    public interface WorkoutLongClickListener {
        void onWorkoutLongClick(int position);
    }
}
