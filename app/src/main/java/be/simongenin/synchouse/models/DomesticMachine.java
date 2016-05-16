package be.simongenin.synchouse.models;


import android.content.SharedPreferences;

import be.simongenin.synchouse.listeners.OnStateChangeListener;

public class DomesticMachine {

    /**
     * The different states a machine can be
     */
    public enum Type { DRYER, WASHING_MACHINE, DISH_WASHER }

    /**
     * The states.
     */
    protected boolean isWorking;
    protected boolean isProgrammed;

    /**
     * The broadcaster
     */
    OnStateChangeListener stateBroadcaster;

    /**
     * Set the state listener
     */
    public void setOnStateChangeListener(OnStateChangeListener stateListener) {
        stateBroadcaster = stateListener;
    }

    /**
     * Start a machine
     */
    public void start() {

        isWorking = true;

        if (stateBroadcaster != null) {
            stateBroadcaster.onStateChange();
        }

    }

    /**
     * Stop the machine
     */
    public void stop() {

        isWorking = false;

        if (stateBroadcaster != null) {
            stateBroadcaster.onStateChange();
        }

    }

    /**
     * Toggle between programmed/not programmed
     */
    public void setProgrammed(boolean programmed) {
        isProgrammed = programmed;

        if (stateBroadcaster != null) {
            stateBroadcaster.onStateChange();
        }

    }

    /**
     * Getter for the working state
     */
    public boolean isWorking() {
        return isWorking;
    }

    /**
     * Getter for the "is programmed" state
     */
    public boolean isProgrammed() {
        return isProgrammed;
    }

    /**
     * Save the state of the object in the shared preferences
     */
    public void saveState(Type type, SharedPreferences preferences) {

        switch (type) {

            case DRYER:
                preferences.edit().putBoolean("dryer_is_working", isWorking).apply();
                preferences.edit().putBoolean("dryer_is_programmed", isProgrammed).apply();
                break;
            case WASHING_MACHINE:
                preferences.edit().putBoolean("wm_is_working", isWorking).apply();
                preferences.edit().putBoolean("wm_is_programmed", isProgrammed).apply();
                break;
            case DISH_WASHER:
                preferences.edit().putBoolean("dw_is_working", isWorking).apply();
                preferences.edit().putBoolean("dw_is_programmed", isProgrammed).apply();
                break;
        }

    }

    /**
     * Retrieve the state of the object in the shared preferences
     */
    public void retrieveState(Type type, SharedPreferences preferences) {

        switch (type) {

            case DRYER:
                isWorking = preferences.getBoolean("dryer_is_working", false);
                isProgrammed = preferences.getBoolean("dryer_is_programmed", false);
                break;
            case WASHING_MACHINE:
                isWorking = preferences.getBoolean("wm_is_working", false);
                isProgrammed = preferences.getBoolean("wm_is_programmed", false);
                break;
            case DISH_WASHER:
                isWorking = preferences.getBoolean("dw_is_working", false);
                isProgrammed = preferences.getBoolean("dw_is_programmed", false);
                break;
        }

    }

}
