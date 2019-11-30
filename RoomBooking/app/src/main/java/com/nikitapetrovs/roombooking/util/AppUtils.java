package com.nikitapetrovs.roombooking.util;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.nikitapetrovs.roombooking.R;
import com.nikitapetrovs.roombooking.repository.models.Building;

public class AppUtils {

    public static PolygonOptions getPolygon(Building building) {
        PolygonOptions p = new PolygonOptions().add(
                stringToCoordinates(building.getCord1())
                , stringToCoordinates(building.getCord2())
                , stringToCoordinates(building.getCord3())
                , stringToCoordinates(building.getCord4()))
                .strokeWidth(7)
                .fillColor(R.color.colorFill)
                .strokeColor(Color.BLACK);
        return p;
    }


    public static LatLng stringToCoordinates(String input) {
        String[] cords = input.split(", ");
        return new LatLng(Double.parseDouble(cords[0]), Double.parseDouble(cords[1]));
    }

    public static String coordinatesToString(LatLng input) {
       return  input.latitude + ", " + input.longitude;
    }

}
