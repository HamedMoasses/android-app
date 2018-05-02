package eu.h2020.sc.dao;

import eu.h2020.sc.domain.messages.Message;
import eu.h2020.sc.exception.ConflictException;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.protocol.SendMessageRequest;
import eu.h2020.sc.protocol.SocialCarRequest;
import eu.h2020.sc.transport.PostHttpTask;

/**
 * Created by pietro on 20/03/2018.
 */
public class MessageDAO {

    public Message createMessage(Message message) throws ServerException, ConnectionException, UnauthorizedException, ConflictException {
        SocialCarRequest socialCarRequest = new SendMessageRequest(message);
        PostHttpTask postHttpTask = new PostHttpTask();

        String json = postHttpTask.makeRequest(socialCarRequest);
        return Message.fromJson(json);

    }
}
