package edu.northeastern.wod_calc;

/**
 * Represents a movement and associated number of reps/calories/meters that have to be completed.
 * UserWorkouts are made up of SingleMovement objects entered by the user.
 */

public class SingleMovement {

    private String name;
    private int reps;

    /**
     * Constructor for creating a SingleMovement instance with name and reps.
     *
     * @param name the name of the movement as selected by user from drop down Spinner
     * @param reps the number of reps/cals/meters designated by the user
     */
    public SingleMovement(String name, int reps){
        this.name = name;
        this.reps = reps;
    }

    /**
     * toString method to allow for setting text within the application
     * @return a String with both the number of reps and name of movement
     */
    public String toString(){
        return reps + " " + name + " ";
    }

    /**
     * Sets the name of the movement
     * @param name the movement name
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     * Sets the number of reps to be completed
     * @param reps number of reps/cals/meters
     */
    public void setReps(int reps){
        this.reps = reps;
    }

    /**
     * Gets the name of the movement
     * @return movement name
     */
    public String getName(){
        return this.name;
    }

    /**
     * Gets the number of reps
     * @return reps/cals/meters of current movement
     */
    public int getReps(){
        return this.reps;
    }
}
