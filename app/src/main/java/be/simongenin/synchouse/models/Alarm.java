package be.simongenin.synchouse.models;

public class Alarm {

    public enum state { PARTIAL, TOTAL, NONE }

    private state currentState;
    private boolean isAlarmsoundActive;

    private AlarmStateListener alarmStateBroadcaster;

    public Alarm() {

        currentState = state.NONE;
        broadcastStateChanged(state.NONE);
        isAlarmsoundActive = false;

    }

    public void turnOffAlarmSound() {

        // TODO check for position !

        isAlarmsoundActive = false;
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

    public boolean isAlarmsoundActive() {
        return isAlarmsoundActive;
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
