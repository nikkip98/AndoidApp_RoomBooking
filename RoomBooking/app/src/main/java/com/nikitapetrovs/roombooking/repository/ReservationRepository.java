package com.nikitapetrovs.roombooking.repository;

import android.os.AsyncTask;

import com.nikitapetrovs.roombooking.repository.models.Reservation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;


public class ReservationRepository extends AsyncTask<String, Void, String> {

    String reservationsString = "";
    ArrayList<Reservation> reservations = new ArrayList<>();

    public interface AsyncResponse {
        void getReservations(String output);
        void getReservationsArray(ArrayList<Reservation> output);
    }

    private AsyncResponse delegate;

    public ReservationRepository(AsyncResponse delegate) {
        this.delegate = delegate;
    }

    @Override
    protected String doInBackground(String... urls) {
        StringBuilder output = new StringBuilder(); //output
        StringBuilder requestResponse = new StringBuilder(); //input

        int currRoomID = Integer.parseInt(urls[1]);

        String readerLine;

        try {
            //Make connection
            URL url = new URL(urls[0]);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            //Check status
            int status = connection.getResponseCode();
            if (status != 200) {
                throw new RuntimeException("Failed : HTTP error code : " +
                        connection.getResponseCode());
            }

            //Get input stream
            BufferedReader reader = new BufferedReader(new InputStreamReader((connection.getInputStream())));

            //Make string from input-stream
            while ((readerLine = reader.readLine()) != null) {
                requestResponse.append(readerLine);
            }

            //release connection
            connection.disconnect();

            //Create output string form input string
            try {
                JSONArray jsonArray = new JSONArray(requestResponse.toString());


                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonobject = jsonArray.getJSONObject(i);

                    Calendar cal = Calendar.getInstance();
                    int year = cal.get(Calendar.YEAR);
                    int month = cal.get(Calendar.MONTH);
                    int day = cal.get(Calendar.DAY_OF_MONTH);
                    String todaysDate = day + "/" + (month + 1) + "/" + year;

                    int id = jsonobject.getInt("id");
                    String reservationDate = jsonobject.getString("date");
                    String timeFrom = jsonobject.getString("timeFrom");
                    String timeTo = jsonobject.getString("timeTo");
                    int roomID = jsonobject.getInt("roomID");

                    if (currRoomID == roomID && todaysDate.equals(reservationDate)) {

                        reservations.add(new Reservation(id, reservationDate, timeFrom, timeTo, roomID));

                        output.append("From: ").append(timeFrom)
                                .append(" ")
                                .append("To: ").append(timeTo)
                                .append("\n");
                    }

                }
                reservationsString = output.toString();
                return output.toString();

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return output.toString();

        } catch (Exception e) {
            return "Something went wrong";
        }
    }

    @Override
    protected void onPostExecute(String ss) {
        delegate.getReservations(reservationsString);
        delegate.getReservationsArray(reservations);
    }
}


