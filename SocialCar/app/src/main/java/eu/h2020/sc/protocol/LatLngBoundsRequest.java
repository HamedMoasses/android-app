package eu.h2020.sc.protocol;

import android.net.Uri;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URL;

import eu.h2020.sc.transport.HttpConstants;

/**
 * @author Danilo Raspa <danilo.raspa@movenda.com>
 */

public class LatLngBoundsRequest extends ExternalApiRequest {

    private static final String TAG = LatLngBoundsRequest.class.getSimpleName();

    private static final String URL = "http://maps.googleapis.com/maps/api/geocode/json?";

    private String countryName;

    public LatLngBoundsRequest(String countryName) {
        this.countryName = countryName;
    }

    @Override
    public String getHttpMethod() {
        return HttpConstants.GET;
    }

    @Override
    public java.net.URL urllize() {
        try {
            String finalUrl = Uri.parse(URL).buildUpon()
                    .appendQueryParameter("address", this.countryName)
                    .build().toString();

            return new URL(finalUrl);
        } catch (MalformedURLException e) {
            Log.e(TAG, "Unable to build a valid URL...", e);
        }
        return null;
    }


}
