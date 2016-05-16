package be.simongenin.synchouse.gcm;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.gcm.GcmListenerService;

import be.simongenin.synchouse.MainActivity;
import be.simongenin.synchouse.SyncHouseApplication;
import be.simongenin.synchouse.utils.NotificationHandler;

/**
 * @author Simon Genin and Google
 */
public class SyncHouseGcmListenerService extends GcmListenerService {

    private static final String TAG = "SHGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {

        /**
         * Get the app object
         */
        SyncHouseApplication application = (SyncHouseApplication) getApplication();

        /**
         * Get back the 3 critical data from the response data
         */
        int statusCode = Integer.parseInt(data.getString("status_code"));
        String homeId = data.getString("home_id");
        String message = data.getString("message");

        /**
         * We first need to be sure that we are concerned by the message.
         */
        if (!application.homeID.equals(homeId)) {
            return;
        }

        /**
         * We need to do something depending on the status code.
         * We send the relevant data to the activity, because we can't
         * do it in a background thread. Why ? Because at the end of the processing,
         * the UI in the fragments will be updated. Or, if we are still on this
         * background thread, our application will crash.
         */
        Intent intent = new Intent("status_code");
        intent.putExtra("status_code", statusCode);
        intent.putExtra("args", data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

        /**
         * Send a notification to the device
         */
        NotificationHandler.sendNotification(message, this, MainActivity.class);

    }

}
