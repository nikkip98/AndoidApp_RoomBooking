package com.nikitapetrovs.roombooking.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nikitapetrovs.roombooking.R;
import com.nikitapetrovs.roombooking.repository.BuildingRepository;
import com.nikitapetrovs.roombooking.repository.models.Building;
import com.nikitapetrovs.roombooking.util.AppUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class AddRoomActivity extends AppCompatActivity implements BuildingRepository.AsyncResponse, OnMapReadyCallback, GoogleMap.OnMapClickListener {

    private GoogleMap mMap;

    private ImageButton backButton;
    private ImageButton chooseLocation;
    private Button buttonMap;
    private TextView selectedAdress;
    private Button buttonSubmit;
    private Spinner spinnerBuilding, spinnerFloor;
    private androidx.constraintlayout.widget.ConstraintLayout mapLayout;
    private EditText description;
    private TextView coordinates;

    private ArrayList<Building> buildings;
    private String selectedCoordinates;
    private String selectedFloor;
    private Building building;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_room);

        final Activity activity = this;

        description = findViewById(R.id.textDescription);
        coordinates = findViewById(R.id.textCoordinates);
        selectedAdress = findViewById(R.id.textViewSelectedAddress);

        spinnerBuilding = findViewById(R.id.spinnerBuilding);
        spinnerFloor = findViewById(R.id.spinnerFloor);
        buildings = new ArrayList<>();
        new BuildingRepository(this).execute("http://student.cs.hioa.no/~s325918/getBuildings.php");

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> finish());

        mapLayout = findViewById(R.id.mapLayout);

        buttonSubmit = findViewById(R.id.submitButton);
        buttonSubmit.setOnClickListener(view -> checkInput());

        chooseLocation = findViewById(R.id.chooseLocation);
        chooseLocation.setOnClickListener(view -> {
            mapLayout.setVisibility(View.VISIBLE);
            mapLayout.setClickable(true);
            buttonSubmit.setVisibility(View.INVISIBLE);
            hideKeyboard(activity);
        });

        buttonMap = findViewById(R.id.buttonMap);
        buttonMap.setOnClickListener(view -> {
            mapLayout.setVisibility(View.INVISIBLE);
            mapLayout.setClickable(false);
            coordinates.setText(selectedCoordinates);
            buttonSubmit.setVisibility(View.VISIBLE);
        });

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this);
        mMap.getUiSettings().setZoomControlsEnabled(true);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mMap.clear();

        Geocoder geo = new Geocoder(this);
        try {
            Address address = geo.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0);
            selectedAdress.setText(address.getAddressLine(0));
        } catch (Exception e) {
            return;
        }

        selectedCoordinates = AppUtils.coordinatesToString(latLng);
        mMap.addPolygon(AppUtils.getPolygon(building));

        MarkerOptions marker = new MarkerOptions();
        marker.position(latLng);
        marker.title(selectedCoordinates);
        mMap.addMarker(marker);
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
                ((TextView) adapterView.getChildAt(0)).setTextSize(20);

                String string = adapterView.getItemAtPosition(i).toString();
                building = findBuilding(string);
                String cords = building.getCenterCoordinates();


                LatLng newLoc = AppUtils.stringToCoordinates(cords);
                mMap.addPolygon(AppUtils.getPolygon(building));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLoc, 18));

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

        ArrayAdapter<Integer> floors = new ArrayAdapter<>(this, R.layout.spinner_layout, flrs);
        floors.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFloor.setAdapter(floors);
        spinnerFloor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                TextView selected = ((TextView) adapterView.getChildAt(0));
                selected.setTextSize(20);
                selected.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                selectedFloor = adapterView.getItemAtPosition(i).toString();
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

    public void checkInput() {
        if(description.getText().toString().trim().isEmpty()) {description.setError("Insert description");}

        if (description.getText().toString().trim().isEmpty() || selectedCoordinates == null) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_LONG).show();
            return;
        }

        showConfirmationDialog();

        submitRoom();

        description.setText("");
        coordinates.setText("");
    }

    public void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Room: " + description.getText() + "\nAt building: " + spinnerBuilding.getSelectedItem().toString() + ", has been created!")
                .setPositiveButton("ok", (dialogInterface, i) -> dialogInterface.dismiss()).create().show();
    }

    public void submitRoom() {
        String description = this.description.getText().toString();
        int building = this.building.getId();

        final String toUrl = "http://student.cs.hioa.no/~s325918/addRoom.php/" +
                "?desc=" + description +
                "&coords=" + selectedCoordinates +
                "&floor=" + selectedFloor +
                "&building=" + building;

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

    public static void hideKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);

        inputMethodManager.hideSoftInputFromWindow(
                activity.getCurrentFocus().getWindowToken(), 0);
    }
}
