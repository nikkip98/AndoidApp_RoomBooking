package com.nikitapetrovs.roombooking.repository.models;

public class Reservation {

    public int id;
    public String date;
    public String timeFrom;
    public String timeTo;
    public int roomID;

    public Reservation(int id, String date, String timeFrom, String timeTo, int roomID) {
        this.id = id;
        this.date = date;
        this.timeFrom = timeFrom;
        this.timeTo = timeTo;
        this.roomID = roomID;
    }


    public String getDate() {
        return date;
    }

    public String getTimeFrom() {
        return timeFrom;
    }

    public String getTimeTo() {
        return timeTo;
    }

    public int getId() {
        return id;
    }
}
