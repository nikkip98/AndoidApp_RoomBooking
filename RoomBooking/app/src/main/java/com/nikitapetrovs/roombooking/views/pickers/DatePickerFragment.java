package com.nikitapetrovs.roombooking.views.pickers;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private onDateSetListener mListener;
    private boolean adding;

    public DatePickerFragment(boolean adding) {
        this.adding = adding;
    }

    /**
     * Listener
     */
    public interface onDateSetListener {
        void receiveDate(String string);
    }

    /**
     * Initializes dialog and content
     * @param savedInstanceState
     */
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(getActivity(), this, year, month, day);

        if(adding) {
            dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        }

        return dialog;
    }

    /**
     * Makes String object with selected data and sends it to listener
     * @param datePicker
     * @param year
     * @param month
     * @param day
     */
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        String date = day + "/" + (month+1) + "/" + year;
        mListener.receiveDate(date);
    }

    /**
     * Attaches listener
     * @param context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mListener = (onDateSetListener) getActivity();
    }
}
