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

import android.os.Bundle;
import android.preference.PreferenceManager;

import com.google.android.gms.gcm.GcmListenerService;

import be.simongenin.synchouse.MainActivity;
import be.simongenin.synchouse.SyncHouseApplication;
import be.simongenin.synchouse.models.Alarm;
import be.simongenin.synchouse.utils.NotificationHandler;

import static be.simongenin.synchouse.requests.StatusCodes.ALARM_PARTIAL_START;
import static be.simongenin.synchouse.requests.StatusCodes.ALARM_PROBLEM;
import static be.simongenin.synchouse.requests.StatusCodes.ALARM_RING_START;
import static be.simongenin.synchouse.requests.StatusCodes.ALARM_RING_STOP;
import static be.simongenin.synchouse.requests.StatusCodes.ALARM_STOP;
import static be.simongenin.synchouse.requests.StatusCodes.ALARM_TOTAL_START;


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
        applyStatusCode(statusCode);

        /**
         * Send a notification to the device
         */
        NotificationHandler.sendNotification(message, this, MainActivity.class);

    }


    private void applyStatusCode(int statusCode) {

        Alarm alarm = new Alarm();
        alarm.retrieveState(PreferenceManager.getDefaultSharedPreferences(this));

        switch (statusCode) {

            case ALARM_TOTAL_START:
                alarm.setState(Alarm.state.TOTAL);
                break;

            case ALARM_PARTIAL_START :
                alarm.setState(Alarm.state.PARTIAL);
                break;

            case ALARM_STOP:
                alarm.setState(Alarm.state.NONE);
                break;

            case ALARM_RING_START:
                alarm.activeSiren();
                break;

            case ALARM_RING_STOP:
                alarm.deactivateSiren();
                break;

            case ALARM_PROBLEM:
                // TODO problem
                break;

        }

        alarm.saveState(PreferenceManager.getDefaultSharedPreferences(this));

    }

}
