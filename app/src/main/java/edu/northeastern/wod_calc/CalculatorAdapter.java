package edu.northeastern.wod_calc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CalculatorAdapter extends RecyclerView.Adapter<CalculatorAdapter.CalculatorViewHolder>{

    private final List<SingleMovement> workout;
    private final LayoutInflater inflater;
    private final MovementClickListener movementClickListener;

    public CalculatorAdapter(Context context, List<SingleMovement> workout, MovementClickListener movementClickListener){
        this.inflater = LayoutInflater.from(context);
        this.workout = workout;
        this.movementClickListener = movementClickListener;
    }

    @Override
    public CalculatorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.item_movement, parent, false);
        return new CalculatorViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CalculatorViewHolder holder, int position) {
        SingleMovement movement = workout.get(position);
        holder.nameTextView.setText(movement.getName());
        holder.repsTextView.setText(String.valueOf(movement.getReps()));
    }

    @Override
    public int getItemCount() {
        return workout.size();
    }


    public class CalculatorViewHolder extends RecyclerView.ViewHolder {

        TextView nameTextView, repsTextView;

        SingleMovement movement;

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

    public interface MovementClickListener {
        void onMovementClick(int position);
    }
}
