package eu.h2020.sc.location.geocoding;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.text.TextUtils;

import com.google.android.gms.location.places.Place;

import org.apache.commons.lang3.StringUtils;
import org.osmdroid.bonuspack.location.GeocoderNominatim;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import eu.h2020.sc.R;
import eu.h2020.sc.exception.NotFoundException;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */

public class OsmGeocodingUtils {

    private static final String TAG = OsmGeocodingUtils.class.getName();

    public static Address getAddressFromStreet(Place place, Context context) throws IOException, NotFoundException {
        String street = extractStreet(place);
        return obtainAddress(fullNormalizeSpaces(getStreetFromAndroidLocation(street, context)), context);
    }

    private static String extractStreet(Place place) {

        if (place.getPlaceTypes().contains(Place.TYPE_POINT_OF_INTEREST)) {
            return String.format("%s, %s", place.getName(), place.getAddress());

        } else {
            return place.getAddress().toString();
        }
    }

    private static String fullNormalizeSpaces(String string) {
        return StringUtils.normalizeSpace(string).replaceAll("' ", "'");
    }

    public static Address convertLocationToAddress(double latitude, double longitude, Context context) throws NotFoundException, IOException {

        GeocoderNominatim geoCoder = new GeocoderNominatim(context.getString(R.string.app_name));
        List<Address> addresses;
        try {
            addresses = geoCoder.getFromLocation(latitude, longitude, 5);
        } catch (IOException e1) {
            throw new IOException(String.format("Invalid location %s!!", String.format("(%s,%s)", latitude, longitude)));
        }
        if (addresses.isEmpty())
            throw new NotFoundException(String.format("No address found for the location: %s", String.format("%s,%s", latitude, longitude)));

        return addresses.get(0);
    }

    public static String convertAddressToString(Address address) {
        return getAddressToString(address);
    }

    public static String getCountryName(double latitude, double longitude, Context context) {

        GeocoderNominatim geoCoder = new GeocoderNominatim(context.getString(R.string.app_name));

        try {
            List<Address> addresses = geoCoder.getFromLocation(latitude, longitude, 1);
            return addresses.get(0).getCountryName();

        } catch (IOException e) {
            return null;
        }
    }

    private static Address obtainAddress(String street, Context context) throws IOException, NotFoundException {

        GeocoderNominatim geoCoder = new GeocoderNominatim(context.getString(R.string.app_name));
        List<Address> addressesFound;

        try {
            addressesFound = geoCoder.getFromLocationName(street, 5);
        } catch (IOException e) {
            throw new IOException(String.format("%s - invalid street %s!!", TAG, street), e);
        }
        if (addressesFound.isEmpty())
            throw new NotFoundException(String.format("GeocoderNominatim - No match found for the street \"%s\"", street));

        return addressesFound.get(0);
    }

    private static String getAddressToString(Address address) {

        if (address.getLocality() != null) {

            if (address.getThoroughfare() != null && address.getSubThoroughfare() == null) {
                return String.format("%s, %s", address.getThoroughfare(), address.getLocality());

            } else if (address.getThoroughfare() != null && address.getSubThoroughfare() != null) {
                return String.format("%s,%s %s", address.getThoroughfare(), address.getSubThoroughfare(), address.getLocality());

            } else if (address.getLocality().equalsIgnoreCase(address.getAddressLine(0)) || TextUtils.isDigitsOnly(address.getAddressLine(0)))
                return address.getLocality();
            else
                return address.getAddressLine(0);
        } else
            return address.getAddressLine(0);
    }

    private static String getStreetFromAndroidLocation(String street, Context context) throws IOException, NotFoundException {

        Geocoder geoCoder = new Geocoder(context, Locale.getDefault());
        List<Address> addressesFound;

        try {
            addressesFound = geoCoder.getFromLocationName(street, 5);

        } catch (IOException e) {
            throw new IOException(String.format("%s Invalid street during android location conversion : %s!!", TAG, street), e);
        }

        if (addressesFound.isEmpty())
            throw new NotFoundException(String.format("Android location - No match found for the street \"%s\"", street));

        return getAddressToString(addressesFound.get(0));
    }
}
