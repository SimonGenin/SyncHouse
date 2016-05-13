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

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.google.android.gms.gcm.GcmListenerService;

import be.simongenin.synchouse.MainActivity;
import be.simongenin.synchouse.SyncHouseApplication;
import be.simongenin.synchouse.models.Alarm;
import be.simongenin.synchouse.models.DomesticMachine;
import be.simongenin.synchouse.models.Mower;
import be.simongenin.synchouse.models.Windows;
import be.simongenin.synchouse.utils.NotificationHandler;

import static be.simongenin.synchouse.requests.StatusCodes.ALARM_PARTIAL_START;
import static be.simongenin.synchouse.requests.StatusCodes.ALARM_PROBLEM;
import static be.simongenin.synchouse.requests.StatusCodes.ALARM_RING_START;
import static be.simongenin.synchouse.requests.StatusCodes.ALARM_RING_STOP;
import static be.simongenin.synchouse.requests.StatusCodes.ALARM_STOP;
import static be.simongenin.synchouse.requests.StatusCodes.ALARM_TOTAL_START;
import static be.simongenin.synchouse.requests.StatusCodes.DISH_WASHER_ELECTRICAL_PROBLEM;
import static be.simongenin.synchouse.requests.StatusCodes.DISH_WASHER_PROGRAM;
import static be.simongenin.synchouse.requests.StatusCodes.DISH_WASHER_START;
import static be.simongenin.synchouse.requests.StatusCodes.DISH_WASHER_STOP;
import static be.simongenin.synchouse.requests.StatusCodes.DISH_WASHER_WATER_PROBLEM;
import static be.simongenin.synchouse.requests.StatusCodes.DRYER_ELECTRICAL_PROBLEM;
import static be.simongenin.synchouse.requests.StatusCodes.DRYER_PROGRAM;
import static be.simongenin.synchouse.requests.StatusCodes.DRYER_START;
import static be.simongenin.synchouse.requests.StatusCodes.DRYER_STOP;
import static be.simongenin.synchouse.requests.StatusCodes.DRYER_WATER_PROBLEM;
import static be.simongenin.synchouse.requests.StatusCodes.MOWER_START;
import static be.simongenin.synchouse.requests.StatusCodes.MOWER_STOP;
import static be.simongenin.synchouse.requests.StatusCodes.SHUTTERS_CLOSE;
import static be.simongenin.synchouse.requests.StatusCodes.SHUTTERS_OPEN;
import static be.simongenin.synchouse.requests.StatusCodes.WASHING_MACHINE_ELECTRICAL_PROBLEM;
import static be.simongenin.synchouse.requests.StatusCodes.WASHING_MACHINE_PROGRAM;
import static be.simongenin.synchouse.requests.StatusCodes.WASHING_MACHINE_START;
import static be.simongenin.synchouse.requests.StatusCodes.WASHING_MACHINE_STOP;
import static be.simongenin.synchouse.requests.StatusCodes.WASHING_MACHINE_WATER_PROBLEM;
import static be.simongenin.synchouse.requests.StatusCodes.WINDOWS_CLOSE;
import static be.simongenin.synchouse.requests.StatusCodes.WINDOWS_OPEN;


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
        applyStatusCode(statusCode, data);

        /**
         * Send a notification to the device
         */
        NotificationHandler.sendNotification(message, this, MainActivity.class);

    }


    /**
     * This method dispatch the status code.
     * Then, it processes them.
     *
     * @param statusCode the status code
     * @param data additional data are in there. Such as the grass height
     */
    private void applyStatusCode(int statusCode, Bundle data) {

        /**
         * Objects (devices)
         */
        Alarm alarm = new Alarm();
        Windows windows = new Windows();
        Mower mower = new Mower();
        DomesticMachine dryer = new DomesticMachine();
        DomesticMachine washingMachine = new DomesticMachine();
        DomesticMachine dishWasher = new DomesticMachine();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        /**
         * Retrive their states
         */
        alarm.retrieveState(preferences);
        windows.retrieveState(preferences);
        mower.retrieveState(preferences);
        dryer.retrieveState(DomesticMachine.Type.DRYER, preferences);
        washingMachine.retrieveState(DomesticMachine.Type.WASHING_MACHINE, preferences);
        dishWasher.retrieveState(DomesticMachine.Type.DISH_WASHER, preferences);

        switch (statusCode) {

            /**
             * Alarms
             */

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

            /**
             * Windows
             */

            case WINDOWS_OPEN:
                windows.setWindowState(Windows.state.OPEN);
                break;

            case WINDOWS_CLOSE:
                windows.setWindowState(Windows.state.CLOSED);
                break;

            case SHUTTERS_OPEN:
                windows.setShutterState(Windows.state.OPEN);
                break;

            case SHUTTERS_CLOSE:
                windows.setShutterState(Windows.state.CLOSED);
                break;

            /**
             * Mower
             */

            case MOWER_START:
                // When we start the mower, we need to know the grass height
                mower.setSizeGrass(Integer.parseInt(data.getString("grass_height")));
                mower.setWorking(true);
                break;

            case MOWER_STOP:
                mower.setWorking(false);
                break;

            /**
             * Dryer
             */
            case DRYER_PROGRAM:
                dryer.start();
                break;

            case DRYER_START:
                dryer.start();
                break;

            case DRYER_STOP:
                dryer.stop();
                break;

            case DRYER_WATER_PROBLEM:
                dryer.stop();
                break;

            case DRYER_ELECTRICAL_PROBLEM:
                dryer.stop();
                break;

            /**
             * Washing machine
             */

            case WASHING_MACHINE_PROGRAM:
                washingMachine.start();
                break;

            case WASHING_MACHINE_START:
                washingMachine.start();
                break;

            case WASHING_MACHINE_STOP:
                washingMachine.stop();
                break;

            case WASHING_MACHINE_WATER_PROBLEM:
                washingMachine.stop();
                break;

            case WASHING_MACHINE_ELECTRICAL_PROBLEM:
                washingMachine.stop();
                break;

            /**
             * Dish washer
             */

            case DISH_WASHER_PROGRAM:
                dishWasher.start();
                break;

            case DISH_WASHER_START:
                dishWasher.start();
                break;

            case DISH_WASHER_STOP:
                dishWasher.stop();
                break;

            case DISH_WASHER_WATER_PROBLEM:
                dishWasher.stop();
                break;

            case DISH_WASHER_ELECTRICAL_PROBLEM:
                dishWasher.stop();
                break;

        }

        alarm.saveState(PreferenceManager.getDefaultSharedPreferences(this));

    }

}
