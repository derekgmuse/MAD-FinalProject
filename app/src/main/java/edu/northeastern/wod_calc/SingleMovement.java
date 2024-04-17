package edu.northeastern.wod_calc;

public class SingleMovement {

    private String name;
    private int reps;

    public SingleMovement(String name, int reps){
        this.name = name;
        this.reps = reps;
    }

    public String toString(){
        return reps + " " + name + " ";
    }

    public void setName(String name){
        this.name = name;
    }

    public void setReps(int reps){
        this.reps = reps;
    }

    public String getName(){
        return this.name;
    }

    public int getReps(){
        return this.reps;
    }
}
