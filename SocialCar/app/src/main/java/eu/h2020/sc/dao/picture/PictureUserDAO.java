/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */
package eu.h2020.sc.dao.picture;

import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.protocol.FindMediaRequest;
import eu.h2020.sc.protocol.UpdatePictureRequest;
import eu.h2020.sc.transport.BinaryHttpTask;
import eu.h2020.sc.transport.GetHttpsTask;

/**
 * Created by fminori on 19/09/16.
 */

public class PictureUserDAO extends PictureDAO {

    @Override
    public byte[] findPictureByID(String resourceID) throws ConnectionException, NotFoundException, UnauthorizedException, ServerException {
        FindMediaRequest findMediaRequest = new FindMediaRequest(resourceID);
        GetHttpsTask getHttpsTask = new GetHttpsTask();
        return getHttpsTask.makeBinaryRequest(findMediaRequest);
    }

    @Override
    public void createOrUpdate(byte[] picture, String resourceID) throws ServerException, ConnectionException, UnauthorizedException, NotFoundException {
        UpdatePictureRequest pictureRequest = new UpdatePictureRequest(picture, "FIX ME", resourceID);
        BinaryHttpTask binaryHttpTask = new BinaryHttpTask();
        binaryHttpTask.makeRequest(pictureRequest);
    }

}
