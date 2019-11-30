package com.nikitapetrovs.roombooking.Views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nikitapetrovs.roombooking.MainActivity;
import com.nikitapetrovs.roombooking.R;

public class AdminActivity  extends AppCompatActivity {

    private androidx.constraintlayout.widget.ConstraintLayout layoutBuilding, layoutRoom, layoutDeleteBuilding, layoutDeleteRoom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        final Context context = this;


        ImageButton backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(view -> {
            Intent i = new Intent(context, MainActivity.class);
            startActivity(i);
        });

        layoutBuilding = findViewById(R.id.layoutBuilding);
        layoutBuilding.setOnClickListener(view -> {
            Intent i = new Intent(context, AddBuildingActivity.class);
            startActivity(i);
        });

        layoutRoom = findViewById(R.id.layoutRoom);
        layoutRoom.setOnClickListener(view -> {
            Intent i = new Intent(context, AddRoomActivity.class);
            startActivity(i);
        });

        layoutDeleteBuilding = findViewById(R.id.layoutDeleteBuilding);
        layoutDeleteBuilding.setOnClickListener(view -> {
            Intent i = new Intent(context, DeleteBuildingActivity.class);
            startActivity(i);
        });

        layoutDeleteRoom = findViewById(R.id.layoutDeleteRoom);
        layoutDeleteRoom.setOnClickListener(view -> {
            Intent i = new Intent(context, DeleteRoomActivity.class);
            startActivity(i);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
