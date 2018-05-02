package eu.h2020.sc.dao.picture;

import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.protocol.UpdatePictureRequest;
import eu.h2020.sc.transport.BinaryHttpTask;

/**
 * Created by Pietro on 23/09/16.
 */

public class PictureCarDAO extends PictureDAO {

    @Override
    public byte[] findPictureByID(String carID) throws ConnectionException, NotFoundException, UnauthorizedException, ServerException {
        return null;
    }

    @Override
    public void createOrUpdate(byte[] picture, String resourceID) throws ConnectionException, NotFoundException, UnauthorizedException, ServerException {
        UpdatePictureRequest pictureRequest = new UpdatePictureRequest(picture, "", resourceID);
        BinaryHttpTask binaryHttpTask = new BinaryHttpTask();
        binaryHttpTask.makeRequest(pictureRequest);
    }
}
