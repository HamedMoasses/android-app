package eu.h2020.sc.socialnetwork;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */

public interface RetrievePhotoListener {

    void onPhotoCached();

    void onPhotoCachedError();
}
