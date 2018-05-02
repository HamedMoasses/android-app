package eu.h2020.sc.dao;

import eu.h2020.sc.domain.Ride;
import eu.h2020.sc.exception.ConflictException;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.protocol.CreateRideRequest;
import eu.h2020.sc.protocol.DeleteRideRequest;
import eu.h2020.sc.protocol.GetRidesRequest;
import eu.h2020.sc.protocol.SocialCarRequest;
import eu.h2020.sc.protocol.UpdateRideRequest;
import eu.h2020.sc.transport.DeleteHttpTask;
import eu.h2020.sc.transport.GetHttpsTask;
import eu.h2020.sc.transport.PostHttpTask;
import eu.h2020.sc.transport.PutHttpTask;

import org.json.JSONException;

import java.util.List;

/**
 * Created by Pietro on 20/09/16.
 */
public class RideDAO {

    public List<Ride> findRidesByUserID(String userID) throws ServerException, ConnectionException, UnauthorizedException, NotFoundException, JSONException {
        SocialCarRequest request = new GetRidesRequest(userID);
        GetHttpsTask getHttpsTask = new GetHttpsTask();
        String json = getHttpsTask.makeRequest(request);
        return Ride.fromJsonToRideList(json);
    }

    public Ride createRide(Ride ride) throws ServerException, ConnectionException, UnauthorizedException, ConflictException {
        SocialCarRequest request = new CreateRideRequest(ride);
        PostHttpTask postHttpTask = new PostHttpTask();
        String jsonRide = postHttpTask.makeRequest(request);

        return Ride.fromJson(jsonRide);
    }

    public void updateRide(Ride ride) throws ConnectionException, NotFoundException, UnauthorizedException, ServerException {
        SocialCarRequest request = new UpdateRideRequest(ride);
        PutHttpTask putHttpTask = new PutHttpTask();
        putHttpTask.makeRequest(request);
    }

    public void deleteRideByID(String rideID) throws ConnectionException, NotFoundException, UnauthorizedException, ServerException {
        SocialCarRequest request = new DeleteRideRequest(rideID);
        DeleteHttpTask deleteHttpTask = new DeleteHttpTask();
        deleteHttpTask.makeRequest(request);
    }
}