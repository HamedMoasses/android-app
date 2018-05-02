package eu.h2020.sc.ui.messages.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import java.util.Date;

import eu.h2020.sc.R;
import eu.h2020.sc.domain.messages.Message;
import eu.h2020.sc.utils.DateUtils;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */

public class MessageReceiverViewHolder extends RecyclerView.ViewHolder {

    private TextView textViewMessage;
    private TextView textViewTime;

    public MessageReceiverViewHolder(View itemView) {
        super(itemView);
        this.textViewMessage = (TextView) itemView.findViewById(R.id.text_view_receiver_body);
        this.textViewTime = (TextView) itemView.findViewById(R.id.text_view_receiver_time);
    }

    public void init(Message message) {
        this.textViewMessage.setText(message.getBody());
        this.textViewTime.setText(DateUtils.formatToHoursAndMinutes(new Date(message.getTimestamp() * 1000L), false));
    }
}
