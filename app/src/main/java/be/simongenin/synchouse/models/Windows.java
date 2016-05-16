package be.simongenin.synchouse.models;

import android.content.SharedPreferences;

import be.simongenin.synchouse.listeners.OnStateChangeListener;

/**
 * @author Simon Genin
 *
 * This class is the model for a windows.
 */
public class Windows{

    /**
     * The possible states for the windows/shutters
     */
    public enum state { OPEN, CLOSED }

    /**
     * The states.
     */
    private state windowState;
    private state shutterState;

    /**
     * The broadcaster
     */
    OnStateChangeListener stateBroadcaster;

    /**
     * Controller
     */
    public Windows() {
        windowState = state.CLOSED;
        shutterState = state.CLOSED;
    }

    /**
     * Set the state listener
     */
    public void setOnStateChangeListener(OnStateChangeListener stateListener) {
        stateBroadcaster = stateListener;
    }

    /**
     * Set the windows state
     */
    public void setWindowState(state s) {
        windowState = s;

        if (stateBroadcaster != null) {
            stateBroadcaster.onStateChange();
        }
    }

    /**
     * Set the shutter state
     */
    public void setShutterState(state s) {
        shutterState = s;
        if (stateBroadcaster != null) {
            stateBroadcaster.onStateChange();
        }
    }

    /**
     * Getter for the window state
     */
    public state getWindowState() {
        return windowState;
    }

    /**
     * Getter for shutter state
     */
    public state getShutterState() {
        return shutterState;
    }

    /**
     * Save the state of the object in the shared preferences
     */
    public void saveState(SharedPreferences preferences) {

        int windowsState = 1;
        int shuttersState = 1;

        switch (windowState) {

            case OPEN:
                windowsState = 2;
                break;
            case CLOSED:
                windowsState = 1;
                break;
        }

        switch (shutterState) {

            case OPEN:
                shuttersState = 2;
                break;
            case CLOSED:
                shuttersState = 1;
                break;
        }

        preferences.edit().putInt("window_state", windowsState).apply();
        preferences.edit().putInt("shutter_state", shuttersState).apply();

    }

    /**
     * Retrieve the state of the object in the shared preferences
     */
    public void retrieveState(SharedPreferences preferences) {

        int windowsState = preferences.getInt("window_state", 1);
        int shuttersState = preferences.getInt("shutter_state", 1);

        switch (windowsState) {

            case 1:
                windowState = state.CLOSED;
                break;
            case 2:
                windowState = state.OPEN;
                break;
        }

        switch (shuttersState) {

            case 1:
                shutterState = state.CLOSED;
                break;
            case 2:
                shutterState = state.OPEN;
                break;
        }

    }

}
