/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */

package eu.h2020.sc.ui.trip.solution;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

import eu.h2020.sc.R;
import eu.h2020.sc.utils.DateUtils;


public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    /*
     TODO refactoring: using same datepicker, implenting separate OnDateSetListener on activity
      */

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

        String tripDateString = DateUtils.formatDateToString(calendar.getTime(), TripSolutionsActivity.TRIP_DATE_FORMAT);

        ((TextView) getActivity().findViewById(R.id.trip_solution_date_picker_label)).setText(tripDateString);
    }
}