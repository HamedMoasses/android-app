package eu.h2020.sc.ui.reports.creation.task;

import android.os.AsyncTask;

import org.json.JSONException;

import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.dao.ReportDAO;
import eu.h2020.sc.domain.report.Report;
import eu.h2020.sc.exception.ConflictException;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.persistence.SocialCarStore;
import eu.h2020.sc.ui.reports.creation.PositionReportActivity;

/**
 * Created by pietro on 24/07/2017.
 */
public class ReportCreationTask extends AsyncTask<Report, Void, Integer> {

    private static final int CREATE_REPORT_SERVER_ERROR_RESULT = 0;
    private static final int CREATE_REPORT_CONNECTION_ERROR_RESULT = 1;
    private static final int CREATE_REPORT_UNAUTHORIZED_ERROR_RESULT = 2;
    private static final int CREATE_REPORT_CONFLICT_ERROR_RESULT = 3;
    private static final int CREATE_REPORT_COMPLETED = 4;

    private PositionReportActivity positionReportActivity;
    private ReportDAO reportDAO;
    private SocialCarStore socialCarStore;

    private Report report;

    public ReportCreationTask(PositionReportActivity positionReportActivity) {
        this.positionReportActivity = positionReportActivity;
        this.reportDAO = new ReportDAO();
        this.socialCarStore = SocialCarApplication.getInstance();
    }

    @Override
    protected Integer doInBackground(Report... reports) {
        Report report = reports[0];

        try {
            this.report = this.reportDAO.createReport(report);
        } catch (ConnectionException e) {
            return CREATE_REPORT_CONNECTION_ERROR_RESULT;
        } catch (ConflictException e) {
            return CREATE_REPORT_CONFLICT_ERROR_RESULT;
        } catch (UnauthorizedException e) {
            return CREATE_REPORT_UNAUTHORIZED_ERROR_RESULT;
        } catch (ServerException | JSONException e) {
            return CREATE_REPORT_SERVER_ERROR_RESULT;
        }
        return CREATE_REPORT_COMPLETED;
    }

    @Override
    protected void onPostExecute(Integer resultCode) {
        this.positionReportActivity.dismissDialog();

        switch (resultCode) {
            case CREATE_REPORT_COMPLETED:
                this.positionReportActivity.goToPostReportFacebook(this.report);
                break;
            case CREATE_REPORT_CONNECTION_ERROR_RESULT:
                this.positionReportActivity.showConnectionError();
                break;
            case CREATE_REPORT_UNAUTHORIZED_ERROR_RESULT:
                this.socialCarStore.removeAllUserInfo();
                this.positionReportActivity.showUnauthorizedError();
                this.positionReportActivity.goToSignInActivity();
                break;
            case CREATE_REPORT_SERVER_ERROR_RESULT:
                this.positionReportActivity.showServerGenericError();
                break;
        }
    }
}
