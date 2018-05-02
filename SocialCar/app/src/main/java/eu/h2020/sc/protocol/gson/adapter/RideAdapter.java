package eu.h2020.sc.protocol.gson.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.domain.Lift;
import eu.h2020.sc.domain.Point;
import eu.h2020.sc.domain.Ride;
import eu.h2020.sc.domain.Route;
import eu.h2020.sc.domain.TimeSpacePoint;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

/**
 * @author Danilo Raspa <danilo.raspa@movenda.com>
 */

    public class RideAdapter implements JsonDeserializer<Ride>, JsonSerializer<Ride> {

    @Override
    public Ride deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject rideJsonObject = json.getAsJsonObject();

        String id = rideJsonObject.get(Ride.ID).getAsString();
        String name = rideJsonObject.get(Ride.NAME).getAsString();
        String polyline = rideJsonObject.get(Ride.POLYLINE).getAsString();
        String carID = rideJsonObject.get(Ride.CAR_ID).getAsString();
        String driverID = rideJsonObject.get(Ride.DRIVER_ID).getAsString();
        boolean activated = rideJsonObject.get(Ride.ACTIVATED).getAsBoolean();

        List<Lift> lifts = SocialCarApplication.getGson().fromJson(rideJsonObject.get(Ride.LIFTS), new TypeToken<List<Lift>>() {
        }.getType());

        Date dateStart = SocialCarApplication.getGson().fromJson(rideJsonObject.get(Ride.DATE), Date.class);
        Point startPoint = SocialCarApplication.getGson().fromJson(rideJsonObject.get(Ride.START_POINT), Point.class);
        Point endPoint = SocialCarApplication.getGson().fromJson(rideJsonObject.get(Ride.END_POINT), Point.class);

        TimeSpacePoint startTimeSpacePoint = new TimeSpacePoint(startPoint, dateStart);
        TimeSpacePoint endTimeSpacePoint = new TimeSpacePoint(endPoint, null);
        Route route = new Route(startTimeSpacePoint, endTimeSpacePoint);

        Ride ride = new Ride(name, route, polyline, carID, driverID);
        ride.setId(id);
        ride.addAll(lifts);
        ride.setActivated(activated);

        return ride;
    }

    @Override
    public JsonElement serialize(Ride ride, Type typeOfSrc, JsonSerializationContext context) {
        SocialCarApplication.getGsonBuilder().excludeFieldsWithoutExposeAnnotation();

        JsonObject jsonRide = new JsonObject();

        jsonRide.addProperty(Ride.ID, ride.getId());
        jsonRide.addProperty(Ride.NAME, ride.getName());
        jsonRide.addProperty(Ride.POLYLINE, ride.getPolyline());
        jsonRide.addProperty(Ride.CAR_ID, ride.getCarId());
        jsonRide.addProperty(Ride.DRIVER_ID, ride.getDriverId());
        jsonRide.addProperty(Ride.ACTIVATED, ride.isActivated());

        Point startPoint = new Point(ride.getRoute().getStartPoint().getPoint().getLat(), ride.getRoute().getStartPoint().getPoint().getLon());
        Point endPoint = new Point(ride.getRoute().getEndPoint().getPoint().getLat(), ride.getRoute().getEndPoint().getPoint().getLon());

        Date dateStart = ride.getRoute().getStartPoint().getDate();

        jsonRide.add(Ride.START_POINT, SocialCarApplication.getGson().toJsonTree(startPoint));
        jsonRide.add(Ride.END_POINT, SocialCarApplication.getGson().toJsonTree(endPoint));
        jsonRide.addProperty(Ride.DATE, SocialCarApplication.getGsonBuilder().create().toJson(dateStart));

        return jsonRide;
    }
}
