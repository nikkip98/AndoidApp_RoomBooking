package com.nikitapetrovs.roombooking.repository.models;

public class Building {

    private int id;
    private String description;
    private String centerCoordinates;
    private String cord1;
    private String cord2;
    private String cord3;
    private String cord4;
    private int floors;

    public Building(int id, String description, String centerCoordinates, String cord1, String cord2, String cord3, String cord4, int floors) {
        this.id = id;
        this.description = description;
        this.centerCoordinates = centerCoordinates;
        this.cord1 = cord1;
        this.cord2 = cord2;
        this.cord3 = cord3;
        this.cord4 = cord4;
        this.floors = floors;
    }

    public int getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getCenterCoordinates() {
        return centerCoordinates;
    }

    public String getCord1() {
        return cord1;
    }

    public String getCord2() {
        return cord2;
    }

    public String getCord3() {
        return cord3;
    }

    public String getCord4() {
        return cord4;
    }

    public int getFloors() {
        return floors;
    }

    @Override
    public String toString() {
        return description;
    }
}
