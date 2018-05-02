package eu.h2020.sc.dao;

import org.json.JSONException;

import java.util.List;

import eu.h2020.sc.domain.Point;
import eu.h2020.sc.domain.report.Report;
import eu.h2020.sc.exception.ConflictException;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.protocol.CreateReportRequest;
import eu.h2020.sc.protocol.FindReportsAroundRequest;
import eu.h2020.sc.protocol.SocialCarRequest;
import eu.h2020.sc.transport.GetHttpsTask;
import eu.h2020.sc.transport.PostHttpTask;

/**
 * Created by pietro on 20/07/2017.
 */

public class ReportDAO {

    public List<Report> findReportByPoint(Point point) throws ServerException, ConnectionException, UnauthorizedException, NotFoundException, JSONException {
        SocialCarRequest request = new FindReportsAroundRequest(point);

        GetHttpsTask getHttpsTask = new GetHttpsTask();
        String json = getHttpsTask.makeRequest(request);

        return Report.fromJsonToReportList(json);
    }

    public Report createReport(Report report) throws ConnectionException, ConflictException, UnauthorizedException, ServerException, JSONException {
        SocialCarRequest request = new CreateReportRequest(report);

        PostHttpTask postHttpTask = new PostHttpTask();
        String json = postHttpTask.makeRequest(request);

        return Report.fromJsonToReport(json);
    }
}
