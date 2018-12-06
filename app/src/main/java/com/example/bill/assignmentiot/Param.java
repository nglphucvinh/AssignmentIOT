package com.example.bill.assignmentiot;

public class Param {
    private String id;
    private String value;
    private String currentTime;
    private String type;
    public Param(){

    }

    public Param(String paramID, String paramValue, String time, String type) {
        this.id = paramID;
        this.value = paramValue;
        this.currentTime = time;
        this.type = type;
    }

    public String getID() {
        return id;
    }

    public String getRawValue() {
        return value;
    }

    public String getValue() {
        return value + " %";
    }

    public String getCurrentTime(){
        return currentTime;
    }

    public String getType() {
        return type;
    }


}
