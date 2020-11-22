package com.rishabhrawat.feeleat;

public class staticcoordinates {

    String lat;
    String log;

    public staticcoordinates(String lat, String log) {
        this.lat = lat;
        this.log = log;
    }

    public staticcoordinates() {
    }


    public String getLat() {
        return lat;
    }

    @Override
    public String toString() {
        return "staticcoordinates{" +
                "lat='" + lat + '\'' +
                ", log='" + log + '\'' +
                '}';
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }
}
