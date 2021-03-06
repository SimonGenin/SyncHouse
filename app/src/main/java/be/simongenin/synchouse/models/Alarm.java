package be.simongenin.synchouse.models;

import android.content.SharedPreferences;

import be.simongenin.synchouse.listeners.OnStateChangeListener;

/**
 * @author Simon Genin
 *
 * This class is the model for an alarm.
 */
public class Alarm {

    /**
     * The different states for the alarm.
     */
    public enum state { PARTIAL, TOTAL, NONE }

    /**
     * The states.
     */
    private state currentState;
    private boolean isSirenActive;

    /**
     * The broadcaster
     */
    private OnStateChangeListener stateBroadcaster;

    /**
     * Controller
     */
     public Alarm() {
        currentState = state.NONE;
        isSirenActive = false;
    }

    /**
     * Set the state listener
     */
    public void setOnStateChangeListener(OnStateChangeListener stateListener) {
        stateBroadcaster = stateListener;
    }

    /**
     * Turn on the siren
     */
    public void activeSiren() {
        isSirenActive = true;

        if (stateBroadcaster != null) {
            stateBroadcaster.onStateChange();
        }
    }

    /**
     * Turn off the siren
     */
    public void turnOffAlarmSound() {
        isSirenActive = false;

        if (stateBroadcaster != null) {
            stateBroadcaster.onStateChange();
        }

    }

    /**
     * Change the state of the alarm
     */
    public void setState(state s) {
        currentState = s;

        if (stateBroadcaster != null) {
            stateBroadcaster.onStateChange();
        }

    }

    /**
     * Getter of the siren
     */
    public boolean isSirenActive() {
        return isSirenActive;
    }

    /**
     * Getter of the state
     */
    public state getCurrentState() {
        return currentState;
    }

    /**
     * Save the state of the object in the shared preferences
     */
    public void saveState(SharedPreferences preferences) {

        int state = 1;
        switch (currentState) {

            case PARTIAL:
                state = 2;
                break;
            case TOTAL:
                state = 3;
                break;
            case NONE:
                state = 1;
                break;
        }

        preferences.edit().putBoolean("is_alarm_active", isSirenActive).apply();
        preferences.edit().putInt("current_state", state).apply();

    }

    /**
     * Retrieve the state of the object in the shared preferences
     */
    public void retrieveState(SharedPreferences preferences) {

        isSirenActive = preferences.getBoolean("is_alarm_active", false);
        int state = preferences.getInt("current_state", 1);

        switch (state) {

            case 1:
                currentState = Alarm.state.NONE;
                break;
            case 2:
                currentState = Alarm.state.PARTIAL;
                break;
            case 3:
                currentState = Alarm.state.TOTAL;
                break;

        }

    }

}
