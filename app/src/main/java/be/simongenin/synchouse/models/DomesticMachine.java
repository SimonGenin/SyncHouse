package be.simongenin.synchouse.models;


import android.content.SharedPreferences;

public class DomesticMachine {

    public enum Type { DRYER, WASHING_MACHINE, DISH_WASHER }

    protected boolean isWorking;

    public void start() {

        isWorking = true;

    }

    public void stop() {

        isWorking = false;

    }

    public void saveState(Type type, SharedPreferences preferences) {

        switch (type) {

            case DRYER:
                preferences.edit().putBoolean("dryer_is_working", isWorking).apply();
                break;
            case WASHING_MACHINE:
                preferences.edit().putBoolean("wm_is_working", isWorking).apply();
                break;
            case DISH_WASHER:
                preferences.edit().putBoolean("dw_is_working", isWorking).apply();
                break;
        }

    }

    public void retrieveState(Type type, SharedPreferences preferences) {

        switch (type) {

            case DRYER:
                isWorking = preferences.getBoolean("dryer_is_working", false);
                break;
            case WASHING_MACHINE:
                isWorking = preferences.getBoolean("wm_is_working", false);
                break;
            case DISH_WASHER:
                isWorking = preferences.getBoolean("dw_is_working", false);
                break;
        }

    }

}
