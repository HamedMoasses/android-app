package eu.h2020.sc.dao.picture;

import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;

/**
 * Created by Pietro on 23/09/16.
 */

public abstract class PictureDAO {

    public abstract byte[] findPictureByID(String resourceID) throws ConnectionException, NotFoundException, UnauthorizedException, ServerException;

    public abstract void createOrUpdate(byte[] picture, String resourceID) throws ConnectionException, NotFoundException, UnauthorizedException, ServerException;
}
