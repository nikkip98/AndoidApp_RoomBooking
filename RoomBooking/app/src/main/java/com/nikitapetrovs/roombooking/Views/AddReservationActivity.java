package com.nikitapetrovs.roombooking.Views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.nikitapetrovs.roombooking.R;
import com.nikitapetrovs.roombooking.Views.pickers.DatePickerFragment;
import com.nikitapetrovs.roombooking.Views.pickers.TimePickerFragment;
import com.nikitapetrovs.roombooking.repository.ReservationRepository;
import com.nikitapetrovs.roombooking.repository.models.Reservation;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddReservationActivity extends AppCompatActivity implements TimePickerFragment.onTimeSetListener, DatePickerFragment.onDateSetListener, ReservationRepository.AsyncResponse {

    private static final String TAG = "ADD_RESERVATION";

    private ImageButton backButton;
    private EditText date;
    private EditText timeFrom;
    private EditText timeTo;
    private TextView room;
    private Button submit;

    private String id;
    private String selectedRoom;
    private String selectedDate;
    private String selectedTimeFrom;
    private String selectedTimeTo;
    private ArrayList<Reservation> reservations;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_reservation);

        reservations = new ArrayList<>();

        room = findViewById(R.id.roomName);
        date = findViewById(R.id.editTextDate);
        timeFrom = findViewById(R.id.editTextTimeFrom);
        timeTo = findViewById(R.id.editTextTImeTo);

        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> finish());
        date.setInputType(InputType.TYPE_NULL);
        date.setOnClickListener(view -> {
            DialogFragment fragment = new DatePickerFragment();
            fragment.show(getSupportFragmentManager(), "datePicker");
        });

        timeFrom.setInputType(InputType.TYPE_NULL);
        timeFrom.setOnClickListener(view -> {
            DialogFragment fragment = new TimePickerFragment();
            fragment.show(getSupportFragmentManager(), "from");
        });

        timeTo.setInputType(InputType.TYPE_NULL);
        timeTo.setOnClickListener(view -> {
            DialogFragment fragment = new TimePickerFragment();
            fragment.show(getSupportFragmentManager(), "to");
        });

        submit = findViewById(R.id.submittButton);
        submit.setOnClickListener(view -> validateSubmit());

        Intent intent = getIntent();
        if (intent.hasExtra("id")) {
            id = intent.getStringExtra("id");
            selectedRoom = intent.getStringExtra("desc");
            room.setText(selectedRoom);
        }

        new ReservationRepository(this).execute("http://student.cs.hioa.no/~s325918/getReservations.php/", id);
    }

    public void validateSubmit() {
        if(!checkInput()) {
            Toast.makeText(this, "Select all fields", Toast.LENGTH_LONG).show();
            return;
        }

        if (!checkIfTimeAvailable()) {
            return;
        }

        showConfirmationDialog();

        date.setText("");
        timeFrom.setText("");
        timeTo.setText("");

        submitReservation();
    }

    private void submitReservation() {
        //Sending reservation to database
        final String toUrl = "http://student.cs.hioa.no/~s325918/addReservation.php/"
                + "?ID=" + this.id
                + "&date=" + selectedDate
                + "&time_from=" + selectedTimeFrom
                + "&time_to=" + selectedTimeTo;

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

    public boolean checkInput() {
        return !date.getText().toString().trim().isEmpty()
                && !timeTo.getText().toString().trim().isEmpty()
                && !timeFrom.getText().toString().trim().isEmpty();
    }

    private boolean checkIfTimeAvailable() {
        for (Reservation reservation : reservations) {
            String date = reservation.getDate();

            DateFormat hhmm = new SimpleDateFormat("hh:mm");

            Date resvDateFrom = new Date();
            Date resvDateTo = new Date();
            Date selectedDateFrom = new Date();
            Date selectedDateTo = new Date();

            try {
                resvDateFrom = hhmm.parse(reservation.getTimeFrom());
                resvDateTo = hhmm.parse(reservation.getTimeTo());
                selectedDateFrom = hhmm.parse(selectedTimeFrom);
                selectedDateTo = hhmm.parse(selectedTimeTo);

            } catch (ParseException e) {
                Log.d(TAG, "submitReservation: " + e);
            }

            if (date.equals(selectedDate)
                    && resvDateFrom.before(selectedDateTo)
                    && selectedDateFrom.before(resvDateTo)) {
                Toast.makeText(this, "Time is not available", Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmation")
                .setMessage("Reservation made for Room: " + selectedRoom
                        + "\nDate: " + selectedDate
                        + "\nFrom: " + selectedTimeFrom + " " + "To: " + selectedTimeTo)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                }).create().show();
    }

    @Override
    public void receiveTimeFrom(String time) {
        selectedTimeFrom = time;
        String text = getString(R.string.from) + " " + time;
        timeFrom.setText(text);
    }

    @Override
    public void receiveTimeTo(String time) {
        selectedTimeTo = time;
        String text = getString(R.string.to) + " " + time;
        timeTo.setText(text);
    }

    @Override
    public void receiveDate(String string) {
        selectedDate = string;
        String text = getString(R.string.date_colon) + " " + string;
        date.setText(text);
    }

    @Override
    public void getReservations(String output) {

    }

    @Override
    public void getReservationsArray(ArrayList<Reservation> output) {
        reservations = output;
    }
}
