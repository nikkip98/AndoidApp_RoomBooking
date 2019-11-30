package com.nikitapetrovs.roombooking.Views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.nikitapetrovs.roombooking.R;
import com.nikitapetrovs.roombooking.Views.pickers.DatePickerFragment;
import com.nikitapetrovs.roombooking.adapters.ReservationAdapter;
import com.nikitapetrovs.roombooking.repository.BuildingRepository;
import com.nikitapetrovs.roombooking.repository.DeleteReservationRepository;
import com.nikitapetrovs.roombooking.repository.ReservationRepository;
import com.nikitapetrovs.roombooking.repository.RoomRepository;
import com.nikitapetrovs.roombooking.repository.models.Building;
import com.nikitapetrovs.roombooking.repository.models.Reservation;
import com.nikitapetrovs.roombooking.repository.models.Room;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DeleteReservationActivity extends AppCompatActivity implements BuildingRepository.AsyncResponse, RoomRepository.AsyncResponse, ReservationAdapter.OnButtonClickedListener, ReservationRepository.AsyncResponse, DatePickerFragment.onDateSetListener {

    private Spinner spinnerBuilding, spinnerFloor, spinnerRoom;
    private ReservationAdapter adapter;
    private EditText date;

    private Building building;
    private Room room;
    private ArrayList<Building> buildings;
    private ArrayList<Room> rooms;
    private String selectedFloor;
    private String selectedRoom;
    private String selectedDate;
    private Reservation reservation;
    private int deletedItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_reservation);

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        selectedDate = day + "/" + (month + 1) + "/" + year;

        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> finish());

        spinnerBuilding = findViewById(R.id.spinnerBuilding);
        spinnerFloor = findViewById(R.id.spinnerFloor);
        spinnerRoom = findViewById(R.id.spinnerRoom);

        date = findViewById(R.id.editTextReservationDate);
        date.setText(selectedDate);
        date.setInputType(InputType.TYPE_NULL);
        date.setOnClickListener(view -> {
            DialogFragment fragment = new DatePickerFragment(false);
            fragment.show(getSupportFragmentManager(), "datePicker");
        });

        new RoomRepository(this).execute("http://student.cs.hioa.no/~s325918/getRooms.php");
        new BuildingRepository(this).execute("http://student.cs.hioa.no/~s325918/getBuildings.php");

        adapter = new ReservationAdapter(this, this);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void getBuildings(ArrayList<Building> output) {
        buildings = output;
        fillBuildingSpinner();
    }

    @Override
    public void getRooms(ArrayList<Room> output) {
        rooms = output;
    }

    @Override
    public void getReservations(String output) {

    }

    @Override
    public void getReservationsArray(ArrayList<Reservation> output) {
        adapter.submitList(output);
        adapter.notifyItemChanged(deletedItem);
    }

    @Override
    public void receiveDate(String string) {
        selectedDate = string;
        date.setText(string);
        updateReservationList();
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
                findRoom();
                updateReservationList();
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

    public void findRoom() {
        for (Room room : rooms) {
            if (room.getDescription().equals(selectedRoom)) {
                this.room = room;
            }
        }
    }

    private void deleteReservation() {
        String toUrl = "http://student.cs.hioa.no/~s325918/deleteReservation.php/?id[0]=" + reservation.getId();

        final String newUrl = toUrl.replaceAll(" ", "%20");

        new Thread(() -> {
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
                updateReservationList();
            } catch (Exception e) {
                Log.d("Adding", "submitReservation: Something went wrong" + e);
            }
        }).start();
    }

    public void updateReservationList() {
        String id = "" + room.getId();
        new ReservationRepository(this).execute("http://student.cs.hioa.no/~s325918/getReservations.php/", id, selectedDate);
    }

    @Override
    public void onButtonClicked(int id) {
        reservation = adapter.getReservationAt(id);
        deletedItem = id;

        new AlertDialog.Builder(this)
                .setTitle("Choose Action")
                .setMessage("Do you want to delete reservation?")
                .setPositiveButton("ok", (dialogInterface, i) -> {
                    deleteReservation();

                })
                .setNegativeButton("cancel", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
    }



}
