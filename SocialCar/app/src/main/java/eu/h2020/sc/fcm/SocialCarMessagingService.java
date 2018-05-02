package eu.h2020.sc.fcm;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import eu.h2020.sc.R;
import eu.h2020.sc.SocialCarApplication;
import eu.h2020.sc.dao.UserDAO;
import eu.h2020.sc.dao.multipart.PictureMultipartDAO;
import eu.h2020.sc.dao.multipart.PictureUserMultipartDAO;
import eu.h2020.sc.domain.Lift;
import eu.h2020.sc.domain.LiftStatus;
import eu.h2020.sc.domain.PushMessageData;
import eu.h2020.sc.domain.User;
import eu.h2020.sc.domain.messages.ChatController;
import eu.h2020.sc.domain.messages.Contact;
import eu.h2020.sc.domain.messages.Message;
import eu.h2020.sc.exception.ConnectionException;
import eu.h2020.sc.exception.NotFoundException;
import eu.h2020.sc.exception.ServerException;
import eu.h2020.sc.exception.UnauthorizedException;
import eu.h2020.sc.ui.lift.AcceptanceLiftActivity;
import eu.h2020.sc.ui.lift.LiftActivity;
import eu.h2020.sc.ui.messages.MessagesActivity;
import eu.h2020.sc.utils.ActivityUtils;
import eu.h2020.sc.utils.DimensionUtils;
import eu.h2020.sc.utils.ImageUtils;
import eu.h2020.sc.utils.PicassoHelper;
import eu.h2020.sc.utils.Utils;

/**
 * @author Nicola d'Adduzio <nicola.dadduzio@movenda.com>
 */
public class SocialCarMessagingService extends FirebaseMessagingService {

    private static final String TAG = SocialCarMessagingService.class.getName();

    private PushMessageData pushMessageData;
    private Lift lift;

    private User user;
    private User loggedUser;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        StringBuilder jsonPushRawData = new StringBuilder();

        Map<String, String> map = remoteMessage.getData();

        for (String key : map.keySet()) {
            jsonPushRawData.append(map.get(key));
        }

        Log.i(TAG, jsonPushRawData.toString());

