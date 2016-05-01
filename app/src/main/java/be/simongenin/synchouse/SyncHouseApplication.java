package be.simongenin.synchouse;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import be.simongenin.synchouse.requests.OkHttpStack;

public class SyncHouseApplication extends Application {

    // The request queue for the application
    public RequestQueue requestQueue;
    public boolean isUserConnected;
    public String homeID;

    private SharedPreferences preferences;

    @Override
    public void onCreate() {
        super.onCreate();

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        requestQueue = Volley.newRequestQueue(this, new OkHttpStack());

        isUserConnected = preferences.getBoolean("is_user_connected", false);
        homeID = preferences.getString("home_id", "");

        Log.i("SynchouseApplication", "Retrieve from prefs : " + isUserConnected);
        Log.i("SynchouseApplication", "Retrieve from prefs : " + homeID);

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


    public void persistState() {;

        preferences.edit().putBoolean("is_user_connected", isUserConnected).commit();
        preferences.edit().putString("home_id", homeID).commit();

        Log.i("SynchouseApplication", "Put into prefs : " + isUserConnected);
        Log.i("SynchouseApplication", "Put into prefs : " + homeID);

    }

    public void retrieveState() {

        isUserConnected = preferences.getBoolean("is_user_connected", false);
        homeID = preferences.getString("home_id", "");

        Log.i("SynchouseApplication", "Retrieve from prefs : " + isUserConnected);
        Log.i("SynchouseApplication", "Retrieve from prefs : " + homeID);

    }
}
