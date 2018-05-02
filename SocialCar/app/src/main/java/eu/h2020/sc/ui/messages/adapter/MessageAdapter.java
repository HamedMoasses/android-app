package eu.h2020.sc.ui.messages.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import eu.h2020.sc.R;
import eu.h2020.sc.domain.messages.Message;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */

public class MessageAdapter extends RecyclerView.Adapter {

    private static int ROW_MESSAGE_SENDER = 0;
    private static int ROW_MESSAGE_RECEIVER = 1;

    private List<Message> messages;

    public MessageAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == ROW_MESSAGE_SENDER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_message_sender, parent, false);
            return new MessageSenderViewHolder(v);

        } else if (viewType == ROW_MESSAGE_RECEIVER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_message_receiver, parent, false);
            return new MessageReceiverViewHolder(v);
        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MessageSenderViewHolder) {
            MessageSenderViewHolder h = (MessageSenderViewHolder) holder;
            h.init(getItem(position));

        } else if (holder instanceof MessageReceiverViewHolder) {
            MessageReceiverViewHolder h = (MessageReceiverViewHolder) holder;
            h.init(getItem(position));
        }
    }

    public void refreshRecyclerView(List<Message> messages) {
        this.messages.clear();
        this.messages.addAll(messages);
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).isMineMessage())
            return ROW_MESSAGE_SENDER;
        else
            return ROW_MESSAGE_RECEIVER;
    }

    public Message getItem(int position) {
        return this.messages.get(position);
    }

    @Override
    public int getItemCount() {
        return this.messages.size();
    }

    public int getPositionByItem(Message message) {
        return this.messages.indexOf(message);
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public void refreshItems() {
        this.notifyDataSetChanged();
    }

    public void addedItemAtPosition(int position) {
        this.notifyItemInserted(position);
    }

    public int getLastPosition() {
        return this.messages.size();
    }
}
