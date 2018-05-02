package eu.h2020.sc.dao;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import org.json.JSONException;
import org.json.JSONObject;

import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.protocol.LatLngBoundsRequest;
import eu.h2020.sc.transport.GetHttpTask;

/**
 * @author Danilo Raspa <danilo.raspa@movenda.com>
 */

public class LatLngBoundsDAO {

    private static final String STATUS_KEY = "status";
    private static final String OK_VALUE = "OK";
    private static final String RESULT_KEY = "results";
    private static final String GEOMETRY_KEY = "geometry";
    private static final String BOUNDS_KEY = "bounds";

    public LatLngBounds getLatLngBounds(String country) throws ConnectionException, NotFoundException, UnauthorizedException, ServerException, JSONException {
        LatLngBoundsRequest latLngBoundsRequest = new LatLngBoundsRequest(country);
        GetHttpTask getHttpTask = new GetHttpTask();
        String json = getHttpTask.makeRequest(latLngBoundsRequest);
        return fromJson(json);
    }

    private LatLngBounds fromJson(String json) throws JSONException {
        JSONObject jsonObject = new JSONObject(json);

        if (jsonObject.has(STATUS_KEY))
            if (jsonObject.getString(STATUS_KEY).equalsIgnoreCase(OK_VALUE)) {
                JSONObject bounds = jsonObject.getJSONArray(RESULT_KEY).getJSONObject(0)
                        .getJSONObject(GEOMETRY_KEY)
                        .getJSONObject(BOUNDS_KEY);

                double northeastLat = Double.parseDouble(bounds.getJSONObject("northeast").getString("lat"));
                double northeastLng = Double.parseDouble(bounds.getJSONObject("northeast").getString("lng"));
                double southwestLat = Double.parseDouble(bounds.getJSONObject("southwest").getString("lat"));
                double southwestLng = Double.parseDouble(bounds.getJSONObject("southwest").getString("lng"));

                return new LatLngBounds(new LatLng(southwestLat, southwestLng), new LatLng(northeastLat, northeastLng));
            }
        throw new JSONException("Wrong JSON");
    }


}
