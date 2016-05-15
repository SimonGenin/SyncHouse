package be.simongenin.synchouse.models;

import android.content.SharedPreferences;

import be.simongenin.synchouse.listeners.OnStateChangeListener;

public class Mower {

    private int sizeGrass;
    private boolean isWorking;
    private boolean isInterrupted;

    OnStateChangeListener stateBroadcaster;

    public Mower() {
        sizeGrass = 2;
    }

    public boolean isInterrupted() {
        return isInterrupted;
    }

    public void setWorking(boolean working) {

        isWorking = working;
        isInterrupted = false;

        if (stateBroadcaster != null) {
            stateBroadcaster.onStateChange();
        }

    }

    public void interrupt(boolean b) {

        isInterrupted = true;

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

    public void saveState(SharedPreferences preferences) {

        preferences.edit().putBoolean("mower_is_working", isWorking).apply();
        preferences.edit().putInt("size_grass", sizeGrass).apply();

    }

    public void retrieveState(SharedPreferences preferences) {

        isWorking = preferences.getBoolean("mower_is_working", false);
        sizeGrass = preferences.getInt("size_grass", 1);

    }

}
