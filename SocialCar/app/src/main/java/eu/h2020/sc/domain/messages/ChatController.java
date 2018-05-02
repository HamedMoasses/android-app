package eu.h2020.sc.domain.messages;

import android.util.Log;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import eu.h2020.sc.SocialCarApplication;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */

public class ChatController {

    private static final String TAG = ChatController.class.getCanonicalName();

    public static final String CONTACT_MESSAGES = "CONTACT_MESSAGES";

    private static ChatController instance = null;

    private ChatController() {
    }

    public static ChatController getInstance() {
        if (instance == null) {
            instance = new ChatController();
        }
        return instance;
    }

    public void storeContact(Contact contact) {

        List<Contact> contacts = this.getAllContacts();

        boolean findContact = false;

        if (contacts != null) {

            for (Contact c : contacts) {

                if (c.getId().equals(contact.getId())) {
                    findContact = true;
                    break;
                }
            }

            if (!findContact) {

                contacts.add(contact);

                Log.i(TAG, String.format("Added contact name %s", contact.getContactName()));

                SocialCarApplication.getEditor().putString(CONTACT_MESSAGES, SocialCarApplication.getGson().toJson(contacts)).commit();
            }

        } else {
            Log.e(TAG, "Unable to add contact.... Return NULL from get all contacts.");
        }
    }

    public void storeMessage(Message message, Contact contact) {

        List<Contact> contacts = this.getAllContacts();

        boolean findContact = false;

        if (contacts != null) {

            for (Contact c : contacts) {

                if (c.getId().equals(contact.getId())) {
                    c.addMessage(message);
                    findContact = true;
                    break;
                }
            }

            if (findContact) {
                Log.i(TAG, String.format("Add message to contact name : %s", contact.getContactName()));
                SocialCarApplication.getEditor().putString(CONTACT_MESSAGES, SocialCarApplication.getGson().toJson(contacts)).commit();
            } else
                Log.e(TAG, "Unable to add message to unknown contact name : " + contact.getContactName());

        } else {
            Log.e(TAG, "Unable to remove contact.... Return NULL from get all contacts.");
        }
    }


    public void updateContact(Contact contact) {

        List<Contact> contacts = this.getAllContacts();

        boolean findContact = false;

        if (contacts != null) {

            for (Contact c : contacts) {

                if (c.getId().equals(contact.getId())) {
                    c.setContactName(contact.getContactName());
                    c.setContactPicturePath(contact.getContactPicturePath());
                    c.setLastMessageDate(contact.getLastMessageDate());
                    c.setReadAllMessages(contact.hasReadAllMessages());

                    findContact = true;
                    break;
                }
            }

            if (findContact) {
                Log.i(TAG, String.format("Update contact : %s", contact.getContactName()));
                SocialCarApplication.getEditor().putString(CONTACT_MESSAGES, SocialCarApplication.getGson().toJson(contacts)).commit();
            } else
                Log.e(TAG, "Unable to update unknown contact : " + contact.getContactName());

        } else {
            Log.e(TAG, "Unable to remove contact.... Return NULL from get all contacts.");
        }


    }

    public List<Contact> getAllContacts() {

        try {
            List<Contact> defaultEmptyContactList = new ArrayList<>();
            String defaultString = SocialCarApplication.getGson().toJson(defaultEmptyContactList);

            String contacts = SocialCarApplication.getSharedPreferences().getString(CONTACT_MESSAGES, defaultString);

            if (contacts != null && contacts.equals("[{}]"))
                return new ArrayList<>();

            else if (contacts != null) {
                return Contact.fromJsonArray(contacts);

            } else {
                Log.e(TAG, "No contacts get from shared preferences...");
                return null;
            }

        } catch (JSONException e) {
            Log.e(TAG, "Unable to parse contacts json array...", e);
            return null;
        }
    }

    public void removeContact(String contactID) {

        List<Contact> contacts = this.getAllContacts();

        Contact contactToRemove = null;

        if (contacts != null) {

            for (Contact contact : contacts) {

                if (contact.getId().equals(contactID)) {
                    contactToRemove = contact;
                    break;
                }
            }

            if (contactToRemove != null) {

                contacts.remove(contactToRemove);

                Log.i(TAG, String.format("Removed contact name %s", contactToRemove.getContactName()));

                SocialCarApplication.getEditor().putString(CONTACT_MESSAGES, SocialCarApplication.getGson().toJson(contacts)).commit();
            } else
                Log.e(TAG, "Unable to remove unknown contact ID : " + contactID);

        } else {
            Log.e(TAG, "Unable to remove contact.... Return NULL from get all contacts.");
        }
    }

    public Contact findContactByID(String userID) {

        List<Contact> contactList = this.getAllContacts();

        if (contactList != null) {

            for (Contact contact : contactList) {
                if (contact.getId().equals(userID))
                    return contact;
            }
        }

        return null;
    }


    public boolean areAllMessagesNotRead() {

        List<Contact> contacts = this.getAllContacts();

        if (contacts != null) {

            for (Contact contact : contacts) {
                if (!contact.hasReadAllMessages()) {
                    return true;
                }
            }
            return false;

        } else {
            Log.e(TAG, "Return NULL from get all contacts....");
            return false;
        }
    }
}
