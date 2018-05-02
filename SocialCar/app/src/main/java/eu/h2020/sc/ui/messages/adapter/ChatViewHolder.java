package eu.h2020.sc.ui.messages.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import eu.h2020.sc.R;

/**
 * Created by pietro on 20/03/2018.
 */
public class ChatViewHolder extends RecyclerView.ViewHolder {

    protected TextView tvSenderName;
    protected ImageView imageViewMessageNotRead;
    protected ImageView imageViewPhotoSender;
    protected TextView lastMessageDate;
    protected RelativeLayout relativeLayoutRow;

    public ChatViewHolder(View itemView) {
        super(itemView);
        this.tvSenderName = (TextView) itemView.findViewById(R.id.message_row_tv_sender_name);
        this.imageViewMessageNotRead = (ImageView) itemView.findViewById(R.id.message_row_img_view_message_not_read);
        this.imageViewPhotoSender = (ImageView) itemView.findViewById(R.id.message_row_img_view_photo_sender);
        this.lastMessageDate = (TextView) itemView.findViewById(R.id.message_row_tv_last_message_date);
        this.relativeLayoutRow = (RelativeLayout) itemView.findViewById(R.id.relative_layout_contact);
    }

}