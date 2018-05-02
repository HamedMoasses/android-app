package eu.h2020.sc.ui.home.driver.becomedriver;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import eu.h2020.sc.R;
import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.dao.CarDAO;
import eu.h2020.sc.dao.multipart.PictureCarMultipartDAO;
import eu.h2020.sc.dao.multipart.PictureMultipartDAO;
import eu.h2020.sc.domain.Car;
import eu.h2020.sc.domain.CarPicture;
import eu.h2020.sc.domain.User;
import eu.h2020.sc.exception.ConflictException;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.persistence.SocialCarStore;
import eu.h2020.sc.utils.ImageUtils;

import java.io.IOException;

/**
 * Created by Pietro on 20/09/16.
 */
public class BecomeDriverTask extends AsyncTask<Car, Void, Integer> {

    private static final int BECOME_DRIVER_GENERIC_ERROR_RESULT = 0;
    private static final int BECOME_DRIVER_CONNECTION_ERROR_RESULT = 1;
    private static final int BECOME_DRIVER_SERVER_ERROR_RESULT = 2;
    private static final int BECOME_DRIVER_UNAUTHORIZED = 3;
    private static final int BECOME_DRIVER_CONFLICT = 4;
    private static final int BECOME_DRIVER_COMPLETED_RESULT = 5;
    private static final int BECOME_DRIVER_COMPLETED_WITHOUT_PHOTO = 6;

    private final String TAG = this.getClass().getName();

    private BecomeDriverActivity becomeDriverActivity;
    private CarDAO carDAO;
    //private PictureDAO pictureDAO;
    private PictureMultipartDAO multipartDAO;
    private SocialCarStore socialCarStore;

    public BecomeDriverTask(BecomeDriverActivity becomeDriverActivity) {
        this.becomeDriverActivity = becomeDriverActivity;
        this.carDAO = new CarDAO();
        //this.pictureDAO = new PictureCarDAO();
        this.multipartDAO = new PictureCarMultipartDAO();
        this.socialCarStore = SocialCarApplication.getInstance();
    }

    @Override
    protected Integer doInBackground(Car... cars) {

        Car car = cars[0];
        User user = this.socialCarStore.getUser();
        car.setOwnerID(user.getId());

        try {
            car = this.carDAO.createCar(car);
            user.addCarID(car.getId());

            this.socialCarStore.store(car);
            this.socialCarStore.store(user);

            Bitmap carPictureBitmap = this.becomeDriverActivity.getSelectedPictureCar();

            if (carPictureBitmap != null) {
                try {

                    Bitmap carPictureToUpload = ImageUtils.generateBitmapToUpload(carPictureBitmap, this.becomeDriverActivity.getApplicationContext());

                    //FIXME: REMOVE MULTIPART POST!!
                    Uri carPictureUri = ImageUtils.getUriFromBitmap(carPictureToUpload, this.becomeDriverActivity);
                    CarPicture carPicture = (CarPicture) this.multipartDAO.createOrUpdate(car.getId(), carPictureUri);
                    Log.i(TAG, carPicture.toString());

                    car.addPicture(carPicture);

                } catch (ServerException | ConnectionException | UnauthorizedException | NotFoundException | IOException e) {
                    Log.e(TAG, "Error during uploading car picture...", e);
                    return BECOME_DRIVER_COMPLETED_WITHOUT_PHOTO;
                }
            }

        } catch (ServerException e) {
            return BECOME_DRIVER_SERVER_ERROR_RESULT;
        } catch (ConnectionException e) {
            return BECOME_DRIVER_CONNECTION_ERROR_RESULT;
        } catch (UnauthorizedException e) {
            return BECOME_DRIVER_UNAUTHORIZED;
        } catch (ConflictException e) {
            return BECOME_DRIVER_CONFLICT;
        }
        return BECOME_DRIVER_COMPLETED_RESULT;
    }


    @Override
    protected void onPostExecute(final Integer resultCode) {
        this.becomeDriverActivity.dismissDialog();

        switch (resultCode) {
            case BECOME_DRIVER_GENERIC_ERROR_RESULT:
                this.becomeDriverActivity.showServerGenericError();
                break;
            case BECOME_DRIVER_CONNECTION_ERROR_RESULT:
                this.becomeDriverActivity.showConnectionError();
                break;
            case BECOME_DRIVER_CONFLICT:
            case BECOME_DRIVER_SERVER_ERROR_RESULT:
                this.becomeDriverActivity.showServerGenericError();
                break;
            case BECOME_DRIVER_UNAUTHORIZED:
                this.socialCarStore.removeAllUserInfo();
                this.becomeDriverActivity.showUnauthorizedError();
                this.becomeDriverActivity.goToSignInActivity();
                break;
            case BECOME_DRIVER_COMPLETED_RESULT:
                this.becomeDriverActivity.closeActivity();
                break;
            case BECOME_DRIVER_COMPLETED_WITHOUT_PHOTO:
                this.becomeDriverActivity.showUploadProblem();
                this.becomeDriverActivity.closeActivity();
                break;
            default:
                this.becomeDriverActivity.showServerGenericError();
        }
    }


    public boolean validateCar(Car car) {
        boolean valid = true;
        if (!car.isModelValid()) {
            this.becomeDriverActivity.showModelMarkerError();
            valid = false;
        }
        if (!car.isPlateValid()) {
            this.becomeDriverActivity.showPlateMarkerError();
            valid = false;
        }

        if (!car.isSeatsValid()) {
            this.becomeDriverActivity.showSeatsMarkerError();
            valid = false;
        }

        String hexTransparentColour = String.format("#%s", ContextCompat.getColor(this.becomeDriverActivity, R.color.transparent));

        if (car.getColour().equals(hexTransparentColour)) {
            this.becomeDriverActivity.showColourMarkerError();
            valid = false;
        }

        if (!valid) {
            this.becomeDriverActivity.showMissingFieldsMessage();
            this.becomeDriverActivity.hideSoftKeyboard();
        }

        return valid;
    }
}