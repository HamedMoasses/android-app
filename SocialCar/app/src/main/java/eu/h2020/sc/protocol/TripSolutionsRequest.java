package eu.h2020.sc.protocol;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import eu.h2020.sc.config.SystemConfiguration;
import eu.h2020.sc.domain.Point;
import eu.h2020.sc.domain.TimeRange;
import eu.h2020.sc.domain.TransferMode;
import eu.h2020.sc.domain.TravelMode;
import eu.h2020.sc.transport.HttpConstants;
import eu.h2020.sc.utils.DateUtils;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class TripSolutionsRequest extends SocialCarRequest implements Parcelable {

    private static final String TAG = TripSolutionsRequest.class.getSimpleName();

    public static final String URI = "/trips?";
    public static final String TRIP_REQUEST_KEY = "TRIP_REQUEST_KEY";

    private TimeRange timeRange;
    private TravelMode[] travelModes;
    private TransferMode transferMode;
    private Point startPoint;
    private Point endPoint;

    public TripSolutionsRequest(Point startPoint, Point endPoint, TravelMode[] travelModes, TransferMode transferMode) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.travelModes = travelModes;
        this.transferMode = transferMode;
    }

    public void setTimeRange(TimeRange timeRange) {
        this.timeRange = timeRange;
    }

    @Override
    public URL urllize() {

        try {
            StringBuilder sb = new StringBuilder(SystemConfiguration.HTTP_PROTOCOL);
            sb.append(SystemConfiguration.HOST).append(":").append(SystemConfiguration.HTTP_PORT);
            sb.append(SystemConfiguration.CONTEXT_ROOT);
            sb.append(SystemConfiguration.SERVER_PATH_VERSION).append(URI);

            Uri.Builder builder = Uri.parse(sb.toString()).buildUpon()
                    .appendQueryParameter("start_lat", String.valueOf(this.startPoint.getLat()))
                    .appendQueryParameter("start_lon", String.valueOf(this.startPoint.getLon()))
                    .appendQueryParameter("end_lat", String.valueOf(this.endPoint.getLat()))
                    .appendQueryParameter("end_lon", String.valueOf(this.endPoint.getLon()))
                    .appendQueryParameter("start_date", Long.toString(DateUtils.toUnixTimestamp(this.timeRange.getStartDate())))
                    .appendQueryParameter("end_date", Long.toString(DateUtils.toUnixTimestamp(this.timeRange.getEndDate())));

            List<TravelMode> prefs = Arrays.asList(this.travelModes);

            builder.appendQueryParameter("use_bus", String.valueOf(prefs.contains(TravelMode.BUS)));
            builder.appendQueryParameter("use_metro", String.valueOf(prefs.contains(TravelMode.METRO)));
            builder.appendQueryParameter("use_train", String.valueOf(prefs.contains(TravelMode.RAIL)));

            // TODO enable it after back-end modifies the API
            //builder.appendQueryParameter("use_carpooling", String.valueOf(prefs.contains(TravelMode.BUS)));
            //builder.appendQueryParameter("use_tram", String.valueOf(prefs.contains(TravelMode.METRO)));
            //builder.appendQueryParameter("use_walk", String.valueOf(prefs.contains(TravelMode.RAIL)));

            builder.appendQueryParameter("transfer_mode", this.transferMode.toString());

            return new URL(builder.build().toString());

        } catch (MalformedURLException e) {
            Log.e(TAG, "Unable to build a valid URL...", e);
        }
        return null;
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public Point getEndPoint() {
        return endPoint;
    }

    public TravelMode[] getTravelModes() {
        return travelModes;
    }

    public void setTravelModes(TravelMode[] travelModes) {
        this.travelModes = travelModes;
    }

    public TransferMode getTransferMode() {
        return transferMode;
    }

    public void setTransferMode(TransferMode transferMode) {
        this.transferMode = transferMode;
    }

    public TimeRange getTimeRange() {
        return timeRange;
    }

    @Override
    public String getBody() {
        return null;
    }

    @Override
    public String getApiName() {
        return TAG;
    }

    @Override
    public String getHttpMethod() {
        return HttpConstants.GET;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.timeRange, flags);
        dest.writeTypedArray(this.travelModes, flags);
        dest.writeInt(this.transferMode == null ? -1 : this.transferMode.ordinal());
        dest.writeSerializable(this.startPoint);
        dest.writeSerializable(this.endPoint);
    }

    protected TripSolutionsRequest(Parcel in) {
        this.timeRange = in.readParcelable(TimeRange.class.getClassLoader());
        this.travelModes = in.createTypedArray(TravelMode.CREATOR);
        int tmpTransferMode = in.readInt();
        this.transferMode = tmpTransferMode == -1 ? null : TransferMode.values()[tmpTransferMode];
        this.startPoint = (Point) in.readSerializable();
        this.endPoint = (Point) in.readSerializable();
    }

    public static final Creator<TripSolutionsRequest> CREATOR = new Creator<TripSolutionsRequest>() {
        @Override
        public TripSolutionsRequest createFromParcel(Parcel source) {
            return new TripSolutionsRequest(source);
        }

        @Override
        public TripSolutionsRequest[] newArray(int size) {
            return new TripSolutionsRequest[size];
        }
    };


    public static TripSolutionsRequest fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, TripSolutionsRequest.class);
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

}
