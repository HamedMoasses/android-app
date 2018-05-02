package eu.h2020.sc.ui.messages;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import eu.h2020.sc.GeneralActivity;
import eu.h2020.sc.R;
import eu.h2020.sc.domain.messages.ChatController;
import eu.h2020.sc.ui.commons.RecyclerViewItemClickListener;
import eu.h2020.sc.ui.messages.adapter.ChatAdapter;
import eu.h2020.sc.utils.ActivityUtils;
import eu.h2020.sc.utils.Utils;

import static eu.h2020.sc.SocialCarApplication.getContext;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class ChatActivity extends GeneralActivity implements RecyclerViewItemClickListener {

    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (Utils.isAfterKitKatVersion())
            setTheme(R.style.Theme_SocialCar_Status_Transparent);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initToolBar(true);
        initUI();
    }

    private void initUI() {
        RecyclerView recyclerViewContact = (RecyclerView) findViewById(R.id.recyclerViewContacts);

        this.chatAdapter = new ChatAdapter(ChatController.getInstance().getAllContacts(), this, getContext());
        recyclerViewContact.setAdapter(this.chatAdapter);
        recyclerViewContact.setHasFixedSize(true);

        recyclerViewContact.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.initDrawer(getString(R.string.messages));
        this.chatAdapter.refreshRecyclerView(ChatController.getInstance().getAllContacts());
    }

    @Override
    public void onItemClickListener(View v, int position) {
        ActivityUtils.openActivityWithParam(this, MessagesActivity.class, MessagesActivity.TAG, this.chatAdapter.getItem(position).getId());
        finish();
    }
}
