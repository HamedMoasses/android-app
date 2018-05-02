package eu.h2020.sc.dao;

import eu.h2020.sc.domain.Feedback;
import eu.h2020.sc.domain.UserType;
import eu.h2020.sc.exception.ConflictException;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.protocol.CreateFeedbackRequest;
import eu.h2020.sc.protocol.FindFeedbackRequest;
import eu.h2020.sc.protocol.SocialCarRequest;
import eu.h2020.sc.protocol.UpdateFeedbackRequest;
import eu.h2020.sc.transport.GetHttpsTask;
import eu.h2020.sc.transport.PostHttpTask;
import eu.h2020.sc.transport.PutHttpTask;

import org.json.JSONException;

import java.util.List;

/**
 * Created by Pietro on 26/09/16.
 */

public class FeedbackDAO {

    public List<Feedback> findFeedBack(String userID, UserType userType) throws ServerException, ConnectionException, UnauthorizedException, NotFoundException {
        SocialCarRequest socialCarRequest = new FindFeedbackRequest(userID, userType);
        GetHttpsTask getHttpsTask = new GetHttpsTask();
        String jsonResponse = getHttpsTask.makeRequest(socialCarRequest);

        try {
            return Feedback.fromJsonList(jsonResponse);
        } catch (JSONException e) {
            throw new RuntimeException("is JSON compliant with the protocol? Check it! " + jsonResponse, e);
        }
    }

    public Feedback createFeedback(Feedback feedback) throws ServerException, ConnectionException, ConflictException, UnauthorizedException {

        CreateFeedbackRequest request = new CreateFeedbackRequest(feedback);
        PostHttpTask postHttpTask = new PostHttpTask();
        String json = postHttpTask.makeRequest(request);

        return Feedback.fromJson(json);
    }

    public void update(Feedback feedback) throws ServerException, ConnectionException, UnauthorizedException, NotFoundException {
        UpdateFeedbackRequest request = new UpdateFeedbackRequest(feedback);
        PutHttpTask putHttpTask = new PutHttpTask();
        putHttpTask.makeRequest(request);
    }
}
