package com.cloudcomputing.instaclone.util;

//Singleton for app to hold user info
public class InstaCloneApi {
    private String username;
    private String userId;
    private static InstaCloneApi instance;

    public static InstaCloneApi getInstance(){
        if(instance == null)
            instance = new InstaCloneApi();
        return instance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
