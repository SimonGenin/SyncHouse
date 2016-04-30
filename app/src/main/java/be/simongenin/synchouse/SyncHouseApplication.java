package be.simongenin.synchouse;

import android.app.Application;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import be.simongenin.synchouse.requests.OkHttpStack;

public class SyncHouseApplication extends Application {

    // The request queue for the application
    public RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext(), new OkHttpStack());

}
