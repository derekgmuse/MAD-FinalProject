package edu.northeastern.wod_calc;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String id;
    private String username;


    public User(){}
    public User(String id, String username){
        this.id = id;
        this.username = username;
    }

    //only need getters here because all information is set on instantiation of User

    public String getId(){
        return this.id;
    }
    public String getUsername(){
        return this.username;
    }

}
