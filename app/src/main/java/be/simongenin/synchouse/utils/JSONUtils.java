package be.simongenin.synchouse.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Simon Genin
 *
 * Some utility classes to work with our JSON.
 */
public class JSONUtils {

    /**
     * Get the "success" param from a json object
     */
    public static boolean getSuccess(JSONObject json) {

        boolean result = false;

        try {
            result = json.getBoolean("success");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;

    }

    /**
     * Get the "error_message" param from a json object
     */
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
