package eu.h2020.sc.dao;

import eu.h2020.sc.domain.Trip;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.protocol.SocialCarRequest;
import eu.h2020.sc.protocol.TripDetailsRequest;
import eu.h2020.sc.transport.GetHttpsTask;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class TripDAO {

    public Trip findTripByID(String tripID) throws ConnectionException, NotFoundException, UnauthorizedException, ServerException, JSONException {
        SocialCarRequest request = new TripDetailsRequest(tripID);
        GetHttpsTask getHttpsTask = new GetHttpsTask();
        String json = getHttpsTask.makeRequest(request);

        JSONObject jsonResponse = new JSONObject(json);

        return Trip.fromJson(jsonResponse.getJSONObject(Trip.TRIP).toString());
    }
}
