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
import java.util.List;

public class DeleteReservationActivity extends AppCompatActivity implements BuildingRepository.AsyncResponse, RoomRepository.AsyncResponse{

    private Spinner spinnerBuilding, spinnerFloor, spinnerRoom;
    private Button submitButton;

    private Building building;
    private Room room;
    private ArrayList<Building> buildings;
    private ArrayList<Room> rooms;
    private String selectedFloor;
    private String selectedRoom;

    private boolean deleted;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_room);

        final Context context = this;

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> finish());

        spinnerBuilding = findViewById(R.id.spinnerBuilding);
        spinnerFloor = findViewById(R.id.spinnerFloor);
        spinnerRoom = findViewById(R.id.spinnerRoom);
        submitButton = findViewById(R.id.submitButton);

        new RoomRepository(this).execute("http://student.cs.hioa.no/~s325918/getRooms.php");
        new BuildingRepository(this).execute("http://student.cs.hioa.no/~s325918/getBuildings.php");

        submitButton.setOnClickListener(view -> new AlertDialog.Builder(context)
                .setTitle("Confirm Action")
                .setMessage("Do you want to delete selected Room?\nAll reservations will be deleted with it!")
                .setPositiveButton("ok", (dialogInterface, i) -> delete())
                .setNegativeButton("cancel", (dialogInterface, i) -> dialogInterface.dismiss()).create().show());
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
                TextView selected = ((TextView) adapterView.getChildAt(0));
                selected.setTextSize(20);
                selected.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                String string = adapterView.getItemAtPosition(i).toString();
                building = findBuilding(string);

                fillFloorSpinner();
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

    public void fillFloorSpinner() {

        List<Integer> flrs = new ArrayList<>();
        int numberOfFloors = building.getFloors();

        for (int x = 0; x < numberOfFloors; x++) {
            flrs.add(x + 1);
        }

        ArrayAdapter<Integer> floors = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, flrs);
        floors.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFloor.setAdapter(floors);
        spinnerFloor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView selected = ((TextView) adapterView.getChildAt(0));
                selected.setTextSize(20);
                selected.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                selectedFloor = adapterView.getItemAtPosition(i).toString();
                fillRoomSpinner();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void getRooms(ArrayList<Room> output) {
        rooms = output;
        if(deleted) {
            fillRoomSpinner();
            deleted = false;
        }
    }

    public void fillRoomSpinner() {
        int floor = Integer.parseInt(selectedFloor);
        Building building = findBuilding(spinnerBuilding.getSelectedItem().toString());

        List<Room> selectedRooms = new ArrayList<>();
        for (Room room : rooms) {
            if (room.getFloor() == floor && room.getBuilding() == building.getId()) {
                selectedRooms.add(room);
            }
        }

        ArrayAdapter<Room> adapter = new ArrayAdapter<>(this, R.layout.spinner_layout, selectedRooms);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRoom.setAdapter(adapter);
        spinnerRoom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView selected = ((TextView) adapterView.getChildAt(0));
                selected.setTextSize(20);
                selected.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                selectedRoom = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    public void findRoom() {
        for (Room room : rooms) {
            if (room.getDescription().equals(selectedRoom)) {
                this.room = room;
            }
        }
    }

    /**
     * Starts deleting process
     */
    private void delete() {
        findRoom();
        deleteReservations();
    }

    private void deleteReservations() {
        DeleteReservationRepository rep = new DeleteReservationRepository(room.getId());
        ArrayList<Reservation>  reservations = rep.getReservations();
        String toUrl = "http://student.cs.hioa.no/~s325918/deleteReservation.php/";
        System.out.println(reservations.size());
        for (int x = 0; x < reservations.size(); x++) {
            if (x == 0) {
                toUrl = toUrl + "?id" + "[" + x + "]" + "=" + +reservations.get(x).getId();
            } else {
                toUrl = toUrl + "&id" + "[" + x + "]" + "=" + reservations.get(x).getId();
            }

        }

        final String newUrl = toUrl.replaceAll(" ", "%20");

        System.out.println(newUrl);

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
                    deleteRoom();
                } catch (Exception e) {
                    Log.d("Adding", "submitReservation: Something went wrong" + e);
                }
            }

        }).start();
    }

    private void deleteRoom() {

        final String toUrl = "http://student.cs.hioa.no/~s325918/deleteRoom.php/" +
                "?id[0]=" + room.getId();

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
                    updateRoomSpinner();
                } catch (Exception e) {
                    Log.d("Adding", "submitReservation: Something went wrong" + e);
                }
            }

        }).start();
    }

    private void updateRoomSpinner() {
        new RoomRepository(this).execute("http://student.cs.hioa.no/~s325918/getRooms.php");
        deleted = true;
    }


}
