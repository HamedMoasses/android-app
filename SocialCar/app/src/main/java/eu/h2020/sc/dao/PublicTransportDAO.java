package eu.h2020.sc.dao;

import eu.h2020.sc.domain.Point;
import eu.h2020.sc.domain.Stop;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.protocol.FindStopsAroundRequest;
import eu.h2020.sc.protocol.GetWaitingTimeRequest;
import eu.h2020.sc.protocol.SocialCarRequest;
import eu.h2020.sc.transport.GetHttpsTask;

import java.util.List;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */

public class PublicTransportDAO {

    public Stop getWaitingTime(String stopCode) throws ConnectionException, NotFoundException, UnauthorizedException, ServerException {
        GetWaitingTimeRequest getWaitingTimeRequest = new GetWaitingTimeRequest(stopCode);
        GetHttpsTask getHttpsTask = new GetHttpsTask();
        String json = getHttpsTask.makeRequest(getWaitingTimeRequest);
        return Stop.fromJson(json);
    }

    public List<Stop> findStopsAround(Point point) throws ConnectionException, NotFoundException, UnauthorizedException, ServerException {

        SocialCarRequest request = new FindStopsAroundRequest(point);
        GetHttpsTask getHttpsTask = new GetHttpsTask();
        String jsonStopList = getHttpsTask.makeRequest(request);
        return Stop.fromJsonToStopList(jsonStopList);
    }
}
