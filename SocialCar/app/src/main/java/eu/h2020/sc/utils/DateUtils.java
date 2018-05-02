package eu.h2020.sc.utils;

import android.annotation.SuppressLint;
import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import eu.h2020.sc.R;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class DateUtils {

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT_FULL_DATE = new SimpleDateFormat("dd-MMMM-yyyy HH:mm", Locale.getDefault());
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT_CANONIC_DATE = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT_INTERNATIONAL_DATE = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT_DAY_SHORT_MONTH = new SimpleDateFormat("dd MMM", Locale.getDefault());
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT_ONLY_HOUR = new SimpleDateFormat("HH:mm", Locale.getDefault());
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT_ONLY_HOUR_12_FORMAT = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    public static String dateLongToString(Date date) {
        return SIMPLE_DATE_FORMAT_FULL_DATE.format(date);
    }

    public static String dateLongToStringCanonic(Date date) {
        return SIMPLE_DATE_FORMAT_CANONIC_DATE.format(date);
    }

    public static String dateLongNoHourToString(Date date) {
        return SIMPLE_DATE_FORMAT_DAY_SHORT_MONTH.format(date);
    }

    public static String hourLongToString(Date date, boolean use24HoursFormat) {

        SimpleDateFormat dateFormatter;

        if (use24HoursFormat)
            dateFormatter = SIMPLE_DATE_FORMAT_ONLY_HOUR;
        else
            dateFormatter = SIMPLE_DATE_FORMAT_ONLY_HOUR_12_FORMAT;

        return dateFormatter.format(date);
    }

    public static Date formatStringToDate(String dateString, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            throw new RuntimeException("Programming error ... please check correct format!", e);
        }
    }

    public static String formatDateToString(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        return dateFormat.format(date);
    }

    public static String formatToExtendedDate(Date date) {
        SimpleDateFormat dateFormatter = new SimpleDateFormat("E d MMM yyyy", Locale.getDefault());
        return dateFormatter.format(date);
    }

    public static String formatToDateOfBirth(Date date) {
        return SIMPLE_DATE_FORMAT_INTERNATIONAL_DATE.format(date);
    }

    public static Date parseDateDateOfBirth(String date) throws ParseException {
        return SIMPLE_DATE_FORMAT_INTERNATIONAL_DATE.parse(date);
    }

    public static String formatToHoursAndMinutes(Date date, boolean use24HoursFormat) {

        SimpleDateFormat dateFormatter;

        if (use24HoursFormat)
            dateFormatter = SIMPLE_DATE_FORMAT_ONLY_HOUR;
        else
            dateFormatter = SIMPLE_DATE_FORMAT_ONLY_HOUR_12_FORMAT;

        return dateFormatter.format(date);
    }

    public static String formatCurrentHour(Date date, boolean use24HoursFormat) {

        SimpleDateFormat dateFormatter;
        if (use24HoursFormat)
            dateFormatter = new SimpleDateFormat("HH");
        else
            dateFormatter = new SimpleDateFormat("hh");

        return dateFormatter.format(date);
    }

    @SuppressLint("DefaultLocale")
    public static String formatToRide(Date date, boolean use24HoursFormat, Context context) {

        String hourMinute = formatToHoursAndMinutes(date, use24HoursFormat);
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM", Locale.getDefault());
        String month = dateFormatter.format(date);

        dateFormatter = new SimpleDateFormat("d", Locale.getDefault());
        int day = Integer.parseInt(dateFormatter.format(date));

        return String.format("%s %s %s %s %d%s", context.getResources().getString(R.string.at_date), hourMinute, context.getResources().getString(R.string.on_date), month, day, getDayOfMonthSuffix(day));
    }

    private static String getDayOfMonthSuffix(final int n) {
        if (n >= 11 && n <= 13) {
            return "th";
        }
        switch (n % 10) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    public static long toUnixTimestamp(Date date) {
        return date.getTime() / 1000L;
    }
}
