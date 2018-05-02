/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */

package eu.h2020.sc.ui.ride.creation.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import eu.h2020.sc.R;
import eu.h2020.sc.ui.ride.creation.activity.RideCreationFormActivity;
import eu.h2020.sc.utils.DateUtils;

import java.util.Calendar;


public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        datePickerDialog.getDatePicker().setMinDate(c.getTime().getTime());
        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        String tripDateString = DateUtils.formatDateToString(calendar.getTime(),RideCreationFormActivity.TRIP_DATE_FORMAT);

        ((TextView) getActivity().findViewById(R.id.ridecreation_text_tripdate)).setText(tripDateString);

    }
}