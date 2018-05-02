package eu.h2020.sc.domain;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class Transit implements Serializable {

    @Expose
    @SerializedName(value = "transport")
    private PublicTransport transport;

    @Expose
    @SerializedName(value = "waiting_time")
    private int waitingTime;

    @Expose
    @SerializedName(value = "stop_distance")
    private int stopDistance;

    @Expose
    @SerializedName(value = "terminus_departure_time")
    private List<SimpleTime> terminusDepartures;

    public Transit() {
        this.waitingTime = -1;
        this.stopDistance = -1;
        this.terminusDepartures = new ArrayList<>();
    }

    public Transit(int waitingTime, int stopDistance, PublicTransport transport, List<SimpleTime> terminusDepartures) {
        this.waitingTime = waitingTime;
        this.stopDistance = stopDistance;
        this.transport = transport;
        this.terminusDepartures = terminusDepartures;
    }

    public PublicTransport getPublicTransport() {
        return transport;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public int getStopDistance() {
        return stopDistance;
    }

    public List<SimpleTime> getTerminusDepartures() {
        return terminusDepartures;
    }

    @Override
    public String toString() {
        return "Transit{" +
                "transport=" + transport +
                ", waitingTime=" + waitingTime +
                ", stopDistance=" + stopDistance +
                ", terminusDepartures=" + terminusDepartures +
                '}';
    }
}
