package com.example.worldquiz;

public class Session {
    private String userName;
    private String points;
    private String date;
    public  Session(String username, String date, String points){
        this.userName = username;
        this.points = points;
        this.date = date;
    }
    public String getPoints(){
        return this.points;
    }
    public String getDate(){
        return this.date;
    }
    public String getUserName(){
        return this.userName;
    }
}
