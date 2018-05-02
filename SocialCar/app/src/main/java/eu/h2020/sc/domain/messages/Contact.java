package eu.h2020.sc.domain.messages;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import eu.h2020.sc.SocialCarApplication;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */

public class Contact implements Serializable {

    private static final String CONTACT_NAME = "contact_name";
    private static final String CONTACT_PICTURE_PATH = "contact_picture_path";
    private static final String CONTACT_LAST_MESSAGE_DATE = "contact_last_message_date";
    private static final String READ_ALL_MESSAGES = "read_all_messages";
    private static final String CONTACT_MESSAGES = "contact_messages";

    private String id;

    @SerializedName(value = CONTACT_NAME)
    private String contactName;

    @SerializedName(value = CONTACT_PICTURE_PATH)
    private String contactPicturePath;

    @SerializedName(value = CONTACT_LAST_MESSAGE_DATE)
    private Date lastMessageDate;

    @SerializedName(value = READ_ALL_MESSAGES)
    private boolean readAllMessages;

    @Expose(serialize = false, deserialize = false)
    private String liftID;

    @SerializedName(value = CONTACT_MESSAGES)
    private List<Message> messages;

    public Contact(String id, String contactName, String contactPicturePath, String liftID) {
        this.id = id;
        this.contactName = contactName;
        this.contactPicturePath = contactPicturePath;
        this.readAllMessages = true;
        this.liftID = liftID;
        this.lastMessageDate = new Date();
        this.messages = new ArrayList<>();
    }

    public Contact(String id, String contactName, String liftID) {
        this.id = id;
        this.contactName = contactName;
        this.readAllMessages = true;
        this.liftID = liftID;
        this.lastMessageDate = new Date();
        this.messages = new ArrayList<>();
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }

    public String getId() {
        return id;
    }

    public List<Message> getMessages() {
        return this.messages;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactPicturePath() {
        return contactPicturePath;
    }

    public Date getLastMessageDate() {
        return lastMessageDate;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setContactPicturePath(String contactPicturePath) {
        this.contactPicturePath = contactPicturePath;
    }

    public boolean hasReadAllMessages() {
        return readAllMessages;
    }

    public void setReadAllMessages(boolean readAllMessages) {
        this.readAllMessages = readAllMessages;
    }

    public String getLiftID() {
        return liftID;
    }

    public void setLastMessageDate(Date lastMessageDate) {
        this.lastMessageDate = lastMessageDate;
    }

    public String toJson() {
        return SocialCarApplication.getGson().toJson(this);
    }

    public static Contact fromJson(String jsonContact) {
        return SocialCarApplication.getGson().fromJson(jsonContact, Contact.class);
    }

    public boolean isYesterdayLastMessageDate() {
        Calendar c1 = Calendar.getInstance();
        c1.add(Calendar.DAY_OF_YEAR, -1);

        Calendar c2 = Calendar.getInstance();
        c2.setTime(this.lastMessageDate);

        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    public boolean isTodayLastMessageDate() {
        Calendar c1 = Calendar.getInstance();

        Calendar c2 = Calendar.getInstance();
        c2.setTime(this.lastMessageDate);

        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    public static List<Contact> fromJsonArray(String jsonContactList) throws JSONException {

        JSONArray contactsJson = new JSONArray(jsonContactList);

        return SocialCarApplication.getGson().<ArrayList<Contact>>fromJson(contactsJson.toString(), new TypeToken<List<Contact>>() {
        }.getType());
    }

    @Override
    public String toString() {
        return "Contact{" +
                "id='" + id + '\'' +
                ", contactName='" + contactName + '\'' +
                ", contactPicturePath='" + contactPicturePath + '\'' +
                ", lastMessageDate=" + lastMessageDate +
                ", readAllMessages=" + readAllMessages +
                ", liftID='" + liftID + '\'' +
                ", messages=" + messages +
                '}';
    }
}
