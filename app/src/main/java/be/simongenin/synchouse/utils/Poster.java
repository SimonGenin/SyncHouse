package be.simongenin.synchouse.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import be.simongenin.synchouse.SyncHouseApplication;
import be.simongenin.synchouse.requests.PostRequest;


public class Poster {

    private OnPostFailListener failBroadcaster;

    public void setOnPostFailListener(OnPostFailListener failListener) {
        this.failBroadcaster = failListener;
    }

    public void postState(int code, final Context context, SyncHouseApplication application, Map<String, String> additionalArgs) {

        /**
         * Creates the post request parameters.
         * The server requires the status code, the home ID and the password.
         *
         * The home id is required so the server knows the modified house (obviously) and
         * the password is used to identify the client.
         * Indeed, if there was no password, anybody could have send post request to the server
         * and change the houses state as long as this person knows the right URL.         *
         */
        Map<String, String> params = new HashMap<>();
        params.put("status_code", String.valueOf(code));
        params.put("home_id", application.homeID);
        params.put("password", application.password);

        /**
         * Add additional parameters if needed
         */
        if (additionalArgs != null) {
            params.putAll(additionalArgs);
        }

        /**
         * The actual request + its listener
         */
        PostRequest request = new PostRequest(ServerUtils.STATUS, params, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                /**
                 * A response from this request is as follows :
                 *
                 * {
                 *      success : true|false,
                 *      message : "",
                 *      error_message : ""
                 * }
                 *
                 * The success param represents whether or not the server accepted the changes.
                 * If yes, there is a success message in the message param. => READ BELOW
                 * If false, the error_message param contains a relevant message defining what's
                 * the problem.
                 *
                 * THE "MESSAGE" PARAM IS NO LONGER USED. IN CASE OF SUCCESS, THE SERVER WILL
                 * NOTIFY THE CHANGES THROUGH GCM.
                 *
                 */

                try {

                    // Let's make our response a json object
                    JSONObject jsonObject = new JSONObject(response);

                    if (JSONUtils.getSuccess(jsonObject)) {

                        Log.i("POST REQUEST TO SERVER", "Success");

                    } else {

                        Log.i("POST REQUEST TO SERVER", "Not Successful");
                        failBroadcaster.onPostFail();
                        Toast.makeText(context, JSONUtils.getError(jsonObject), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {

                    /**
                     * Error with the JSON.
                     * It shouldn't append, unless the server changes something in the response.
                     */
                    Log.i("POST REQUEST TO SERVER", "JSON error");
                    failBroadcaster.onPostFail();
                    Toast.makeText(context, "La reponse du server est corrompue.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                /**
                 * Error !
                 * We couldn't reach the server.
                 */
                Log.i("POST REQUEST TO SERVER", "Volley response error");
                failBroadcaster.onPostFail();
                Toast.makeText(context, "Une erreur est survenue.", Toast.LENGTH_LONG).show();

            }
        });

        application.requestQueue.add(request);

    }

}

