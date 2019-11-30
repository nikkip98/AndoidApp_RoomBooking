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
import java.util.concurrent.ExecutionException;

public class DeleteReservationRepository {

    private ArrayList<Reservation> reservations;
    private int currRoomID;

    public DeleteReservationRepository(int roomID) {
        this.reservations = new ArrayList<>();
        this.currRoomID = roomID;
    }

    public ArrayList<Reservation> getReservations() {
        GetJSON task = new GetJSON();
        try {
            task.execute("http://student.cs.hioa.no/~s325918/getReservations.php").get();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("Task was interrupted");
        }

        return reservations;
    }

    public class GetJSON extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            StringBuilder output = new StringBuilder(); //output
            StringBuilder requestResponse = new StringBuilder(); //input

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

                        int id = jsonobject.getInt("id");
                        String reservationDate = jsonobject.getString("date");
                        String timeFrom = jsonobject.getString("timeFrom");
                        String timeTo = jsonobject.getString("timeTo");
                        int roomID = jsonobject.getInt("roomID");

                        if(currRoomID == roomID) {
                            reservations.add(new Reservation(id, reservationDate, timeFrom, timeTo, roomID));
                        }

                    }
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

        }
    }

}
