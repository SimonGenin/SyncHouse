package be.simongenin.synchouse.models;

import android.content.SharedPreferences;

public class Alarm {

    public enum state { PARTIAL, TOTAL, NONE }

    private state currentState;
    private boolean isAlarmSoundActive;

    private AlarmStateListener alarmStateBroadcaster;

    public void saveState(SharedPreferences preferences) {

        preferences.edit().putBoolean("is_alarm_active", isAlarmSoundActive);

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

        preferences.edit().putInt("current_state", state);

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

    public Alarm() {

        currentState = state.NONE;
        broadcastStateChanged(state.NONE);
        isAlarmSoundActive = false;

    }

    public void turnOffAlarmSound() {

        isAlarmSoundActive = false;
        currentState = state.NONE;
        broadcastStateChanged(state.NONE);

    }

    public void setState(state s) {

        currentState = s;
        broadcastStateChanged(s);

    }

    private state getState() {
        return currentState;
    }

    public boolean isAlarmSoundActive() {
        return isAlarmSoundActive;
    }

    private void broadcastStateChanged(state s) {
        if ( alarmStateBroadcaster != null ) {
            alarmStateBroadcaster.alarmSateChanged(s);
        }
    }

    public interface AlarmStateListener {

        void alarmSateChanged(Alarm.state s);

    }

}
