/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */
package eu.h2020.sc.dao.multipart;

import android.net.Uri;
import android.util.Log;

import eu.h2020.sc.domain.Picture;
import eu.h2020.sc.domain.UserPicture;
import eu.h2020.sc.exception.ConflictException;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.protocol.CreateUserPictureRequest;
import eu.h2020.sc.protocol.FindMediaRequest;
import eu.h2020.sc.protocol.FindUserPictureRequest;
import eu.h2020.sc.transport.GetHttpsTask;
import eu.h2020.sc.transport.MultipartPostTask;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */

public class PictureUserMultipartDAO extends PictureMultipartDAO {

    @Override
    public byte[] findPictureByID(String userID) throws ConnectionException, NotFoundException, UnauthorizedException, ServerException {
        FindUserPictureRequest findUserPictureRequest = new FindUserPictureRequest(userID);
        GetHttpsTask getHttpsTask = new GetHttpsTask();
        return getHttpsTask.makeBinaryRequest(findUserPictureRequest);
    }

    @Override
    public byte[] findPictureByMediaUri(String mediaUri) throws ConnectionException, NotFoundException, UnauthorizedException, ServerException {
        FindMediaRequest findMediaRequest = new FindMediaRequest(mediaUri);
        GetHttpsTask getHttpsTask = new GetHttpsTask();
        return getHttpsTask.makeBinaryRequest(findMediaRequest);
    }

    @Override
    public Picture createOrUpdate(String userID, Uri mediaFileUri) throws ConnectionException, NotFoundException, UnauthorizedException, ServerException, ConflictException {

        CreateUserPictureRequest pictureRequest = new CreateUserPictureRequest(userID, mediaFileUri);

        MultipartPostTask multipartPostTask = new MultipartPostTask();
        String jsonResult = multipartPostTask.makeRequest(pictureRequest);

        Log.i(PictureUserMultipartDAO.class.getCanonicalName(), jsonResult);

        return UserPicture.fromJson(jsonResult);
    }
}
