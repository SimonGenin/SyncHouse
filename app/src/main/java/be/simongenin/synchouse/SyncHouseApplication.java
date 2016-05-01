package be.simongenin.synchouse;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import be.simongenin.synchouse.requests.OkHttpStack;

public class SyncHouseApplication extends Application {

    // The request queue for the application
    public RequestQueue requestQueue;
    public boolean isUserConnected;

    @Override
    public void onCreate() {
        super.onCreate();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        requestQueue = Volley.newRequestQueue(this, new OkHttpStack());
        isUserConnected = preferences.getBoolean("isUserConnected", false);

    }

    public void disconnect() {

        isUserConnected = false;
        Intent disconnectIntent = new Intent(this, LoginActivity.class);
        disconnectIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        disconnectIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Toast.makeText(this, "Une erreur critique est survenue.", Toast.LENGTH_LONG).show();
        startActivity(disconnectIntent);

    }
}
