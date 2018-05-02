package eu.h2020.sc.ui.waitingtime.task;

import eu.h2020.sc.domain.Transit;

import java.util.Comparator;

/**
 * Created by fabiolombardi on 20/01/17.
 */

public class TransitStopDistanceComparator implements Comparator<Transit>{
    @Override
    public int compare(Transit t1, Transit t2) {

        int d1 = t1.getStopDistance();
        int d2 = t2.getStopDistance();

        if(d1<0)d1=1000;
        if(d2<0)d2=1000;

        return d1 - d2;
    }
}
