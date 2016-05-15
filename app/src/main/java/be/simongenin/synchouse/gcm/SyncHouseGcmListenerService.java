package be.simongenin.synchouse.gcm;

/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.gcm.GcmListenerService;

import be.simongenin.synchouse.MainActivity;
import be.simongenin.synchouse.SyncHouseApplication;
import be.simongenin.synchouse.utils.NotificationHandler;


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

        SyncHouseApplication application = (SyncHouseApplication) getApplication();

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
