package com.example.bill.assignmentiot;

public class Param {
    private String id;
    private String value;
    private String currentTime;
    private String name;
    public Param(){

    }

    public Param(String paramID, String paramValue, String time, String name) {
        this.id = paramID;
        this.value = paramValue;
        this.currentTime = time;
        this.name = name;
    }

    public String getID() {
        return id;
    }

    public String getValue() {
        return value + " %";
    }

    public String getCurrentTime(){
        return currentTime;
    }

    public String getName() {
        return name;
    }
}
