package com.nikitapetrovs.roombooking;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;
import com.nikitapetrovs.roombooking.views.AdminActivity;
import com.nikitapetrovs.roombooking.dialogs.DialogMarker;
import com.nikitapetrovs.roombooking.repository.BuildingRepository;
import com.nikitapetrovs.roombooking.repository.RoomRepository;
import com.nikitapetrovs.roombooking.repository.models.Building;
import com.nikitapetrovs.roombooking.repository.models.Room;
import com.nikitapetrovs.roombooking.util.AppUtils;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, RoomRepository.AsyncResponse, BuildingRepository.AsyncResponse {

    private GoogleMap mMap;
    private Spinner spinnerBuilding;
    private Spinner spinnerFloors;
    private ImageButton adminButton;
    private TextView adminText;



    private ArrayList<Room> rooms;
    private ArrayList<Building> buildings;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Context context = this;

        spinnerBuilding = findViewById(R.id.spinnerBuilding);
        spinnerFloors = findViewById(R.id.spinnerFloor);
        adminButton = findViewById(R.id.adminButton);
        adminText = findViewById(R.id.textAdmin);

        View.OnClickListener adminListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) { //Listener for number buttons
                Intent i = new Intent(context, AdminActivity.class);
                startActivity(i);
                finish();
            }
        };

        adminButton.setOnClickListener(adminListener);
        adminText.setOnClickListener(adminListener);

        rooms = new ArrayList<>();
        buildings = new ArrayList<>();

        new RoomRepository(this).execute("http://student.cs.hioa.no/~s325918/getRooms.php");
        new BuildingRepository(this).execute("http://student.cs.hioa.no/~s325918/getBuildings.php");

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.getUiSettings().setMapToolbarEnabled(true);


    }

    public void addMarker(Room room) {
        String cor = room.getCoordinates();
        String[] cords = cor.split(", ");
        if(cords.length != 1 && !cords[0].equals("") && !cords[1].equals("")) {
            LatLng newLoc = new LatLng(Double.parseDouble(cords[0]), Double.parseDouble(cords[1]));
            MarkerOptions marker = new MarkerOptions().position(newLoc)
                    .title(room.getDescription())
                    .draggable(false)
                    .snippet("" + room.getId())
                    .icon(BitmapDescriptorFactory.fromBitmap(getBitmap(this, room.getDescription())));

            mMap.addMarker(marker);
        }
    }


    public Bitmap getBitmap(Context context, String label) {
        IconGenerator iconGenerator = new IconGenerator(context);
        return iconGenerator.makeIcon(label);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        DialogMarker dialog = new DialogMarker(this, marker.getTitle(), marker.getSnippet());
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        return true;
    }

    @Override
    public void getRooms(ArrayList<Room> output) {
        rooms = output;
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
                fillFloorsSpinner(string);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void fillFloorsSpinner(String building) {

        List<Integer> flrs = new ArrayList<>();
        int numberOfFloors = findBuilding(building).getFloors();

        for(int x = 0; x < numberOfFloors; x++) {
            flrs.add(x+1);
        }

        ArrayAdapter<Integer> floors = new ArrayAdapter<>(this, R.layout.spinner_layout, flrs);
        floors.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFloors.setAdapter(floors);
        spinnerFloors.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextSize(18);
                displayRooms();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public Building findBuilding(String input) {
        for(Building building: buildings) {
            if(building.getDescription().equals(input)) {
                String cords = building.getCenterCoordinates();
                LatLng newLoc = AppUtils.stringToCoordinates(cords);

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLoc,18));
                mMap.addPolygon(AppUtils.getPolygon(building));
                return building;
            }
        }
        return null;
    }

    public void displayRooms() {
        mMap.clear();
        int floor = Integer.parseInt(spinnerFloors.getSelectedItem().toString());
        Building building = findBuilding(spinnerBuilding.getSelectedItem().toString());
        for(Room room: rooms) {
            if(room.getFloor() == floor && room.getBuilding() == building.getId()) {
                addMarker(room);
            }
        }
    }


}

