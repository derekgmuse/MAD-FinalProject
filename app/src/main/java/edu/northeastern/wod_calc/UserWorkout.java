package edu.northeastern.wod_calc;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a user's calculated and logged workout.  Each workout has a formatted String of
 * movement names, a difficulty rating out of 10, the date the workout was logged, an estimated
 * time it will take to complete, an actual time it took to complete, the user's id that logged
 * the workout and a unique id for the specific workout.
 */
public class UserWorkout {

    private String movements;
    private String difficulty;
    private String date;
    private String estimated_time;
    private String actual_time;
    private String userId;
    private String workoutId;

    /**
     * Default no args constructor necessary when using Firebase for storing class objects
     */
    public UserWorkout(){}

    /**
     * Constructor used when user's initially log a calculated workout.  As the workout has just
     * been calculated, the difficulty and actual time fields are initialized to "*" before they
     * can be updated within the log by the user.
     * @param movements formatted string of movements that make up the workout
     * @param estimated_time estimated time calculated
     * @param userId the user's id set up by Firebase authentication
     * @param workoutId the workout id set using UUID.randomUUID().toString();
     */
    public UserWorkout(String movements, String estimated_time, String userId, String workoutId){
        this.movements = movements;
        this.difficulty = "*";
        this.date = LocalDate.now().toString();
        this.estimated_time = estimated_time;
        this.actual_time = "*";
        this.userId = userId;
        this.workoutId = workoutId;
    }

    /**
     * toString override to format the UserWorkout for the case when a user clicks a workout within
     * their log of workouts.
     * @return String including movements, difficulty, date, estimated time and actual time
     */
    @Override
    public String toString(){
        return "Movements:\n" + this.getMovements()
                + "\n Difficulty: " + this.getDifficulty()
                + "\n Date: " + this.getDate()
                + "\n Estimated Time: " + this.getEstimatedTime()
                + "\n Actual Time: " + this.getActualTime();
    }

    /**
     * Changes the difficulty of a workout on a scale of 1-10
     * @param difficulty between 1 and 10
     */
    public void changeDifficulty(int difficulty){
        this.difficulty = Integer.toString(difficulty);
    }

    /**
     * Changes the actual time it took to complete the workout
     * @param minutes to complete the workout
     * @param seconds to complete the workout
     */
    public void changeActualTime(int minutes, int seconds){
        String time = minutes + " m " + seconds + " s";
        this.actual_time = time;
    }

    /**
     * Sets the movements
     * @param movements included in a workout
     */
    public void setMovements(String movements){ this.movements = movements;}

    /**
     * Sets the difficulty of a workout
     * @param difficulty between 1 and 10
     */
    public void setDifficulty(String difficulty){
        this.difficulty = difficulty;
    }

    /**
     * Sets the date
     * @param date workout was logged by user
     */
    public void setDate(String date) { this.date = date; }

    /**
     * Sets the estimated time of a workout
     * @param estimated_time to complete workout
     */
    public void setEstimatedTime(String estimated_time) { this.estimated_time = estimated_time; }

    /**
     * Sets the actual time
     * @param actual_time it took a user to complete the workout
     */
    public void setActualTime(String actual_time) { this.actual_time = actual_time; }

    /**
     * Sets the user's id
     * @param userId the user's designated id set by firebase authentication
     */
    public void setUserId(String userId) { this.userId = userId; }

    /**
     * Sets the workout id
     * @param workoutId is the unique id set for a specific workout
     */
    public void setWorkoutId(String workoutId) { this.workoutId = workoutId; }

    /**
     * Gets the movements
     * @return all movements included in the workout
     */
    public String getMovements(){
        return this.movements;
    }

    /**
     * Gets the difficulty
     * @return difficulty of the workout between 1 and 10
     */
    public String getDifficulty(){
        return this.difficulty;
    }

    /**
     * Gets the date
     * @return the date the workout was logged
     */
    public String getDate() { return this.date; }

    /**
     * Gets the estimated time
     * @return calculated time it will take to complete the workout
     */
    public String getEstimatedTime() { return this.estimated_time; }

    /**
     * Gets the actual time
     * @return user entered time it took to complete the workout
     */
    public String getActualTime() { return this.actual_time; }

    /**
     * Gets the user is
     * @return the user's id who logged the workout
     */
    public String getUserId() { return this.userId; }

    /**
     * Gets the workout id
     * @return the unique id of a workout
     */
    public String getWorkoutId() { return this.workoutId; }

}
