package com.poman.atm;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;

import static android.support.constraint.Constraints.TAG;

public class DatePickerFragment extends DialogFragment {
    private static DatePickerFragment datePickerFragment;

    public static DatePickerFragment getInstance() {
        if (datePickerFragment == null) {
            datePickerFragment = new DatePickerFragment();
        }
        return datePickerFragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        Log.d(TAG, "onCreateDialog: " + calendar.getTime());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), dateSetListener, year, month, day);
    }

    private DatePickerDialog.OnDateSetListener dateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                    ((OnDateSetListener)getActivity()).onDateSet(view,year,month,dayOfMonth);
                }
            };

    public interface OnDateSetListener {
        void onDateSet(DatePicker view, int year, int month, int dayOfMonth);
    }
}
