package be.simongenin.synchouse.models;

import be.simongenin.synchouse.exceptions.AlarmActivatedAlarmException;
import be.simongenin.synchouse.exceptions.ShutterClosedException;
import be.simongenin.synchouse.exceptions.WindowOpenedException;

public class Windows implements Alarm.AlarmStateListener{

    public enum state { OPEN, CLOSED }

    private state windowState;
    private state shutterState;
    private Alarm.state alarmState;

    public Windows() {

        windowState = state.CLOSED;
        shutterState = state.CLOSED;
        alarmState = Alarm.state.NONE;

    }

    /**
     *
     * ouvre ou fermer les fenetres
     *
     * Pour ouvrir :
     * Si l'alarme est activée, envoi une excpetion.
     * Si volets pas ouvert, envoi une exception.
     *
     * @param s
     * @throws AlarmActivatedAlarmException
     * @throws ShutterClosedException
     */
    public void setWindowState(state s) throws AlarmActivatedAlarmException, ShutterClosedException {

        // Ouvir
        if (s == state.OPEN) {

            // Si pas d'alarmes
            if (alarmState == Alarm.state.NONE) {

                // Si volets ouvert
                if (shutterState == state.OPEN) {

                    windowState = state.OPEN;

                } else {

                    throw new ShutterClosedException();

                }

            }
            else {

                throw new AlarmActivatedAlarmException();

            }
        }

        // Fermer
        if (s == state.CLOSED) {

            windowState = state.CLOSED;

        }


    }

    /**
     *
     * Permet d'ouvrir et fermer les volets
     *
     * Pour femer :
     * si les fenetres sont ouvertes, envoi un exception
     *
     * Pour ourvir :
     * Si l'alarm est activée, envoi une exception
     *
     * @param s
     * @throws AlarmActivatedAlarmException
     * @throws WindowOpenedException
     */
    public void setShutterState(state s) throws AlarmActivatedAlarmException, WindowOpenedException {

        if (s == state.OPEN) {

            if (alarmState == Alarm.state.NONE) {

                shutterState = state.OPEN;

            } else {

                throw new AlarmActivatedAlarmException();

            }

        }

        if (s == state.CLOSED) {

            if (windowState == state.OPEN) {

                throw new WindowOpenedException();

            }

            else {

                shutterState = state.CLOSED;

            }

        }

    }

    @Override
    public void alarmSateChanged(Alarm.state s) {

        alarmState = s;

        // Si l'alarme est activée, on ferme tout
        if (s != Alarm.state.NONE) {

            try {
                setWindowState(state.CLOSED);
                setShutterState(state.CLOSED);
            } catch (AlarmActivatedAlarmException | WindowOpenedException | ShutterClosedException e) {
                e.printStackTrace();
                // Ne doit jamais arriver si les specs sont respectées
            }
        }

    }

}
