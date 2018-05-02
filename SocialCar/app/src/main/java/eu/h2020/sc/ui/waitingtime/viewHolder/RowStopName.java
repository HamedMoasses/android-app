package eu.h2020.sc.ui.waitingtime.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import eu.h2020.sc.R;
import eu.h2020.sc.domain.Stop;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class RowStopName extends RecyclerView.ViewHolder {

    public TextView textViewStopName;


    public RowStopName(View itemView) {
        super(itemView);
        this.textViewStopName = (TextView) itemView.findViewById(R.id.stop_name);
    }

    public void init(Stop stop) {
        this.textViewStopName.setText(String.format("%s(%s)", stop.getName(), stop.getStopCode()));
    }
}
