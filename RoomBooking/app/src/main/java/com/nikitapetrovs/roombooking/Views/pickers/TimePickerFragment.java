package com.nikitapetrovs.roombooking.Views.pickers;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private onTimeSetListener mListener;

    /**
     * Listener
     */
    public interface onTimeSetListener {
        void receiveTimeFrom(String time);
        void receiveTimeTo(String time);
    }

    /**
     * Initializes dialog and content
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute, true);
    }

    /**
     * Makes String object with selected time and sends it to listener
     * @param timePicker
     * @param hour
     * @param minute
     */
    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        String time = String.format("%02d:%02d", hour, minute);

        if(this.getTag().equals("from")) {
            mListener.receiveTimeFrom(time);
            return;
        }
        mListener.receiveTimeTo(time);

    }

    /**
     * Attaches listener
     * @param context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (onTimeSetListener) getActivity();
    }
}
