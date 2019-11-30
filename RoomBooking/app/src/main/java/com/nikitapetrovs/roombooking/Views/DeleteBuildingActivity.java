package com.nikitapetrovs.roombooking.Views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nikitapetrovs.roombooking.R;
import com.nikitapetrovs.roombooking.repository.BuildingRepository;
import com.nikitapetrovs.roombooking.repository.DeleteReservationRepository;
import com.nikitapetrovs.roombooking.repository.RoomRepository;
import com.nikitapetrovs.roombooking.repository.models.Building;
import com.nikitapetrovs.roombooking.repository.models.Reservation;
import com.nikitapetrovs.roombooking.repository.models.Room;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DeleteBuildingActivity extends AppCompatActivity implements BuildingRepository.AsyncResponse, RoomRepository.AsyncResponse {

    private Spinner spinnerBuilding;

    private Building building;
    private ArrayList<Building> buildings;
    private ArrayList<Room> rooms;
    private Button submitButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_building);

        final Context context = this;

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        spinnerBuilding = findViewById(R.id.spinnerBuilding);
        submitButton = findViewById(R.id.submitButton);

        new BuildingRepository(this).execute("http://student.cs.hioa.no/~s325918/getBuildings.php");
        new RoomRepository(this).execute("http://student.cs.hioa.no/~s325918/getRooms.php");

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Confirm Action")
                        .setIcon(R.drawable.ic_warning_red_24dp)
                        .setMessage("Do you want to delete selected building?\nAll rooms and reservations will be deleted with it!")
                        .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                delete();
                            }
                        })
                        .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();
            }
        });
    }

    @Override
    public void getBuildings(ArrayList<Building> output) {
        buildings = output;
        fillBuildingSpinner();
    }

    public void fillBuildingSpinner() {
        ArrayAdapter<Building> adapter = new ArrayAdapter<>(this, R.layout.spinner_layout, buildings);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerBuilding.setAdapter(adapter);
        spinnerBuilding.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextSize(18);
                String string = adapterView.getItemAtPosition(i).toString();
                building = findBuilding(string);
                System.out.println(string);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public Building findBuilding(String input) {
        for (Building building : buildings) {
            if (building.getDescription().equals(input)) {
                return building;
            }
        }
        return null;
    }

    @Override
    public void getRooms(ArrayList<Room> output) {
        rooms = output;
    }

    private void delete() {
        deleteRoomReservations();
        new Thread(new Runnable() {
            @Override
            public void run() {
                deleteRooms();
                deleteBuilding();
                updateBuildingSpinner();
            }
        }).start();


    }

    private void deleteBuilding() {
        //Delete building
        final String toUrl = "http://student.cs.hioa.no/~s325918/deleteBuilding.php/" +
                "?id=" + building.getId();

        final String newUrl = toUrl.replaceAll(" ", "%20");

        System.out.println(newUrl);

        try {
            URL url = new URL(newUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");

            int status = connection.getResponseCode();
            if (status != 200) {
                throw new RuntimeException("Failed : HTTP error code : " +
                        connection.getResponseCode());
            }

            connection.disconnect();

        } catch (Exception e) {
            Log.d("Adding", "submitReservation: Something went wrong" + e);
        }
    }

    private void deleteRooms() {
        String toUrl = "http://student.cs.hioa.no/~s325918/deleteRoom.php/";

        ArrayList<Room> roomsToDelete = new ArrayList<>();

        for (Room room : rooms) {
            if (room.getBuilding() == this.building.getId()) {
               roomsToDelete.add(room);
            }
        }

        for (int x = 0; x < roomsToDelete.size(); x++) {
                if (x == 0) {
                    toUrl = toUrl + "?id" + "[" + x + "]" + "=" + roomsToDelete.get(x).getId();
                } else {
                    toUrl = toUrl + "&id" + "[" + x + "]" + "=" + roomsToDelete.get(x).getId();
                }

        }

        final String newUrl = toUrl.replaceAll(" ", "%20");

        System.out.println(newUrl);

        try {
            URL url = new URL(newUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Accept", "application/json");

            int status = connection.getResponseCode();
            if (status != 200) {
                throw new RuntimeException("Failed : HTTP error code : " +
                        connection.getResponseCode());
            }

            connection.disconnect();

        } catch (Exception e) {
            Log.d("Adding", "submitReservation: Something went wrong" + e);
        }
    }

    private void deleteRoomReservations() {
        for (Room room : rooms) {
            if (room.getBuilding() == building.getId()) {
                DeleteReservationRepository rep = new DeleteReservationRepository(room.getId());
                deleteReservations(rep.getReservations());
            }
        }
    }


    private void deleteReservations(ArrayList<Reservation> reservations) {
        String toUrl = "http://student.cs.hioa.no/~s325918/deleteReservation.php/";

        for (int x = 0; x < reservations.size(); x++) {
            if (x == 0) {
                toUrl = toUrl + "?id" + "[" + x + "]" + "=" + reservations.get(x).getId();
            } else {
                toUrl = toUrl + "&id" + "[" + x + "]" + "=" + reservations.get(x).getId();
            }

        }

        final String newUrl = toUrl.replaceAll(" ", "%20");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(newUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Accept", "application/json");

                    int status = connection.getResponseCode();
                    if (status != 200) {
                        throw new RuntimeException("Failed : HTTP error code : " +
                                connection.getResponseCode());
                    }

                    connection.disconnect();

                } catch (Exception e) {
                    Log.d("Adding", "submitReservation: Something went wrong" + e);
                }
            }

        }).start();
    }

    private void updateBuildingSpinner() {
        new BuildingRepository(this).execute("http://student.cs.hioa.no/~s325918/getBuildings.php");
    }
}
