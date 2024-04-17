package edu.northeastern.wod_calc;

import java.time.LocalDate;
import java.util.List;

public class UserWorkout {

    private String movements;
    private String difficulty;
    private String date;
    private String estimated_time;
    private String actual_time;
    private String userId;

    private String workoutId;

    public UserWorkout(){}
    public UserWorkout(String movements, String estimated_time, String userId, String workoutId){
        this.movements = movements;
        this.difficulty = "*";
        this.date = LocalDate.now().toString();
        this.estimated_time = estimated_time;
        this.actual_time = "*";
        this.userId = userId;
        this.workoutId = workoutId;
    }

    @Override
    public String toString(){
        return "Movements: " + this.getMovements()
                + "\n Difficulty: " + this.getDifficulty()
                + "\n Date: " + this.getDate()
                + "\n Estimated Time: " + this.getEstimatedTime()
                + "\n Actual Time: " + this.getActualTime()
                + "\n UserID: " + this.getUserId();
    }

    public void changeDifficulty(int difficulty){
        this.difficulty = Integer.toString(difficulty);
    }
    public void changeActualTime(int minutes, int seconds){
        String time = minutes + " m " + seconds + " s";
        this.actual_time = time;
    }
    public void setMovements(String movements){ this.movements = movements;}

    public void setDifficulty(String difficulty){
        this.difficulty = difficulty;
    }

    public void setDate(String date) { this.date = date; }

    public void setEstimatedTime(String estimated_time) { this.estimated_time = estimated_time; }

    public void setActualTime(String actual_time) { this.actual_time = actual_time; }
    public void setUserId(String userId) { this.userId = userId; }

    public void setWorkoutId(String workoutId) { this.workoutId = workoutId; }

    public String getMovements(){
        return this.movements;
    }

    public String getDifficulty(){
        return this.difficulty;
    }

    public String getDate() { return this.date; }

    public String getEstimatedTime() { return this.estimated_time; }

    public String getActualTime() { return this.actual_time; }
    public String getUserId() { return this.userId; }
    public String getWorkoutId() { return this.workoutId; }

}
