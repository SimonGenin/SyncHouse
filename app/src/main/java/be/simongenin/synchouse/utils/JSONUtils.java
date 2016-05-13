package be.simongenin.synchouse.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Simon on 13/05/16.
 */
public class JSONUtils {

    public static int getStatusCode(JSONObject json) {

        int result = -1;

        try {
            result = json.getInt("status_code");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;

    }

    public static boolean getSuccess(JSONObject json) {

        boolean result = false;

        try {
            result = json.getBoolean("success");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;

    }

    public static String getMessage(JSONObject json) {

        String result = "";

        try {
            result = json.getString("message");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;

    }

    public static String getHomeID(JSONObject json) {

        String result = "";

        try {
            result = json.getString("home_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;

    }

    public static String getError(JSONObject json) {

        String result = "";

        try {
            result = json.getString("error_message");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;

    }

}
