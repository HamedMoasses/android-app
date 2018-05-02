package eu.h2020.sc;

import android.util.Log;

import org.junit.Test;

import java.util.Date;

import eu.h2020.sc.utils.DateUtils;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */

public class DateTest {

    @Test
    public void testHourNo24Format() {


        long timestamp = 1521631715;


        String date = DateUtils.formatToHoursAndMinutes(new Date(timestamp * 1000L), true);

        System.out.println(date);


    }
}
