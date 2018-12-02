package com.example.bill.assignmentiot;

public class Param {
    private String id;
    private String value;
    private String currentTime;
    public Param(){

    }

    public Param(String paramID, String paramValue, String time) {
        this.id = paramID;
        this.value = paramValue;
        this.currentTime = time;
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
}
