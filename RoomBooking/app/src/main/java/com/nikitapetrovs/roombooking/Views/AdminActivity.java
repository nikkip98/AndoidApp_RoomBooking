package com.nikitapetrovs.roombooking.Views;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.nikitapetrovs.roombooking.MainActivity;
import com.nikitapetrovs.roombooking.R;

public class AdminActivity  extends AppCompatActivity {

    private ImageButton backButton;
    private androidx.constraintlayout.widget.ConstraintLayout layoutBuilding, layoutRoom, layoutDeleteBuilding, layoutDeleteRoom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        final Context context = this;


        backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, MainActivity.class);
                startActivity(i);
            }
        });

        layoutBuilding = findViewById(R.id.layoutBuilding);
        layoutBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, AddBuildingActivity.class);
                startActivity(i);
            }
        });

        layoutRoom = findViewById(R.id.layoutRoom);
        layoutRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, AddRoomActivity.class);
                startActivity(i);
            }
        });

        layoutDeleteBuilding = findViewById(R.id.layoutDeleteBuilding);
        layoutDeleteBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, DeleteBuildingActivity.class);
                startActivity(i);
            }
        });

        layoutDeleteRoom = findViewById(R.id.layoutDeleteRoom);
        layoutDeleteRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, DeleteRoomActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
    }
}
