package eu.h2020.sc.ui.lift;


import android.os.AsyncTask;
import android.util.Log;

import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.dao.LiftDAO;
import eu.h2020.sc.domain.Lift;
import eu.h2020.sc.domain.LiftType;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.persistence.SocialCarStore;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Pietro on 28/09/16.
 */

public class RetrieveLiftTask extends AsyncTask<LiftType, Void, Integer> {

    private final String TAG = RetrieveLiftTask.class.getCanonicalName();

    private static final int RETRIEVE_LIFT_SERVER_ERROR_RESULT = 0;
    private static final int RETRIEVE_LIFT_CONNECTION_ERROR_RESULT = 1;
    private static final int RETRIEVE_LIFT_PARSER_ERROR_RESULT = 2;
    private static final int RETRIEVE_LIFT_UNAUTHORIZED = 3;
    private static final int RETRIEVE_LIFT_NOT_FOUND = 4;
    private static final int RETRIEVE_LIFT_COMPLETED_WITH_DATA = 5;
    private static final int RETRIEVE_LIFT_COMPLETED_NO_DATA = 6;

    private LiftFragment liftFragment;
    private SocialCarStore socialCarStore;
    private LiftDAO liftDAO;
    private List<Lift> lifts;

    public RetrieveLiftTask(LiftFragment liftFragment) {
        this.liftFragment = liftFragment;
        this.liftDAO = new LiftDAO();
        this.socialCarStore = SocialCarApplication.getInstance();
    }

    @Override
    protected Integer doInBackground(LiftType... liftTypes) {

        String userID = this.socialCarStore.getUser().getId();

        LiftType liftType = liftTypes[0];

        try {
            this.lifts = this.liftDAO.findLift(userID, liftType);
            if (lifts.size() < 1)
                return RETRIEVE_LIFT_COMPLETED_NO_DATA;
            else
                return RETRIEVE_LIFT_COMPLETED_WITH_DATA;
        } catch (ConnectionException e) {
            return RETRIEVE_LIFT_CONNECTION_ERROR_RESULT;
        } catch (NotFoundException e) {
            return RETRIEVE_LIFT_NOT_FOUND;
        } catch (UnauthorizedException e) {
            return RETRIEVE_LIFT_UNAUTHORIZED;
        } catch (ServerException e) {
            return RETRIEVE_LIFT_SERVER_ERROR_RESULT;
        } catch (JSONException e) {
            Log.e(TAG, "is JSON compliant with the protocol? Check it! ", e);
            return RETRIEVE_LIFT_PARSER_ERROR_RESULT;
        }
    }

    @Override
    protected void onPostExecute(Integer resultCode) {
        this.liftFragment.dismissLoader();

        switch (resultCode) {
            case RETRIEVE_LIFT_COMPLETED_NO_DATA:
                this.liftFragment.showLayoutEmptyLift();
                break;
            case RETRIEVE_LIFT_COMPLETED_WITH_DATA:
                List<Lift> sortedLifts = sortLiftsInReverseOrder(lifts);
                this.liftFragment.showLayoutLiftsList(sortedLifts);
                break;
            case RETRIEVE_LIFT_CONNECTION_ERROR_RESULT:
                this.liftFragment.showConnectionError();
                break;
            case RETRIEVE_LIFT_NOT_FOUND:
                this.liftFragment.showNotFoundError();
                break;
            case RETRIEVE_LIFT_UNAUTHORIZED:
                this.socialCarStore.removeAllUserInfo();
                this.liftFragment.showUnauthorizedError();
                this.liftFragment.goToSignInActivity();
                break;
            case RETRIEVE_LIFT_SERVER_ERROR_RESULT:
                this.liftFragment.showServerGenericError();
                break;
            case RETRIEVE_LIFT_PARSER_ERROR_RESULT:
                this.liftFragment.showLayoutErrorParser();
                break;
        }
    }

    private List<Lift> sortLiftsInReverseOrder(List<Lift> lifts) {
        List<Lift> sortedLifts = new ArrayList<>();
        for (int i = lifts.size() - 1; i >= 0; i--) {
            sortedLifts.add(lifts.get(i));
        }
        return sortedLifts;
    }
}
