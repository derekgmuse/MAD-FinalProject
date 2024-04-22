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
 * Adapter class for handling the calculator functionality.  User of the app is able to add movements
 * and repetitions to a workout before calculating.  Each item contains a movement's name
 * as well as the specified number of reps/calories/meters to complete.  This class also includes the
 * creation of a view holder object, as well as binding the view holders with data included in
 * SingleMovement objects.
 */
public class CalculatorAdapter extends RecyclerView.Adapter<CalculatorAdapter.CalculatorViewHolder>{

    private final List<SingleMovement> workout;
    private final LayoutInflater inflater;
    private final MovementClickListener movementClickListener;

    /**
     * Constructor for creating the adapter with onClick functionality
     * @param context of the activity where the layouts will be inflated
     * @param workout a list of SingleMovements making up a workout
     * @param movementClickListener a lister for click events in the recycler view
     */
    public CalculatorAdapter(Context context, List<SingleMovement> workout, MovementClickListener movementClickListener){
        this.inflater = LayoutInflater.from(context);
        this.workout = workout;
        this.movementClickListener = movementClickListener;
    }

    /**
     * Used when the recycler view needs a new CalculatorViewHolder to represent a single movement
     * in the workout calculator.  This view holder will be used to show items of the adapter through
     * the onBindViewHolder method
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to
     *               an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return a new CalculatorViewHolder
     */
    @Override
    public CalculatorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_movement, parent, false);
        return new CalculatorViewHolder(itemView);
    }

    /**
     * Used by the recycler view to show single movement information at a specific position
     * in the view.  TextViews are set according to the SingleMovement at the specified position
     * in the workouts array list.
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the
     *        item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull CalculatorViewHolder holder, int position) {
        SingleMovement movement = workout.get(position);
        holder.nameTextView.setText(movement.getName());
        holder.repsTextView.setText(String.valueOf(movement.getReps()));
    }

    /**
     * Returns the total number of items in the workout list of SingleMovements
     * @return the number of movements in a workout
     */
    @Override
    public int getItemCount() {
        return workout.size();
    }

    /**
     * ViewHolder for the calculator view of movements.  Holds the TextViews for the name of the
     * movement as well as the number of reps/cals/meters to be completed.  Also include the
     * SingleMovement object for use within the Calculator activities.
     */
    public class CalculatorViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView, repsTextView;
        SingleMovement movement;

        /**
         * Initializes a new ViewHolder for a SingleMovement.  Binds the TextViews to their
         * respective views in the layout.  Sets up the onClick listener that utilizes a callback
         * taking in the position of the item and utilizing it within the activity to
         * edit an item in the recycler view.
         *
         * @param itemView the view of the items layout
         */
        CalculatorViewHolder(View itemView){
            super(itemView);
            nameTextView = itemView.findViewById(R.id.movement_value);
            repsTextView = itemView.findViewById(R.id.reps_value);

            String name = nameTextView.getText().toString();
            int reps = Integer.parseInt(repsTextView.getText().toString());
            movement = new SingleMovement(name, reps);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (movementClickListener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            movementClickListener.onMovementClick(position);
                        }
                    }
                }
            });
        }
    }

    /**
     * Interface to set up a callback that will trigger whena  user clicks a movement
     * in the recycler view.
     */
    public interface MovementClickListener {
        void onMovementClick(int position);
    }
}
