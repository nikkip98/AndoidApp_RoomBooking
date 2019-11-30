package com.nikitapetrovs.roombooking.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.nikitapetrovs.roombooking.repository.ReservationRepository;
import com.nikitapetrovs.roombooking.repository.models.Reservation;

import java.util.ArrayList;

public class DialogMarkerViewModel extends AndroidViewModel  {

    private MutableLiveData<String> reservations = new MutableLiveData<>();

    public DialogMarkerViewModel(@NonNull Application application) {
        super(application);

        this.reservations.setValue("Reservations: \n");

    }



}
