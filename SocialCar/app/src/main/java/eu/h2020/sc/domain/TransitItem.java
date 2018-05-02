package eu.h2020.sc.domain;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */

public class TransitItem extends Transit {

    private boolean lineMonitored;

    public TransitItem(Transit transit) {
        super(transit.getWaitingTime(), transit.getStopDistance(), transit.getPublicTransport(), transit.getTerminusDepartures());
        this.lineMonitored = false;
    }

    public TransitItem() {
        super();
        this.lineMonitored = false;
    }

    public boolean isLineMonitored() {
        return lineMonitored;
    }

    public void setLineMonitored(boolean lineMonitored) {
        this.lineMonitored = lineMonitored;
    }

    public boolean isRealTime() {
        return (getWaitingTime() != -1 && getStopDistance() != -1);
    }

    public boolean isOffline() {
        return (getWaitingTime() == -1 && getStopDistance() == -1);
    }

    public boolean isArriving() {
        return (getWaitingTime() < 2 && getStopDistance() <= 1);
    }

    public boolean isNotMonitored() {
        return (getWaitingTime() == -1 && getStopDistance() == -1 && getTerminusDepartures().isEmpty());
    }
}
