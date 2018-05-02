package eu.h2020.sc.domain.messages;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import eu.h2020.sc.SocialCarApplication;

/**
 * @author Pietro Ditta <pietro.ditta@movenda.com>
 */

public class Message implements Serializable {

    public static final String SENDER_ID = "sender_id";
    public static final String RECEIVER_ID = "receiver_id";

    private static final String BODY_ID = "body";
    private static final String LIFT_ID = "lift_id";

    @SerializedName(value = SENDER_ID)
    @Expose
    private String senderID;

    @SerializedName(value = RECEIVER_ID)
    @Expose
    private String receiverID;

    @SerializedName(value = BODY_ID)
    @Expose
    private String body;

    @SerializedName(value = LIFT_ID)
    @Expose
    private String liftID;

    @Expose(serialize = false)
    private Long timestamp;

    public Message(String senderID, String receiverID, String liftID, String body) {
        this.body = body;
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.liftID = liftID;
        this.timestamp = System.currentTimeMillis() / 1000L;
    }

    public boolean isMineMessage() {
        return SocialCarApplication.getInstance().getUser().getId().equals(this.senderID);
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getBody() {
        return body;
    }

    public String getSenderID() {
        return senderID;
    }

    public String toJson() {
        return SocialCarApplication.getGson().toJson(this);
    }

    public String toJsonForRequest() {
        GsonBuilder gsonBuilder = SocialCarApplication.getGsonBuilder().excludeFieldsWithoutExposeAnnotation();
        return gsonBuilder.create().toJson(this);
    }

    public static Message fromJson(String jsonMessage) {
        return SocialCarApplication.getGson().fromJson(jsonMessage, Message.class);
    }


    @Override
    public String toString() {
        return "Message{" +
                "senderID='" + senderID + '\'' +
                ", receiverID='" + receiverID + '\'' +
                ", body='" + body + '\'' +
                ", liftID='" + liftID + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
