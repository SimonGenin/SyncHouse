package be.simongenin.synchouse.utils;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import be.simongenin.synchouse.SyncHouseApplication;
import be.simongenin.synchouse.models.Alarm;
import be.simongenin.synchouse.models.Windows;
import be.simongenin.synchouse.requests.PostRequest;

/**
 * @author Simon Genin
 *
 * Helper methods to make sure the app is sync.
 */
public class SyncUtils {


    /**
     * Get all the data from the server
     */
    public static void putLocalDataUpToDate(JSONObject jsonObject, SyncHouseApplication application) {

        try {

            /**
             * Get the right object
             */
            JSONObject statesJSON = jsonObject.getJSONObject("states");

            /**
             * Get the values
             */
            int alarm = statesJSON.getInt("alarm");
            int siren = statesJSON.getInt("siren");
            int windows = statesJSON.getInt("windows");
            int shutters = statesJSON.getInt("shutters");
            int mower = statesJSON.getInt("mower");
            int grassSize = statesJSON.getInt("grass_size");
            int washingMachineProgrammed = statesJSON.getInt("washing_machine_programmed");
            int washingMachineRunning = statesJSON.getInt("washing_machine_running");
            int dryerProgrammed = statesJSON.getInt("dryer_programmed");
            int dryerRunning = statesJSON.getInt("dryer_running");
            int dishWasherProgrammed = statesJSON.getInt("dish_washer_programmed");
            int dishWasherRunning = statesJSON.getInt("dish_washer_running");

            /**
             * Handles all the states
             */
            if (alarm == 1) application.house.alarm.setState(Alarm.state.NONE);
            if (alarm == 2) application.house.alarm.setState(Alarm.state.PARTIAL);
            if (alarm == 3) application.house.alarm.setState(Alarm.state.TOTAL);
            if (siren == 1) application.house.alarm.turnOffAlarmSound();
            if (siren == 2) application.house.alarm.activeSiren();

            if (mower == 1) application.house.mower.setWorking(false);
            if (mower == 2) application.house.mower.setWorking(true);
            if (mower == 3) application.house.mower.interrupt(true);
            application.house.mower.setSizeGrass(grassSize);

            if (windows == 1) application.house.windows.setWindowState(Windows.state.CLOSED);
            if (windows == 2) application.house.windows.setWindowState(Windows.state.OPEN);
            if (shutters == 1) application.house.windows.setShutterState(Windows.state.CLOSED);
            if (shutters == 2) application.house.windows.setShutterState(Windows.state.OPEN);

            if (washingMachineProgrammed == 1) application.house.washingMachine.setProgrammed(false);
            if (washingMachineProgrammed == 2) application.house.washingMachine.setProgrammed(true);
            if (washingMachineRunning == 1) application.house.washingMachine.stop();
            if (washingMachineRunning == 2) application.house.washingMachine.start();

            if (dishWasherProgrammed == 1) application.house.dishWasher.setProgrammed(false);
            if (dishWasherProgrammed == 2) application.house.dishWasher.setProgrammed(true);
            if (dishWasherRunning == 1) application.house.dishWasher.stop();
            if (dishWasherRunning == 2) application.house.dishWasher.start();

            if (dryerProgrammed == 1) application.house.dryer.setProgrammed(false);
            if (dryerProgrammed == 2) application.house.dryer.setProgrammed(true);
            if (dryerRunning == 1) application.house.dryer.stop();
            if (dryerRunning == 2) application.house.dryer.start();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Sync the data fro the server to the app
     */
    public static void sync(final Context context, final SyncHouseApplication application) {

        /**
         * We do the same requets as the login request
         */

        Map<String, String> parameters = new HashMap<>();
        parameters.put("home_id", application.homeID);
        parameters.put("password", application.password);

        PostRequest syncRequest = new PostRequest(ServerUtils.LOGIN_URL, parameters, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    /**
                     * When we retrieve the date, we use our other method to update all the states
                     */

                    putLocalDataUpToDate(new JSONObject(response), application);
                    Toast.makeText(context, "Données syncronisées", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(context, "Impossible de synchroniser les données maintenant", Toast.LENGTH_SHORT).show();

            }
        });

        application.requestQueue.add(syncRequest);
    }
}
