package edu.northeastern.wod_calc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class UserLogAdapter extends RecyclerView.Adapter<UserLogAdapter.UserLogViewHolder> {

    private final List<UserWorkout> allWorkouts;
    private final LayoutInflater inflater;

    private final WorkoutClickListener workoutClickListener;


    public UserLogAdapter(Context context, List<UserWorkout> allWorkouts, WorkoutClickListener workoutClickListener){
        this.inflater = LayoutInflater.from(context);
        this.allWorkouts = allWorkouts;
        this.workoutClickListener = workoutClickListener;
    }

    @Override
    public UserLogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_workout, parent, false);
        return new UserLogViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserLogViewHolder holder, int position) {
        UserWorkout workout = allWorkouts.get(position);
        holder.dateTextView.setText(workout.getDate());
        holder.est_timeTextView.setText(workout.getEstimatedTime());
        holder.real_timeTextView.setText(workout.getActualTime());
        holder.difficultyTextView.setText(workout.getDifficulty());
    }

    @Override
    public int getItemCount() {
        return allWorkouts.size();
    }

    public class UserLogViewHolder extends RecyclerView.ViewHolder {

        TextView dateTextView, est_timeTextView, real_timeTextView, difficultyTextView;

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
        }
    }

    public interface WorkoutClickListener {
        void onWorkoutClick(int position);
    }
}
