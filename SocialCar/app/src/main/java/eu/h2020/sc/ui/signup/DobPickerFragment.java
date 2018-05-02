package eu.h2020.sc.ui.signup;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.widget.DatePicker;
import android.widget.TextView;

import eu.h2020.sc.R;
import eu.h2020.sc.utils.DateUtils;

import java.util.Calendar;

public class DobPickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private Calendar calendar;
    private TextView textViewDob;
    private boolean signUpContext;

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.calendar = Calendar.getInstance();
        int year = this.calendar.get(Calendar.YEAR);
        int month = this.calendar.get(Calendar.MONTH);
        int day = this.calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(this.calendar.getTime().getTime());
        return datePickerDialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {

        this.calendar.set(year, month, day);

        String dobString = DateUtils.formatToDateOfBirth(this.calendar.getTime());

        this.textViewDob.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        this.textViewDob.setText(dobString);

        if (this.signUpContext)
            this.textViewDob.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(getActivity(), R.drawable.ic_birthday_24dp), null, null, null);
    }

    public void setTextViewDob(TextView textViewDob) {
        this.textViewDob = textViewDob;
    }

    public void setSignUpContext(boolean signUpContext) {
        this.signUpContext = signUpContext;
    }
}
