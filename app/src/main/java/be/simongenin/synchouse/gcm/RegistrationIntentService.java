package be.simongenin.synchouse.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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

/**
 * @author Simon Genin and Google
 *
 */
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
             * Hand the token to the server
             */
            sendRegistrationToServer(token);

            /**
             * We keep the token in the app object for later use
             */
            ((SyncHouseApplication)getApplication()).currentToken = token;

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



