package be.simongenin.synchouse.models;

import android.content.SharedPreferences;

public class Alarm {


    public enum state { PARTIAL, TOTAL, NONE }

    private state currentState;
    private boolean isSirenActive;


    public Alarm() {

        currentState = state.NONE;
        isSirenActive = false;

    }

    public state getCurrentState() {
        return currentState;
    }

    public void turnOffAlarmSound() {

        isSirenActive = false;
        currentState = state.NONE;

    }

    public void setState(state s) {

        currentState = s;
    }


    public boolean isSirenActive() {
        return isSirenActive;
    }
    public void activeSiren() {
        isSirenActive = true;
    }
    public void deactivateSiren() {
        isSirenActive = false;
    }


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

        preferences.edit().putBoolean("is_alarm_active", isSirenActive).commit();
        preferences.edit().putInt("current_state", state).commit();

    }

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
