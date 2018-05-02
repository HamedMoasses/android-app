package eu.h2020.sc.ui.drawer.task;

import java.util.ArrayList;
import java.util.List;

import eu.h2020.sc.domain.Lift;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */

public class LiftsController {

    private static LiftsController instance = null;

    private List<Lift> requestedLifts;
    private List<Lift> offeredLifts;

    private LiftsController() {
        this.requestedLifts = new ArrayList<>();
        this.offeredLifts = new ArrayList<>();
    }

    public static LiftsController getInstance() {
        if (instance == null) {
            instance = new LiftsController();
        }
        return instance;
    }

    public boolean areAllLiftsReviewed() {

        try {

            for (Lift lift : this.requestedLifts) {

                if (lift.isCompleted())
                    return false;
            }

            for (Lift lift : this.offeredLifts) {

                if (lift.isCompleted())
                    return false;
            }

            return true;

        } catch (Throwable e) {
            return true;
        }
    }


    public synchronized void syncRequestedLifts(List<Lift> requestedLifts) {
        this.requestedLifts.clear();
        this.requestedLifts.addAll(requestedLifts);
    }

    public synchronized void syncOfferedLifts(List<Lift> offeredLifts) {
        this.offeredLifts.clear();
        this.offeredLifts.addAll(offeredLifts);
    }

    public void clearLifts() {
        this.requestedLifts.clear();
        this.offeredLifts.clear();
    }
}
