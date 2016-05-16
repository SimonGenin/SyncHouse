package be.simongenin.synchouse.utils;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import be.simongenin.synchouse.R;

/**
 * @author Simon Genin
 *
 * Help functions for the notifications
 */
public class NotificationHandler {

    /**
     * Every notification needs a unique ID.
     * It will be this variable that will be incremented.
     */
    private static int cout = 0;

    /**
     * Send a notification.
     *
     * It will display the content of the message parameter and will open
     * the "openedActivity" activity when clicked.
     */
    public static void sendNotification(String message, Context context, Class<?> openedActivity) {

        /**
         * Prepare the intent for the onclicked activity destination
         */
        Intent intent = new Intent(context, openedActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        /**
         * Build the actual notification
         */
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_menu_alarm)
                .setContentTitle("GCM Message")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        /**
         * Send the notification
         */
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(cout++ /* ID of notification */, notificationBuilder.build());
    }

}