        try {
            JSONObject jsonObject = new JSONObject(jsonPushRawData.toString());

            if (jsonObject.has(Message.SENDER_ID) && jsonObject.has(Message.RECEIVER_ID)) {

                ChatController chatController = ChatController.getInstance();

                Message receivedMessage = Message.fromJson(jsonPushRawData.toString());

                Contact contact = chatController.findContactByID(receivedMessage.getSenderID());

                if (contact != null) {
                    contact.setReadAllMessages(false);

                    chatController.updateContact(contact);
                    chatController.storeMessage(receivedMessage, contact);

                    User user = this.getUserByUserID(receivedMessage.getSenderID());

                    if (user != null) {
                        Bitmap largeIcon = this.getUserPicture(user);
                        PushNotificationBuilder pushNotificationBuilder = new PushNotificationBuilder(this, largeIcon, R.mipmap.ic_notification_app_icon, contact.getContactName(), receivedMessage.getBody(), true);
                        pushNotificationBuilder.goToActivity(ActivityUtils.buildChatIntent(this, MessagesActivity.class, MessagesActivity.TAG, contact.getId()));

                    } else {
                        Log.e(TAG, String.format("Unable to show notification....Unable to get user with _id [%s]", receivedMessage.getSenderID()));
                    }

                } else {
                    Log.e(TAG, String.format("Unable to retrieve contact by senderID [%s]", receivedMessage.getSenderID()));
                }

            } else {

                this.loggedUser = SocialCarApplication.getInstance().getUser();
                this.lift = Lift.fromJson(jsonPushRawData.toString());

                if (this.lift != null && this.loggedUser != null) {

                    String userID = this.obtainUserID();
                    this.user = this.getUserByUserID(userID);

                    Bitmap largeIcon = this.getUserPicture(this.user);

                    String title = this.generateNotificationTitle(this.lift.getStatus());
                    String subTitle = this.getSubtitle(this.user);

                    this.pushMessageData = new PushMessageData(ImageUtils.convertBitmap(largeIcon), this.lift, this.user);

                    PushNotificationBuilder pushNotificationBuilder = new PushNotificationBuilder(this, largeIcon, R.mipmap.ic_notification_app_icon, title, subTitle, true);
                    this.goToActivity(pushNotificationBuilder);

                } else
                    Log.e(TAG, "Unable to show notification....logged user is null or error when parsing lift, why is null?");
            }
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void goToActivity(PushNotificationBuilder pushNotificationBuilder) {

        switch (this.lift.getStatus()) {
            case PENDING:
                pushNotificationBuilder.goToActivity(ActivityUtils.generatePushIntent(this, AcceptanceLiftActivity.class, AcceptanceLiftActivity.TAG, this.pushMessageData));
                break;

            case ACTIVE:
                Contact contact;

                if (this.user.getUserPictures() != null && !this.user.getUserPictures().isEmpty()) {
                    contact = new Contact(this.user.getId(), this.user.getName(), this.user.getUserPictures().get(0).getMediaUri(), this.lift.getID());
                } else
                    contact = new Contact(this.user.getId(), this.user.getName(), this.lift.getID());

                ChatController.getInstance().storeContact(contact);

                pushNotificationBuilder.goToActivity(ActivityUtils.generatePassengerPushIntent(this, LiftActivity.class, LiftActivity.TAG, 0));
                break;
            case REFUSED:
                pushNotificationBuilder.goToActivity(ActivityUtils.generatePassengerPushIntent(this, LiftActivity.class, LiftActivity.TAG, 0));
                break;
            case CANCELLED:
                ChatController.getInstance().removeContact(this.user.getId());
                pushNotificationBuilder.goToActivity(ActivityUtils.generatePassengerPushIntent(this, LiftActivity.class, LiftActivity.TAG, this.getTabToOpenFromCancelledLift()));
                break;
            default:
                pushNotificationBuilder.goToActivity(ActivityUtils.generatePassengerPushIntent(this, LiftActivity.class, LiftActivity.TAG, 0));
        }
    }

    private String obtainUserID() {
        if (this.lift.getDriverID().equals(this.loggedUser.getId()))
            return this.lift.getPassengerID();
        else
            return this.lift.getDriverID();
    }

    private Integer getTabToOpenFromCancelledLift() {
        if (this.lift.getDriverID().equals(this.loggedUser.getId()))
            return 1;

        return 0;
    }

    private User getUserByUserID(String userID) {

        try {
            UserDAO userDAO = new UserDAO();
            return userDAO.findUserByID(userID);
        } catch (ConnectionException | NotFoundException | UnauthorizedException | ServerException e) {
            Log.e(TAG, "Unable to get user by obtained userID...", e);
            return null;
        }
    }

    private String getSubtitle(User passenger) {
        if (passenger != null)
            return String.format("%s %s", passenger.getName(), this.getString(R.string.app_name));
        else
            return this.getString(R.string.app_name);
    }

    private String generateNotificationTitle(LiftStatus liftStatus) {
        switch (liftStatus) {
            case PENDING:
                return getString(R.string.lift_request);
            case ACTIVE:
                return getString(R.string.trip_accepted);
            case REFUSED:
                return getString(R.string.trip_declined);
            case CANCELLED:
                return getString(R.string.trip_cancelled);
            default:
                return getString(R.string.lift_request);
        }
    }

    private Bitmap getUserPicture(User user) {
        try {

            if (user.getUserPictures() != null && !user.getUserPictures().isEmpty()) {

                PictureMultipartDAO pictureDAO = new PictureUserMultipartDAO();
                byte[] userPicture = pictureDAO.findPictureByMediaUri(user.getUserPictures().get(0).getMediaUri());

                float density = SocialCarApplication.getContext().getResources().getDisplayMetrics().density;

                if (Utils.isAfterKitKatVersion())
                    return PicassoHelper.getCircleBitmap(BitmapFactory.decodeByteArray(userPicture, 0, userPicture.length));

                else {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(userPicture, 0, userPicture.length);
                    return Bitmap.createScaledBitmap(bitmap, DimensionUtils.obtainNotificationWidthAvatarDimension(density), DimensionUtils.obtainNotificationHeightAvatarDimension(density), false);
                }

            } else
                return BitmapFactory.decodeResource(SocialCarApplication.getContext().getResources(), R.mipmap.user_notification_default);


        } catch (Exception e) {
            return BitmapFactory.decodeResource(SocialCarApplication.getContext().getResources(), R.mipmap.user_notification_default);
        }
    }
}
