package eu.h2020.sc.ui.reports.task;

import android.os.AsyncTask;

import org.json.JSONException;

import java.util.List;

import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.dao.ReportDAO;
import eu.h2020.sc.domain.Point;
import eu.h2020.sc.domain.report.Report;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.persistence.SocialCarStore;
import eu.h2020.sc.ui.reports.ReportsActivity;

/**
 * Created by pietro on 20/07/2017.
 */
public class FindReportAroundTask extends AsyncTask<Point, Void, Integer> {

    private static final int FIND_REPORTS_SERVER_ERROR_RESULT = 0;
    private static final int FIND_REPORTS_CONNECTION_ERROR_RESULT = 1;
    private static final int FIND_REPORTS_PARSER_ERROR_RESULT = 2;
    private static final int FIND_REPORTS_UNAUTHORIZED_ERROR_RESULT = 3;
    private static final int FIND_REPORTS_NOT_FOUND_ERROR_RESULT = 4;
    private static final int FIND_REPORTS_COMPLETED = 5;

    private ReportsActivity reportsActivity;
    private ReportDAO reportDAO;
    private SocialCarStore socialCarStore;
    private List<Report> reports;

    public FindReportAroundTask(ReportsActivity reportsActivity) {
        this.reportsActivity = reportsActivity;
        this.reportDAO = new ReportDAO();
        this.socialCarStore = SocialCarApplication.getInstance();
    }

    @Override
    protected Integer doInBackground(Point... points) {
        Point point = points[0];

        try {
            this.reports = reportDAO.findReportByPoint(point);
        } catch (ServerException e) {
            return FIND_REPORTS_SERVER_ERROR_RESULT;
        } catch (ConnectionException e) {
            return FIND_REPORTS_CONNECTION_ERROR_RESULT;
        } catch (UnauthorizedException e) {
            return FIND_REPORTS_UNAUTHORIZED_ERROR_RESULT;
        } catch (NotFoundException e) {
            return FIND_REPORTS_NOT_FOUND_ERROR_RESULT;
        } catch (JSONException e) {
            return FIND_REPORTS_PARSER_ERROR_RESULT;
        }
        return FIND_REPORTS_COMPLETED;
    }

    @Override
    protected void onPostExecute(Integer resultCode) {
        this.reportsActivity.dismissDialog();

        switch (resultCode) {
            case FIND_REPORTS_COMPLETED:
                if (this.reports.size() > 0) {
                    this.reportsActivity.showLayoutReportsList(this.reports);
                } else {
                    this.reportsActivity.showLayoutEmptyReports();
                }
                break;
            case FIND_REPORTS_CONNECTION_ERROR_RESULT:
                this.reportsActivity.showConnectionError();
                break;
            case FIND_REPORTS_NOT_FOUND_ERROR_RESULT:
                this.reportsActivity.showNotFoundError();
                break;
            case FIND_REPORTS_UNAUTHORIZED_ERROR_RESULT:
                this.socialCarStore.removeAllUserInfo();
                this.reportsActivity.showUnauthorizedError();
                this.reportsActivity.goToSignInActivity();
                break;
            case FIND_REPORTS_SERVER_ERROR_RESULT:
                this.reportsActivity.showServerGenericError();
                break;
        }
    }
}
