package eu.h2020.sc.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import eu.h2020.sc.R;

/**
 * Created by Pietro on 28/06/16.
 */
public class PushNotificationBuilder {

    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private Context context;
    private int resIcon;
    private String title, subTitle;
    private Bitmap largeIcon;
    private boolean autoCancel;

    public PushNotificationBuilder(Context context, Bitmap largeIcon, int resIcon, String title, String subTitle, boolean autoCancel) {
        this.context = context;
        this.largeIcon = largeIcon;
        this.autoCancel = autoCancel;
        this.resIcon = resIcon;
        this.title = title;
        this.subTitle = subTitle;
        this.mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        this.buildNotification();
    }

    public void goToActivity(Intent i) {
        int requestID = (int) System.currentTimeMillis();
        int notificationID = (int) System.currentTimeMillis();

        PendingIntent contentIntent = PendingIntent.getActivity(context, requestID, i,
                PendingIntent.FLAG_UPDATE_CURRENT);
        this.mBuilder.setContentIntent(contentIntent);
        this.mNotificationManager.notify(notificationID, mBuilder.build());
    }

    private void buildNotification() {
        this.mBuilder = new NotificationCompat.Builder(context)
                .setLargeIcon(largeIcon)
                .setSmallIcon(resIcon)
                .setContentTitle(title)
                .setContentText(subTitle)
                .setAutoCancel(autoCancel)
                .setLights(ContextCompat.getColor(context, R.color.primary_color), 1, 1)
                .setColor(ContextCompat.getColor(context, R.color.primary_color))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
    }
}