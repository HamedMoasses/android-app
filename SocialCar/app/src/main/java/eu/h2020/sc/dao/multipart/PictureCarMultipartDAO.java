/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */
package eu.h2020.sc.dao.multipart;

import android.net.Uri;
import android.util.Log;

import eu.h2020.sc.domain.CarPicture;
import eu.h2020.sc.domain.Picture;
import eu.h2020.sc.exception.ConflictException;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.protocol.CreateCarPictureRequest;
import eu.h2020.sc.protocol.FindMediaRequest;
import eu.h2020.sc.transport.GetHttpsTask;
import eu.h2020.sc.transport.MultipartPostTask;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */

public class PictureCarMultipartDAO extends PictureMultipartDAO {

    @Override
    public byte[] findPictureByID(String userID) throws ConnectionException, NotFoundException, UnauthorizedException, ServerException {
        return null;
    }

    @Override
    public byte[] findPictureByMediaUri(String mediaUri) throws ConnectionException, NotFoundException, UnauthorizedException, ServerException {
        FindMediaRequest findMediaRequest = new FindMediaRequest(mediaUri);
        GetHttpsTask getHttpsTask = new GetHttpsTask();
        return getHttpsTask.makeBinaryRequest(findMediaRequest);
    }

    @Override
    public Picture createOrUpdate(String resourceID, Uri mediaFileUri) throws ConnectionException, NotFoundException, UnauthorizedException, ServerException, ConflictException {

        CreateCarPictureRequest pictureRequest = new CreateCarPictureRequest(resourceID, mediaFileUri);

        MultipartPostTask multipartPostTask = new MultipartPostTask();
        String jsonResult = multipartPostTask.makeRequest(pictureRequest);

        Log.i(PictureCarMultipartDAO.class.getCanonicalName(), jsonResult);

        return CarPicture.fromJson(jsonResult);
    }
}
