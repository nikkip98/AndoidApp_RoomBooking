package com.nikitapetrovs.roombooking.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.google.android.gms.maps.model.PolygonOptions;
import com.nikitapetrovs.roombooking.R;
import com.nikitapetrovs.roombooking.util.AppUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;

import static java.util.Locale.getDefault;

public class AddBuildingActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener  {

    private GoogleMap mMap;

    private ImageButton backButton;
    private ImageButton chooseLocation;
    private Button buttonMap;
    private Button buttonSubmit;
    private TextView coordinates;
    private TextView selectedAdress;
    private EditText description;
    private EditText floors;

    private String centerCoordinates;
    private ArrayList<LatLng> corners;
    private int counter;
    private boolean done;
    private boolean centerIsSet;

    private androidx.constraintlayout.widget.ConstraintLayout mapLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_building);

        final Activity activity = this;

        corners = new ArrayList<>();
        centerCoordinates = "";

        mapLayout = findViewById(R.id.mapLayout);
        coordinates = findViewById(R.id.textCoordinates);
        description = findViewById(R.id.textDescription);
        floors = findViewById(R.id.textFloors);
        selectedAdress = findViewById(R.id.textViewSelectedAddress);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> finish());

        buttonSubmit = findViewById(R.id.submitButton);
        buttonSubmit.setOnClickListener(view -> checkInput());

        buttonMap = findViewById(R.id.buttonMap);
        buttonMap.setOnClickListener(view -> {
            mapLayout.setVisibility(View.INVISIBLE);
            mapLayout.setClickable(false);
            coordinates.setText(centerCoordinates);
            buttonSubmit.setVisibility(View.VISIBLE);
        });

        chooseLocation = findViewById(R.id.chooseLocation);
        chooseLocation.setOnClickListener(view -> {
            if(!done) {showTutorialDialog();}
            mapLayout.setVisibility(View.VISIBLE);
            mapLayout.setClickable(true);
            buttonSubmit.setVisibility(View.INVISIBLE);
            hideKeyboard(activity);
        });

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setOnMapClickListener(this);
        LatLng loc = new LatLng(59.919472, 10.735318);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 18));
    }

    @Override
    public void onMapClick(LatLng latLng) {
        //mMap.clear();

        if(done) {return;}

        if(centerIsSet && counter < 4) {
            corners.add(latLng);
            counter++;
        }

        if(counter == 4) {
            mMap.addPolygon(new PolygonOptions().add(
                    corners.get(0), corners.get(1), corners.get(2), corners.get(3))
                    .strokeWidth(7)
                    .fillColor(R.color.colorFill)
                    .strokeColor(Color.BLACK));
            done = true;
        }

        if(!centerIsSet) {
            this.centerCoordinates = AppUtils.coordinatesToString(latLng);
            Geocoder geo = new Geocoder(this);
            try {
                Address address = geo.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0);
                selectedAdress.setText(address.getAddressLine(0));
            } catch (Exception e) {
                return;
            }


            centerIsSet = true;
        }

        MarkerOptions marker = new MarkerOptions();
        marker.position(latLng);
        mMap.addMarker(marker);
    }

    public void checkInput() {
        if(description.getText().toString().trim().isEmpty()) { description.setError("Insert description");}

        if(floors.getText().toString().trim().isEmpty()) { floors.setError("Set number of floors"); }

        if(!floors.getText().toString().isEmpty() && Integer.parseInt(floors.getText().toString()) > 100) { floors.setError("Building cannot have more than 100 floors"); }

        if(description.getText().toString().trim().isEmpty() || centerCoordinates == null
        || floors.getText().toString().trim().isEmpty() ) {
            Toast.makeText(this, "Fill all fields", Toast.LENGTH_LONG).show();
            return;
        }

        if(corners.isEmpty()) {
            Toast.makeText(this, "Select corners", Toast.LENGTH_SHORT).show();
        }


        showConfirmationDialog();

        submitBuilding();

        description.setText("");
        floors.setText("");
        coordinates.setText("");
    }

    public void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Building: " + description.getText() + ", has been created!")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }

    public void showTutorialDialog() {
        new AlertDialog.Builder(this)
                .setTitle("You are about to create a building")
                .setIcon(R.drawable.ic_warning_orange_24dp)
                .setMessage("1. Choose center of the building\n2. Choose 4 corners of the building")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();
    }

    public void submitBuilding() {
        String desc = description.getText().toString();
        String floors = this.floors.getText().toString();

        String toUrl = "http://student.cs.hioa.no/~s325918/addBuilding.php/" +
                "?desc=" + desc +
                "&center=" + centerCoordinates +
                "&cord1=" + AppUtils.coordinatesToString(corners.get(0)) +
                "&cord2=" + AppUtils.coordinatesToString(corners.get(1)) +
                "&cord3=" + AppUtils.coordinatesToString(corners.get(2)) +
                "&cord4=" + AppUtils.coordinatesToString(corners.get(3)) +
                "&floors=" + floors;

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
