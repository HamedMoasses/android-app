package eu.h2020.sc.dao;

import eu.h2020.sc.domain.Car;
import eu.h2020.sc.exception.ConflictException;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.protocol.CreateCarRequest;
import eu.h2020.sc.protocol.GetCarRequest;
import eu.h2020.sc.protocol.SocialCarRequest;
import eu.h2020.sc.transport.GetHttpsTask;
import eu.h2020.sc.transport.PostHttpTask;

/**
 * Created by Pietro on 05/10/16.
 */

public class CarDAO {

    public Car createCar(Car car) throws ServerException, ConnectionException, ConflictException, UnauthorizedException {

        SocialCarRequest request = new CreateCarRequest(car);
        PostHttpTask postHttpTask = new PostHttpTask();
        String json = postHttpTask.makeRequest(request);

        return Car.fromJson(json);
    }

    public Car findCarByID(String carID) throws ConnectionException, NotFoundException, UnauthorizedException, ServerException {

        SocialCarRequest request = new GetCarRequest(carID);

        GetHttpsTask getHttpsTask = new GetHttpsTask();
        String json = getHttpsTask.makeRequest(request);

        return Car.fromJson(json);
    }
}
