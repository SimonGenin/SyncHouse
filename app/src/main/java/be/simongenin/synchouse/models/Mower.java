package be.simongenin.synchouse.models;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

public class Mower {

    private int sizeGrass;
    private boolean isWorking;

    OnStateChangeListener stateBroadcaster;

    public Mower() {
        sizeGrass = 2;
    }

    /**
     * Start if mower finished properly it's 4h
     * check if can start first, runtime exception
     */
    public void start() {

        isWorking = true;

        if (stateBroadcaster != null) {
            stateBroadcaster.onStateChange();
        }

    }

    /**
     * Stop the mower.
     * No matter what.
     */
    public void stop() {

        isWorking = false;

        if (stateBroadcaster != null) {
            stateBroadcaster.onStateChange();
        }

    }

    public int getSizeGrass() {
        return sizeGrass;
    }

    public void setSizeGrass(int sizeGrass) {
        this.sizeGrass = sizeGrass;

        if (stateBroadcaster != null) {
            stateBroadcaster.onStateChange();
        }
    }

    public void setOnStateChangeListener(OnStateChangeListener stateListener) {
        stateBroadcaster = stateListener;
    }

    public boolean isWorking() {
        return isWorking;
    }

    public void setWorking(boolean working) {
        isWorking = working;
    }

    @SuppressLint("CommitPrefEdits")
    public void saveState(SharedPreferences preferences) {

        preferences.edit().putBoolean("mower_is_working", isWorking).commit();
        preferences.edit().putInt("size_grass", sizeGrass).commit();

    }

    public void retrieveState(SharedPreferences preferences) {

        isWorking = preferences.getBoolean("mower_is_working", false);
        sizeGrass = preferences.getInt("size_grass", 1);

    }
}
