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

public class SyncHouseApplication extends Application {


    public RequestQueue requestQueue;
    public boolean isUserConnected;
    public String homeID;
    public ConnectedHouse house;

    private SharedPreferences preferences;
    public String currentToken;
    public String password;

    @Override
    public void onCreate() {
        super.onCreate();

        currentToken = "BASE";

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        requestQueue = Volley.newRequestQueue(this, new OkHttpStack());

        isUserConnected = preferences.getBoolean("is_user_connected", false);
        homeID = preferences.getString("home_id", "");
        password = preferences.getString("password", "");

        house = new ConnectedHouse(this);
        house.retrieveState();
    }

    public void disconnect(boolean criticalError) {

        isUserConnected = false;
        Intent disconnectIntent = new Intent(this, LoginActivity.class);
        disconnectIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        disconnectIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (criticalError) {
            Toast.makeText(this, "Une erreur critique est survenue.", Toast.LENGTH_LONG).show();
        }

        startActivity(disconnectIntent);

    }



    public void persistState() {

        preferences.edit().putBoolean("is_user_connected", isUserConnected).commit();
        preferences.edit().putString("home_id", homeID).commit();
        preferences.edit().putString("password", password).commit();
        house.saveState();

    }

    public void retrieveState() {

        isUserConnected = preferences.getBoolean("is_user_connected", false);
        homeID = preferences.getString("home_id", "");
        password = preferences.getString("password", "");
        house.retrieveState();
    }

}
