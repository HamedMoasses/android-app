/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */

package eu.h2020.sc.ui.ride.creation;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TextView;
import android.widget.TimePicker;

import eu.h2020.sc.R;
import eu.h2020.sc.utils.DateUtils;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private boolean is24HourFormat;


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        this.is24HourFormat = DateFormat.is24HourFormat(getActivity());
        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                this.is24HourFormat);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        final Calendar c = Calendar.getInstance();
        // every date is okay ... we need only hours and minutes..
        c.set(0, 0, 0, hourOfDay, minute);

        String hoursAndMinutes = DateUtils.formatToHoursAndMinutes(c.getTime(), is24HourFormat);
        ((TextView) getActivity().findViewById(R.id.ridecreation_text_triptime)).setText(hoursAndMinutes);

    }
}