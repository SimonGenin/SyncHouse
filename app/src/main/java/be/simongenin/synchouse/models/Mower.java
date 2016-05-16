package be.simongenin.synchouse.models;

import android.content.SharedPreferences;

import be.simongenin.synchouse.listeners.OnStateChangeListener;

/**
 * @author Simon Genin
 *
 * This class is the model for a mower.
 */
public class Mower {

    /**
     * The states.
     */
    private int sizeGrass;
    private boolean isWorking;
    private boolean isInterrupted;

    /**
     * The broadcaster
     */
    OnStateChangeListener stateBroadcaster;

    /**
     * Controller
     */
    public Mower() {
        sizeGrass = 2;
    }

    /**
     * Set the state listener
     */
    public void setOnStateChangeListener(OnStateChangeListener stateListener) {
        stateBroadcaster = stateListener;
    }

    /**
     * Turn on/off the mower
     */
    public void setWorking(boolean working) {

        isWorking = working;
        isInterrupted = false;

        if (stateBroadcaster != null) {
            stateBroadcaster.onStateChange();
        }

    }

    /**
     * Put the mower in a state of interruption.
     */
    public void interrupt(boolean b) {

        isInterrupted = true;
        isWorking = true;

        if (stateBroadcaster != null) {
            stateBroadcaster.onStateChange();
        }
    }

    /**
     * Set the grass size
     */
    public void setSizeGrass(int sizeGrass) {

        this.sizeGrass = sizeGrass;

        if (stateBroadcaster != null) {
            stateBroadcaster.onStateChange();
        }
    }

    /**
     * Getter for the interrupted state
     */
    public boolean isInterrupted() {
        return isInterrupted;
    }

    /**
     * Getter for the grass height
     */
    public int getSizeGrass() {
        return sizeGrass;
    }

    /**
     * Getter for the working state
     */
    public boolean isWorking() {
        return isWorking;
    }

    /**
     * Save the state of the object in the shared preferences
     */
    public void saveState(SharedPreferences preferences) {

        preferences.edit().putBoolean("mower_is_working", isWorking).apply();
        preferences.edit().putInt("size_grass", sizeGrass).apply();

    }

    /**
     * Retrieve the state of the object in the shared preferences
     */
    public void retrieveState(SharedPreferences preferences) {

        isWorking = preferences.getBoolean("mower_is_working", false);
        sizeGrass = preferences.getInt("size_grass", 1);

    }

}
