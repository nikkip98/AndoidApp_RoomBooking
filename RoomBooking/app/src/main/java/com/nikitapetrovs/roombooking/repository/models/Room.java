package com.nikitapetrovs.roombooking.repository.models;

public class Room {

    private int id;
    private String description;
    private String coordinates;
    private int floor;
    private int building;

    public Room(int id, String description, String coordinates, int floor, int building) {
        this.id = id;
        this.description = description;
        this.coordinates = coordinates;
        this.floor = floor;
        this.building = building;
    }

    public int getId() {
        return id;
    }

    public String getIdString() {
        return "" + id;
    }

    public String getDescription() {
        return description;
    }

    public String getCoordinates() {
        return coordinates;
    }

    public int getFloor() {
        return floor;
    }

    public int getBuilding() {
        return building;
    }

    @Override
    public String toString() {
        return description;
    }

}
