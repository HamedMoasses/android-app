package eu.h2020.sc.utils;


import android.content.Intent;
import android.net.Uri;

/**
 * Create all the Intent to interact with the phone:
 * - call phone
 * - send sms
 * <p/>
 * Created by
 * Fabio Lombardi <fabio.lombardi@movenda.com> on 28/07/2016.
 * © All rights reserved by Movenda S.p.A..
 */
public class PhoneUtils {

    public static Intent createCallPhoneIntent(String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse(String.format("tel:%s", phoneNumber)));
        return callIntent;
    }

    public static Intent createSendSMSIntent(String phoneNumber) {
        Intent sendIntent = new Intent(Intent.ACTION_SENDTO);
        sendIntent.setData(Uri.parse(String.format("sms:%s", phoneNumber)));
        sendIntent.putExtra("exit_on_sent", true);
        return sendIntent;
    }
}
