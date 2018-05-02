package eu.h2020.sc.ui.messages;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.List;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.domain.messages.ChatController;
import eu.h2020.sc.domain.messages.Contact;
import eu.h2020.sc.domain.messages.Message;
import eu.h2020.sc.ui.messages.adapter.MessageAdapter;
import eu.h2020.sc.ui.messages.task.SendMessageTask;
import eu.h2020.sc.utils.ActivityUtils;
import eu.h2020.sc.utils.Utils;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class MessagesActivity extends GeneralActivity {

    public static final String TAG = MessagesActivity.class.getCanonicalName();

    private EditText editTextMessage;
    private Contact contact;

    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;

    private List<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        String contactID = getIntent().getStringExtra(TAG);
        this.contact = ChatController.getInstance().findContactByID(contactID);

        this.contact.setReadAllMessages(true);
        ChatController.getInstance().updateContact(this.contact);

        this.messages = this.contact.getMessages();

        this.initUI();
    }

    private void initUI() {

        initToolBar(false);
        initBack();

        setTitle(this.contact.getContactName());

        this.editTextMessage = (EditText) findViewById(R.id.edit_text_send_message);
        this.recyclerViewMessages = (RecyclerView) findViewById(R.id.recyclerViewMessages);

        initRecycleViewMessages();
    }

    private void initRecycleViewMessages() {

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        llm.setStackFromEnd(true);

        this.recyclerViewMessages.setHasFixedSize(true);
        this.recyclerViewMessages.setLayoutManager(llm);
        this.recyclerViewMessages.setItemAnimator(new DefaultItemAnimator());

        this.messageAdapter = new MessageAdapter(this.contact.getMessages());
        this.recyclerViewMessages.setAdapter(this.messageAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ActivityUtils.openActivityNoBack(this, ChatActivity.class);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        ActivityUtils.openActivityNoBack(this, ChatActivity.class);
    }

    public void sendMessage(View view) {

        String bodyMessage = this.editTextMessage.getText().toString();

        if (!bodyMessage.isEmpty()) {
            Utils.hideSoftKeyboard(this.editTextMessage);

            Message message = new Message(SocialCarApplication.getInstance().getUser().getId(), this.contact.getId(), this.contact.getLiftID(), bodyMessage);

            SendMessageTask sendMessageTask = new SendMessageTask(this);
            sendMessageTask.execute(message);

            showProgressDialog();
        }
    }

    public void storeMessage(Message createdMessage) {

        Log.i(getTagFromActivity(this), "Created message : " + createdMessage.toString());
        this.editTextMessage.getText().clear();

        ChatController.getInstance().storeMessage(createdMessage, this.contact);

        this.messages.add(createdMessage);
        this.messageAdapter.setMessages(this.messages);

        this.messageAdapter.addedItemAtPosition(this.messages.size());

        this.recyclerViewMessages.scrollToPosition(this.messageAdapter.getPositionByItem(createdMessage));
        this.messageAdapter.refreshItems();
    }
}
