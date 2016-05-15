package be.simongenin.synchouse.models;


import android.content.SharedPreferences;

public class DomesticMachine {

    public enum Type { DRYER, WASHING_MACHINE, DISH_WASHER }

    protected boolean isWorking;
    protected boolean isProgrammed;

    public boolean isWorking() {
        return isWorking;
    }

    public boolean isProgrammed() {
        return isProgrammed;
    }

    OnStateChangeListener stateBroadcaster;

    public void start() {

        isWorking = true;

        if (stateBroadcaster != null) {
            stateBroadcaster.onStateChange();
        }

    }

    public void stop() {

        isWorking = false;

        if (stateBroadcaster != null) {
            stateBroadcaster.onStateChange();
        }

    }

    public void setProgrammed(boolean programmed) {
        isProgrammed = programmed;
    }

    public void setOnStateChangeListener(OnStateChangeListener stateListener) {
        stateBroadcaster = stateListener;
    }

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
