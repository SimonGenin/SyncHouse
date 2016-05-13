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


import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.util.HashMap;
import java.util.Map;

import be.simongenin.synchouse.R;
import be.simongenin.synchouse.SyncHouseApplication;
import be.simongenin.synchouse.requests.PostRequest;
import be.simongenin.synchouse.utils.ServerUtils;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {

            /**
             * Get the token from GCM.
             */
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.i(TAG, "GCM Registration Token: " + token);

            /**
             * Hand to token to the server
             */
            sendRegistrationToServer(token);

            /**
             * On le met dans notre objet application afin de l'avoir globalement
             */
            ((SyncHouseApplication)getApplication()).currentToken = token;


            // TODO remove if not needed
            // Subscribe to topic channels
            // subscribeTopics(token);

            /**
             * Keep a trace in the prefs that we sent the token to the server
             */
            sharedPreferences.edit().putBoolean(GCMPreferences.SENT_TOKEN_TO_SERVER, true).apply();

        } catch (Exception e) {

            Log.d(TAG, "Failed to complete token refresh", e);

            /**
             * Keep a trace in the prefs that we didn't sent the token to the server
             */
            sharedPreferences.edit().putBoolean(GCMPreferences.SENT_TOKEN_TO_SERVER, false).apply();
        }

        /**
         * Notify the UI that we have finished the registration.
         * So we can't stop the progress bar.
         */
        Intent registrationComplete = new Intent(GCMPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);

}

    /**
     * Persist registration to server.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(final String token) {

        final SyncHouseApplication application = (SyncHouseApplication)getApplication();

        /**
         * We simply do a post request with the token as argument.
         */

        String url = ServerUtils.TOKEN_URL;
        Map<String, String> params = new HashMap<>();
        params.put("gcm_token", token);

        PostRequest tokenRequest = new PostRequest(url, params, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                /**
                 * Everything went well.
                 */
            }

        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                /**
                 * If it doesn't work, we can't do much.
                 * It means that we won't get notifications from GCM.
                 * So the user won't be able to control the devices.
                 * To prevent any major issue, we disconnect this user.
                 */

                Log.e(TAG, error.toString());
                application.disconnect(true);

            }

        });

        application.requestQueue.add(tokenRequest);

    }


}



