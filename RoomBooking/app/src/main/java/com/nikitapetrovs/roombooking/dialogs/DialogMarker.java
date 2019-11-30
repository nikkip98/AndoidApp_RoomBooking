package com.nikitapetrovs.roombooking.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.nikitapetrovs.roombooking.R;
import com.nikitapetrovs.roombooking.Views.AddReservationActivity;
import com.nikitapetrovs.roombooking.repository.ReservationRepository;
import com.nikitapetrovs.roombooking.repository.models.Reservation;

import java.util.ArrayList;

public class DialogMarker extends Dialog implements ReservationRepository.AsyncResponse {

    private TextView desc, list;
    private Button button;
    private ImageButton close;
    private String roomID;
    private String inputDesc;

    public DialogMarker(@NonNull Context context, String description, String id) {
        super(context);
        this.roomID = id;
        this.inputDesc = description;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_marker);

        new ReservationRepository(this).execute("http://student.cs.hioa.no/~s325918/getReservations.php", roomID, null);

        desc = findViewById(R.id.labelDesc);
        list = findViewById(R.id.textList);
        button = findViewById(R.id.buttonResv);
        close = findViewById(R.id.buttonClose);

        desc.setText(inputDesc);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), AddReservationActivity.class);
                i.putExtra("id", roomID);
                i.putExtra("desc", inputDesc);
                getContext().startActivity(i);
                dismiss();
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }


    @Override
    public void getReservations(String output) {
        list.setText(output);
    }

    @Override
    public void getReservationsArray(ArrayList<Reservation> output) {

    }
}
