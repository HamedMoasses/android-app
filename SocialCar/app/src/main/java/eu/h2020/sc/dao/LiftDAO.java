package eu.h2020.sc.dao;

import eu.h2020.sc.domain.Lift;
import eu.h2020.sc.domain.LiftType;
import eu.h2020.sc.exception.ConflictException;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.protocol.CreateLiftRequest;
import eu.h2020.sc.protocol.FindLiftsRequest;
import eu.h2020.sc.protocol.SocialCarRequest;
import eu.h2020.sc.protocol.UpdateLiftRequest;
import eu.h2020.sc.transport.GetHttpsTask;
import eu.h2020.sc.transport.PostHttpTask;
import eu.h2020.sc.transport.PutHttpTask;

import org.json.JSONException;

import java.util.List;

/**
 * Created by Pietro on 26/09/16.
 */
public class LiftDAO {

    public void createLift(Lift lift) throws ServerException, ConnectionException, ConflictException, UnauthorizedException {
        SocialCarRequest socialCarRequest = new CreateLiftRequest(lift);
        PostHttpTask postHttpTask = new PostHttpTask();
        postHttpTask.makeRequest(socialCarRequest);
    }

    public void updateLift(Lift lift) throws ConnectionException, NotFoundException, UnauthorizedException, ServerException {
        SocialCarRequest request = new UpdateLiftRequest(lift);
        PutHttpTask putHttpTask = new PutHttpTask();
        putHttpTask.makeRequest(request);
    }

    public List<Lift> findLift(String userID, LiftType liftType) throws ConnectionException, NotFoundException, UnauthorizedException, ServerException, JSONException {
        SocialCarRequest socialCarRequest = new FindLiftsRequest(userID, liftType);
        GetHttpsTask getHttpsTask = new GetHttpsTask();
        String json = getHttpsTask.makeRequest(socialCarRequest);

        return Lift.fromJsonToLiftList(json);
    }
}
