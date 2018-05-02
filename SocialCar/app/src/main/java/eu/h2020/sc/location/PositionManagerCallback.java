package eu.h2020.sc.location;

import android.location.Location;

/**
 * @author Danilo Raspa <danilo.raspa@movenda.com>
 */
public interface PositionManagerCallback {

    //success
    void onPosition(Location location,int requestID);

    //error
    void networkError();

    void genericError(int errorType);

    void locationServiceDisable();

    void permissionDisabled(String permissionType);
}
