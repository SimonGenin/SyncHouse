package be.simongenin.synchouse;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import be.simongenin.synchouse.models.ConnectedHouse;
import be.simongenin.synchouse.requests.OkHttpStack;

/**
 * @author Simon Genin
 *
 * The application class itself.
 */
public class SyncHouseApplication extends Application {


    /**
     * Request queue used through the entire application
     */
    public RequestQueue requestQueue;

    /**
     * Useful data
     */
    public boolean isUserConnected;
    public String homeID;
    public String currentToken;
    public String password;

    /**
     * The house object that holds all the components
     */
    public ConnectedHouse house;

    /**
     * The preferences used to save the state of the app
     */
    private SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();

        /**
         * The prefs getting back from the prefs manager
         */
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        /**
         * Retrieve the usefull data
         */
        currentToken = "BASE";
        isUserConnected = preferences.getBoolean("is_user_connected", false);
        homeID = preferences.getString("home_id", "");
        password = preferences.getString("password", "");

        /**
         * Creation of a new request queue with the OkHttpStack
         */
        requestQueue = Volley.newRequestQueue(this, new OkHttpStack());

        /**
         * Get back the house object
         */
        house = new ConnectedHouse(this);
        house.retrieveState();
    }

    /**
     * Disconnect the user.
     */
    public void disconnect(boolean criticalError) {

        isUserConnected = false;

        /**
         * Sent the disconnect user to the login page
         */
        Intent disconnectIntent = new Intent(this, LoginActivity.class);
        disconnectIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        disconnectIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        /**
         * If the disconnection happens because of an error, let's print a message
         */
        if (criticalError) {
            Toast.makeText(this, "Une erreur critique est survenue.", Toast.LENGTH_LONG).show();
        }

        startActivity(disconnectIntent);

    }


    /**
     * Persist the state of the app.
     */
    public void persistState() {

        preferences.edit().putBoolean("is_user_connected", isUserConnected).apply();
        preferences.edit().putString("home_id", homeID).apply();
        preferences.edit().putString("password", password).apply();
        house.saveState();

    }

    /**
     * Retrieve the state of the app.
     */
    public void retrieveState() {

        isUserConnected = preferences.getBoolean("is_user_connected", false);
        homeID = preferences.getString("home_id", "");
        password = preferences.getString("password", "");
        house.retrieveState();
    }

}
