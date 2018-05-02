package eu.h2020.sc.dao;

import eu.h2020.sc.domain.google.direction.DirectionResponse;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.protocol.GoogleDirectionRequest;
import eu.h2020.sc.transport.GetHttpTask;

/**
 * @author Danilo Raspa <danilo.raspa@movenda.com>
 */

public class GoogleDirectionDAO {

    public DirectionResponse getDirection(String originAddress, String destinationAddress) throws ConnectionException, NotFoundException, UnauthorizedException, ServerException {
        GoogleDirectionRequest googleDirectionRequest = new GoogleDirectionRequest(originAddress, destinationAddress);
        GetHttpTask getHttpTask = new GetHttpTask();
        String json = getHttpTask.makeRequest(googleDirectionRequest);
        return DirectionResponse.fromJson(json);
    }


}
