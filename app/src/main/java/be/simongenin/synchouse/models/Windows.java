package be.simongenin.synchouse.models;

import android.content.SharedPreferences;

public class Windows{

    public enum state { OPEN, CLOSED }

    private state windowState;
    private state shutterState;

    OnStateChangeListener stateBroadcaster;

    public Windows() {

        windowState = state.CLOSED;
        shutterState = state.CLOSED;

    }

    public void setWindowState(state s) {
        windowState = s;

        if (stateBroadcaster != null) {
            stateBroadcaster.onStateChange();
        }
    }

    public void setShutterState(state s) {
        shutterState = s;
        if (stateBroadcaster != null) {
            stateBroadcaster.onStateChange();
        }
    }

    public void setOnStateChangeListener(OnStateChangeListener stateListener) {
        stateBroadcaster = stateListener;
    }

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

    public state getWindowState() {
        return windowState;
    }

    public state getShutterState() {
        return shutterState;
    }

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
