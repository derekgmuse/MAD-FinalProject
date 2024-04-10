package edu.northeastern.wod_calc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Movement_Data {

    private HashMap<String, Integer> allMovements;

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

    public ArrayList<String> getMovementNames(){
        ArrayList<String> movementNames = new ArrayList<String>(this.allMovements.keySet());
        Collections.sort(movementNames);
        return movementNames;
    }

    public int getReps(String movement){
        return this.allMovements.get(movement);
    }
}
