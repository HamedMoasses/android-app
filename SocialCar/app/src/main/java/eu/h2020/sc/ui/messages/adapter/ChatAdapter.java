package eu.h2020.sc.ui.messages.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Date;
import java.util.List;

import eu.h2020.sc.R;
import eu.h2020.sc.domain.messages.Contact;
import eu.h2020.sc.ui.commons.RecyclerViewItemClickListener;
import eu.h2020.sc.utils.AnimationHelper;
import eu.h2020.sc.utils.DateUtils;
import eu.h2020.sc.utils.PicassoHelper;

/**
 * Created by pietro on 20/03/2018.
 */
public class ChatAdapter extends RecyclerView.Adapter<ChatViewHolder> implements View.OnClickListener {

    private RecyclerViewItemClickListener recyclerViewItemClickListener;
    private List<Contact> contacts;
    private Context context;

    public ChatAdapter(List<Contact> contacts, RecyclerViewItemClickListener recyclerViewItemClickListenerMenu, Context context) {
        this.contacts = contacts;
        this.recyclerViewItemClickListener = recyclerViewItemClickListenerMenu;
        this.context = context;
    }

    @Override
    public ChatViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewChatRow = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_contact, parent, false);
        return new ChatViewHolder(viewChatRow);
    }

    @Override
    public void onBindViewHolder(ChatViewHolder holder, int position) {

        Contact contact = this.getItem(position);

        holder.tvSenderName.setText(contact.getContactName());

        Date lastMessageDate = contact.getLastMessageDate();

        if (contact.isTodayLastMessageDate()) {
            holder.lastMessageDate.setText(DateUtils.hourLongToString(lastMessageDate, DateFormat.is24HourFormat(this.context)));
        } else if (contact.isYesterdayLastMessageDate()) {
            holder.lastMessageDate.setText(this.context.getResources().getString(R.string.yestarday));
        } else {
            holder.lastMessageDate.setText(DateUtils.dateLongToStringCanonic(lastMessageDate));
        }

        if (!contact.hasReadAllMessages())
            holder.imageViewMessageNotRead.setVisibility(View.VISIBLE);
        else
            holder.imageViewMessageNotRead.setVisibility(View.GONE);

        if (contact.getContactPicturePath() != null) {
            PicassoHelper.loadMedia(this.context, holder.imageViewPhotoSender, contact.getContactPicturePath(), R.drawable.img_default_ride_avatar, R.drawable.img_default_ride_avatar, true);
        } else
            PicassoHelper.loadPictureByUserID(this.context, holder.imageViewPhotoSender, contact.getId(), R.drawable.img_default_ride_avatar, R.drawable.img_default_ride_avatar, true);


        holder.relativeLayoutRow.setTag(contact);
        holder.relativeLayoutRow.setOnClickListener(this);
    }

    public void refreshRecyclerView(List<Contact> contacts) {
        AnimationHelper.animationsLocked = false;
        AnimationHelper.lastAnimatedPosition = -1;

        this.contacts.clear();
        this.contacts.addAll(contacts);
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return this.contacts.size();
    }

    public Contact getItem(int position) {
        return this.contacts.get(position);
    }

    @Override
    public void onClick(View v) {
        Contact item = (Contact) v.getTag();
        int position = this.contacts.indexOf(item);
        this.recyclerViewItemClickListener.onItemClickListener(v, position);
    }
}