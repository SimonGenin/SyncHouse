package be.simongenin.synchouse.models;

import android.content.SharedPreferences;

public class Alarm {

    public enum state { PARTIAL, TOTAL, NONE }

    private state currentState;
    private boolean isAlarmSoundActive;


    public Alarm() {

        currentState = state.NONE;
        isAlarmSoundActive = false;

    }

    public void turnOffAlarmSound() {

        isAlarmSoundActive = false;
        currentState = state.NONE;

    }

    public void setState(state s) {

        currentState = s;
    }

    private state getState() {
        return currentState;
    }

    public boolean isAlarmSoundActive() {
        return isAlarmSoundActive;
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

        preferences.edit().putBoolean("is_alarm_active", isAlarmSoundActive).apply();
        preferences.edit().putInt("current_state", state).apply();

    }

    public void retrieveState(SharedPreferences preferences) {

        isAlarmSoundActive = preferences.getBoolean("is_alarm_active", false);
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
