package eu.h2020.sc.ui.home.trip.search;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;

import org.json.JSONException;

import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.config.Globals;
import eu.h2020.sc.dao.LatLngBoundsDAO;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.location.geocoding.OsmGeocodingUtils;

/**
 * @author Danilo Raspa <danilo.raspa@movenda.com>
 */

public class GoogleLatLngBoundsTask extends AsyncTask<Void, Void, Integer> {

    private static final String TAG = GoogleLatLngBoundsTask.class.getSimpleName();

    private static final int BOUNDS_COMPLETED_RESULT = 1;
    private static final int BOUNDS_COMPLETED_NO_RESULT = 2;

    private SocialCarApplication socialCarStore;
    private LatLngBoundsDAO latLngBoundsDAO;
    private LatLngBounds latLngBounds;

    private Context context;
    private double latitude;
    private double longitude;

    public GoogleLatLngBoundsTask(double lat, double lon, Context context) {
        this.socialCarStore = SocialCarApplication.getInstance();
        this.latLngBoundsDAO = new LatLngBoundsDAO();
        this.latitude = lat;
        this.longitude = lon;
        this.context = context;
    }

    @Override
    protected Integer doInBackground(Void... voids) {

        if (this.context == null)
            return BOUNDS_COMPLETED_NO_RESULT;

        String country = OsmGeocodingUtils.getCountryName(this.latitude, this.longitude, this.context);

        try {
            if (country != null) {
                Log.i(TAG, String.format("Retrieving bounds for country : %s", country));
                this.latLngBounds = this.latLngBoundsDAO.getLatLngBounds(country);
            } else
                return BOUNDS_COMPLETED_NO_RESULT;

        } catch (ConnectionException e) {
            Log.e(TAG, "No internet connection.");
            return BOUNDS_COMPLETED_NO_RESULT;
        } catch (NotFoundException e) {
            Log.e(TAG, "Wrong URL.");
            return BOUNDS_COMPLETED_NO_RESULT;
        } catch (UnauthorizedException e) {
            Log.e(TAG, "Unauthorized to retrieve bounds from Google service.");
            return BOUNDS_COMPLETED_NO_RESULT;
        } catch (ServerException e) {
            Log.e(TAG, "Google internal server error.");
            return BOUNDS_COMPLETED_NO_RESULT;
        } catch (JSONException e) {
            Log.e(TAG, "Wrong json or no results.");
            return BOUNDS_COMPLETED_NO_RESULT;
        }
        return BOUNDS_COMPLETED_RESULT;
    }

    @Override
    protected void onPostExecute(Integer resultCode) {
        switch (resultCode) {
            case BOUNDS_COMPLETED_RESULT:
                storeBounds();
                break;
            case BOUNDS_COMPLETED_NO_RESULT:
                Log.e(TAG, "Unable to geocode country name...");
                break;
        }
    }

    private void storeBounds() {
        String latLngBoundsString = new Gson().toJson(this.latLngBounds);
        this.socialCarStore.storeBounds(latLngBoundsString);
    }

    public static LatLngBounds getPresentCountryLatLngBounds() {
        LatLngBounds presentCountryLatLngBounds = SocialCarApplication.getInstance().retrieveBounds();

        if (presentCountryLatLngBounds == null) {
            presentCountryLatLngBounds = new LatLngBounds(new LatLng(Globals.EU_BOUNDS_SOUTHWEST_LAT, Globals.EU_BOUNDS_SOUTHWEST_LNG), new LatLng(Globals.EU_BOUNDS_NORTHEAST_LAT, Globals.EU_BOUNDS_NORTHEAST_LNG));
        }
        return presentCountryLatLngBounds;
    }
}
