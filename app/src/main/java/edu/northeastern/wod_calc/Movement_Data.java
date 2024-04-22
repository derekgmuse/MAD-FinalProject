package edu.northeastern.wod_calc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Represents data needed for workout duration calculations.  Object includes one hashmap with all
 * Movements included.  The integer value of each movement describes how many repetitions, calories
 * or meters can be completed of the movement within one minute.
 */

public class Movement_Data {

    private HashMap<String, Integer> allMovements;

    /**
     * Constructor for creating a Movement_Data instance such that calculations can be completed
     * to estimate workout durations.  No args constructor as the Hashmap values are set based upon
     * average gym members at the gym I attend.
     */
    public Movement_Data() {
        this.allMovements = new HashMap<>();
        //the integer values represent how many reps, cals or meters can be performed in 1 minute
        allMovements.put("Row (cals)", 20);
        allMovements.put("Row (meters)", 250);
        allMovements.put("Bike Erg (cals)", 20);
        allMovements.put("Bike Erg (meters)", 500);
        allMovements.put("Echo Bike (cals)", 15);
        allMovements.put("Ski (cals)", 15);
        allMovements.put("Ski (meters)", 200);
        allMovements.put("Pull-ups", 10);
        allMovements.put("Push-ups", 20);
        allMovements.put("Squats", 30);
        allMovements.put("Double Under", 50);
        allMovements.put("Single Under", 100);
        allMovements.put("Clean and Jerk", 20);
        allMovements.put("Snatch", 20);
    }

    /**
     * Gets the movement names.
     *
     * @return an alphabetically sorted arraylist of strings with all movement names.  Used for
     * setting up the Spinner to allow for selection of movement during calculation.
     */
    public ArrayList<String> getMovementNames(){
        ArrayList<String> movementNames = new ArrayList<String>(this.allMovements.keySet());
        Collections.sort(movementNames);
        return movementNames;
    }

    /**
     * Returns the number of reps, calories or meters of a specified movement that can be completed
     * in one minute.
     *
     * @param movement the specific movement
     * @return The number of reps/cals/meters
     */
    public int getReps(String movement){
        return this.allMovements.get(movement);
    }
}
