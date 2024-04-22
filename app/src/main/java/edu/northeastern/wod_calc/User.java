package edu.northeastern.wod_calc;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a user within the application.  Each user has a user id set up using Firebase
 * authentication as well as a username that will be displayed in the UserCalc and UserLog
 * activities.
 */

public class User {

    private String id;
    private String username;

    /**
     * Default no args constructor necessary when using Firebase for storing class objects
     */
    public User(){}

    /**
     * Constructor for creating a User object with id and username
     * @param id the user's id set by Firebase
     * @param username the user's chosen username
     */
    public User(String id, String username){
        this.id = id;
        this.username = username;
    }

    /**
     * Gets the id of the user
     * @return user's id
     */
    public String getId(){
        return this.id;
    }

    /**
     * Gets the username of the user
     * @return user's username
     */
    public String getUsername(){
        return this.username;
    }

    /**
     * Sets the user's id
     * @param id user's id
     */
    public void setId(String id){
        this.id = id;
    }

    /**
     * Sets the user's username
     * @param username user's username
     */
    public void setUsername(String username){
        this.username = username;
    }
}
