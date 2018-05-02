package eu.h2020.sc.ui.drawer.task;

import android.os.AsyncTask;

import org.json.JSONException;

import java.util.List;

import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.dao.LiftDAO;
import eu.h2020.sc.domain.Lift;
import eu.h2020.sc.domain.LiftType;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.persistence.SocialCarStore;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */

public class RetrieveLiftAsPassengerTask extends AsyncTask<Void, Void, Integer> {

    private static final int RETRIEVE_LIFT_SERVER_ERROR_RESULT = 0;
    private static final int RETRIEVE_LIFT_CONNECTION_ERROR_RESULT = 1;
    private static final int RETRIEVE_LIFT_PARSER_ERROR_RESULT = 2;
    private static final int RETRIEVE_LIFT_UNAUTHORIZED = 3;
    private static final int RETRIEVE_LIFT_NOT_FOUND = 4;
    private static final int RETRIEVE_LIFT_COMPLETED = 5;

    private RendezvousLiftTask taskExecutor;

    private SocialCarStore socialCarStore;
    private LiftDAO liftDAO;
    private List<Lift> lifts;


    public RetrieveLiftAsPassengerTask(RendezvousLiftTask taskExecutor) {
        this.taskExecutor = taskExecutor;
        this.liftDAO = new LiftDAO();
        this.socialCarStore = SocialCarApplication.getInstance();
    }

    @Override
    protected Integer doInBackground(Void... voids) {

        if (this.socialCarStore.getUser() == null)
            return RETRIEVE_LIFT_SERVER_ERROR_RESULT;

        String userID = this.socialCarStore.getUser().getId();

        try {
            this.lifts = this.liftDAO.findLift(userID, LiftType.REQUESTED);
            return RETRIEVE_LIFT_COMPLETED;

        } catch (ConnectionException e) {
            return RETRIEVE_LIFT_CONNECTION_ERROR_RESULT;

        } catch (NotFoundException e) {
            return RETRIEVE_LIFT_NOT_FOUND;

        } catch (UnauthorizedException e) {
            return RETRIEVE_LIFT_UNAUTHORIZED;

        } catch (ServerException e) {
            return RETRIEVE_LIFT_SERVER_ERROR_RESULT;

        } catch (JSONException e) {
            return RETRIEVE_LIFT_PARSER_ERROR_RESULT;
        }
    }

    @Override
    protected void onPostExecute(Integer resultCode) {

        switch (resultCode) {

            case RETRIEVE_LIFT_SERVER_ERROR_RESULT:
            case RETRIEVE_LIFT_PARSER_ERROR_RESULT:
            case RETRIEVE_LIFT_NOT_FOUND:
                this.taskExecutor.serverError();
                break;

            case RETRIEVE_LIFT_CONNECTION_ERROR_RESULT:
                this.taskExecutor.noConnectionError();
                break;

            case RETRIEVE_LIFT_UNAUTHORIZED:
                this.taskExecutor.unauthorizedError();
                break;

            case RETRIEVE_LIFT_COMPLETED:
                this.taskExecutor.counterTasksResult++;
                this.taskExecutor.onSuccess();
                break;
        }
    }

    public List<Lift> getLifts() {
        return lifts;
    }
}
