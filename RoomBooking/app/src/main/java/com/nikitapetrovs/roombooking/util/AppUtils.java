package com.nikitapetrovs.roombooking.util;

import android.graphics.Color;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.nikitapetrovs.roombooking.R;
import com.nikitapetrovs.roombooking.repository.models.Building;
import com.nikitapetrovs.roombooking.repository.models.Reservation;

import java.util.ArrayList;
import java.util.Collections;

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

    public static ArrayList<Reservation> sortReservations(ArrayList<Reservation> reservations) {

        Collections.sort(reservations, (r1, r2) -> {
            String[] fromTime1 = r1.getTimeFrom().split(":");
            String[] formTime2 = r2.getTimeFrom().split(":");

            int time1 = Integer.parseInt(fromTime1[0]);
            int time2 = Integer.parseInt(formTime2[0]);

            return Integer.compare(time1, time2);
        });

        return reservations;
    }

    public static String getReservationString(ArrayList<Reservation> input) {
        ArrayList<Reservation> reservations = sortReservations(input);
        StringBuilder output = new StringBuilder();

        for (Reservation reservation: reservations) {
            output.append("From: ").append(reservation.timeFrom)
                    .append(" ")
                    .append("To: ").append(reservation.timeTo)
                    .append("\n");
        }

        return output.toString();
    }

}
