package com.nikitapetrovs.roombooking.repository;

import android.os.AsyncTask;

import com.nikitapetrovs.roombooking.repository.models.Building;
import com.nikitapetrovs.roombooking.repository.models.Room;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class BuildingRepository extends AsyncTask<String, Void, String> {

    private ArrayList<Building> buildings = new ArrayList<>();

    public interface AsyncResponse {
        void getBuildings(ArrayList<Building> output);
    }

    private AsyncResponse delegate;

    public BuildingRepository(AsyncResponse delegate) {
        this.delegate = delegate;
    }

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
                    String description = jsonobject.getString("description");
                    String center = jsonobject.getString("centercoordinates");
                    String cord1 = jsonobject.getString("cord1");
                    String cord2 = jsonobject.getString("cord2");
                    String cord3 = jsonobject.getString("cord3");
                    String cord4 = jsonobject.getString("cord4");
                    int floors = jsonobject.getInt("floors");

                    buildings.add(new Building(id, description, center, cord1, cord2, cord3, cord4, floors));

                    output.append(description).append("\n");
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
        delegate.getBuildings(buildings);
    }
}
