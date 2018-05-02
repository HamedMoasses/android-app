package eu.h2020.sc.ui.waitingtime.task;

import eu.h2020.sc.domain.Transit;

import java.util.Comparator;

/**
 * Created by fabiolombardi on 20/01/17.
 */

public class TransitTimeComparator  implements Comparator<Transit>{
    @Override
    public int compare(Transit t1, Transit t2) {
        int w1 = t1.getWaitingTime();
        int w2 = t2.getWaitingTime();

        if(w1<0)w1=1000;
        if(w2<0)w2=1000;

        return w1 - w2;
    }
}
