package eu.h2020.sc.protocol.gson.adapter;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

import eu.h2020.sc.domain.Car;
import eu.h2020.sc.domain.PrivateTransport;
import eu.h2020.sc.domain.PublicTransport;
import eu.h2020.sc.domain.Ride;
import eu.h2020.sc.domain.Transport;
import eu.h2020.sc.domain.TravelMode;
import eu.h2020.sc.domain.User;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class TransportAdapter implements JsonDeserializer<Transport>, JsonSerializer<Transport> {

    @Override
    public Transport deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        JsonObject tripObject = json.getAsJsonObject();
        String travelModeElement = tripObject.get(Transport.TRAVEL_MODE).getAsString();


        if (travelModeElement.equals(Transport.CAR_POOLING)) {

            String riderID = tripObject.get(PrivateTransport.RIDE_ID).getAsString();

            String carJson = tripObject.getAsJsonObject(PrivateTransport.CAR).toString();
            Car car = Car.fromJson(carJson);

            String driverJson = tripObject.getAsJsonObject(PrivateTransport.DRIVER).toString();
            User driver = User.fromJson(driverJson);


            PrivateTransport privateTransport = new PrivateTransport(TravelMode.valueOf(travelModeElement), riderID, car, driver);

            JsonElement elementPublicUri = tripObject.get(PrivateTransport.PUBLIC_URI);
            String publicURI = null;
            if (elementPublicUri != null) {
                publicURI = tripObject.get(PrivateTransport.PUBLIC_URI).getAsString();
                privateTransport.setPublicURI(publicURI);
            }


            return privateTransport;

        } else {

            String travelMode = tripObject.get(Transport.TRAVEL_MODE).getAsString();
            String shortName = tripObject.get(PublicTransport.SHORT_NAME).getAsString();
            String longName = tripObject.get(PublicTransport.LONG_NAME).getAsString();

            return new PublicTransport(TravelMode.valueOf(travelMode), shortName, longName);
        }

    }

    @Override
    public JsonElement serialize(Transport transport, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject jsonTransport = new JsonObject();

        if (transport.getTravelMode().equals(TravelMode.CAR_POOLING)) {

            jsonTransport.addProperty(Transport.TRAVEL_MODE, Transport.CAR_POOLING);
            jsonTransport.addProperty(Ride.RIDE_ID, ((PrivateTransport) transport).getRideID());

            Car car = ((PrivateTransport) transport).getCar();

            jsonTransport.addProperty(Ride.CAR_ID, car.getId());
            jsonTransport.addProperty(Ride.DRIVER_ID, ((PrivateTransport) transport).getDriver().getId());

            String publicURI = ((PrivateTransport) transport).getPublicURI();

            if (publicURI != null) {
                jsonTransport.addProperty(PrivateTransport.PUBLIC_URI, publicURI);
            }

        } else {
            jsonTransport.addProperty(Transport.TRAVEL_MODE, transport.getTravelMode().toString());
            jsonTransport.addProperty(PublicTransport.SHORT_NAME, ((PublicTransport) transport).getShortName());
            jsonTransport.addProperty(PublicTransport.LONG_NAME, ((PublicTransport) transport).getLongName());
        }

        return jsonTransport;
    }
}
