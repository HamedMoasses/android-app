/**
 * Copyright (C) 2016 Movenda SPA - All Rights Reserved
 */
package eu.h2020.sc.ui.messages.task;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import eu.h2020.sc.dao.MessageDAO;
import eu.h2020.sc.domain.messages.Message;
import eu.h2020.sc.exception.ConflictException;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.ui.messages.MessagesActivity;

public class SendMessageTask extends AsyncTask<Message, Void, Integer> {

    protected final String TAG = this.getClass().getName();

    private static final int CONNECTION_ERROR_RESULT = 1;
    private static final int SERVER_ERROR_RESULT = 2;
    private static final int UNAUTHORIZED_RESULT = 3;
    private static final int COMPLETED_RESULT = 4;

    @SuppressLint("StaticFieldLeak")
    private MessagesActivity messagesActivity;

    private MessageDAO messageDAO;
    private Message createdMessage;

    public SendMessageTask(MessagesActivity messagesActivity) {
        this.messagesActivity = messagesActivity;
        this.messageDAO = new MessageDAO();
    }

    @Override
    protected Integer doInBackground(Message... messages) {

        Message message = messages[0];

        try {
            this.createdMessage = this.messageDAO.createMessage(message);
            return COMPLETED_RESULT;

        } catch (ServerException | ConflictException e) {
            return SERVER_ERROR_RESULT;

        } catch (ConnectionException e) {
            return CONNECTION_ERROR_RESULT;

        } catch (UnauthorizedException e) {
            return UNAUTHORIZED_RESULT;
        }
    }


    @Override
    protected void onPostExecute(final Integer resultCode) {

        this.messagesActivity.dismissDialog();

        switch (resultCode) {
            case CONNECTION_ERROR_RESULT:
                this.messagesActivity.showConnectionError();
                break;

            case SERVER_ERROR_RESULT:
                this.messagesActivity.showServerGenericError();
                break;

            case UNAUTHORIZED_RESULT:
                this.messagesActivity.showUnauthorizedError();
                break;

            case COMPLETED_RESULT:
                this.messagesActivity.storeMessage(this.createdMessage);
                break;

            default:
                this.messagesActivity.showServerGenericError();
        }
    }
}
