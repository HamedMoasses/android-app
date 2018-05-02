/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */
package eu.h2020.sc.dao;

import android.util.Log;

import eu.h2020.sc.domain.SocialProvider;
import eu.h2020.sc.domain.User;
import eu.h2020.sc.exception.ConflictException;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.protocol.CreateUserRequest;
import eu.h2020.sc.protocol.FindUsersRequest;
import eu.h2020.sc.protocol.GetUserRequest;
import eu.h2020.sc.protocol.QueryParameter;
import eu.h2020.sc.protocol.SocialCarRequest;
import eu.h2020.sc.protocol.UpdateUserRequest;
import eu.h2020.sc.transport.GetHttpsTask;
import eu.h2020.sc.transport.PostHttpTask;
import eu.h2020.sc.transport.PutHttpTask;

import org.json.JSONException;

/**
 * Created by fminori on 16/09/16.
 */

public class UserDAO {

    private final String TAG = getClass().getSimpleName();

    public User findUserByEmail(String email) throws ServerException, ConnectionException, UnauthorizedException, NotFoundException {
        QueryParameter queryParameter = QueryParameter.with(User.EMAIL, email);
        SocialCarRequest request = new FindUsersRequest(queryParameter.parameters());

        GetHttpsTask getHttpsTask = new GetHttpsTask();
        String json = getHttpsTask.makeRequest(request);

        try {
            return User.fromJsonList(json);
        } catch (JSONException e) {
            throw new RuntimeException("is JSON compliant with the protocol? Check it! " + json, e);
        }
    }

    public User findBySocialProvider(SocialProvider socialProvider) throws ServerException, ConnectionException, UnauthorizedException, NotFoundException {

        String socialIDParameter = String.format("%s.%s", User.SOCIAL_PROVIDER, SocialProvider.SOCIAL_ID);
        String socialNetworkParameter = String.format("%s.%s", User.SOCIAL_PROVIDER, SocialProvider.SOCIAL_NETWORK);

        QueryParameter queryParameter = QueryParameter.with(socialIDParameter, socialProvider.getSocialID()).and(socialNetworkParameter, socialProvider.getSocialNetwork().toString());
        SocialCarRequest request = new FindUsersRequest(queryParameter.parameters());

        GetHttpsTask getHttpsTask = new GetHttpsTask();
        String json = getHttpsTask.makeRequest(request);

        try {
            return User.fromJsonList(json);
        } catch (JSONException e) {
            throw new RuntimeException("is JSON compliant with the protocol? Check it! " + json, e);
        }
    }

    public User findUserByID(String userID) throws ServerException, ConnectionException, UnauthorizedException, NotFoundException {
        SocialCarRequest request = new GetUserRequest(userID);
        GetHttpsTask getHttpsTask = new GetHttpsTask();
        String json = getHttpsTask.makeRequest(request);
        return User.fromJson(json);
    }

    public void updateUser(User user) throws ServerException, ConnectionException, UnauthorizedException, NotFoundException {
        user.setPlatform("ANDROID");
        UpdateUserRequest request = new UpdateUserRequest(user);
        PutHttpTask putHttpTask = new PutHttpTask();
        putHttpTask.makeRequest(request);
    }

    public User createUser(User user) throws ServerException, ConnectionException, ConflictException {
        user.setPlatform("ANDROID");
        SocialCarRequest request = new CreateUserRequest(user);
        PostHttpTask postHttpTask = new PostHttpTask();
        String json = null;
        try {
            json = postHttpTask.makeRequest(request);
        } catch (UnauthorizedException e) {
            Log.e(TAG, "Unexpected error: ", e);
        }

        return User.fromJson(json);
    }
}