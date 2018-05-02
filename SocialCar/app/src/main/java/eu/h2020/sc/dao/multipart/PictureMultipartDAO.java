package eu.h2020.sc.dao.multipart;

import android.net.Uri;

import eu.h2020.sc.domain.Picture;
import eu.h2020.sc.exception.ConflictException;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;


/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public abstract class PictureMultipartDAO {

    public abstract byte[] findPictureByID(String userID) throws ConnectionException, NotFoundException, UnauthorizedException, ServerException;

    public abstract byte[] findPictureByMediaUri(String mediaUri) throws ConnectionException, NotFoundException, UnauthorizedException, ServerException;

    public abstract Picture createOrUpdate(String resourceID, Uri mediaFileUri) throws ConnectionException, NotFoundException, UnauthorizedException, ServerException, ConflictException;
}
