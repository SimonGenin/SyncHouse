package be.simongenin.synchouse.models;

import java.util.Calendar;

import be.simongenin.synchouse.exceptions.ForbiddenHoursException;

public class Mower {

    private int forbiddenHourMorning = 8 * 60;
    private int forbiddenHourEvening = 22 * 60;

    private boolean hasBeenInterrupted;
    private int leftFromLastWork;

    private boolean isWorking;
    private int timeToWork;
    private int startTime;

    private int sizeGrass;

    public Mower() {

        sizeGrass = 2;

    }

    public void start() {
        start(4 * 60);
    }

    /**
     * Start if mower finished properly it's 4h
     * check if can start first, runtime exception
     */
    public void start(int time) {

        int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minutesOfHour = Calendar.getInstance().get(Calendar.MINUTE);

        startTime = hourOfDay * 60 + minutesOfHour;

        if (startTime < forbiddenHourMorning || startTime >= forbiddenHourEvening) {

            throw new ForbiddenHoursException();

        }

        else {

            isWorking = true;
            timeToWork = time;

        }

    }

    /**
     * Stop the mower.
     * No matter what.
     */
    public void stop() {

        isWorking = false;
        hasBeenInterrupted = false;
        leftFromLastWork = 0;

    }

    /**
     * Pospose simplement la fin de la tonte a la prochaine reprise
     */
    public void interrupt() {

        hasBeenInterrupted = true;

        int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minutesOfHour = Calendar.getInstance().get(Calendar.MINUTE);
        int interruptionTime = hourOfDay * 60 + minutesOfHour;

        leftFromLastWork = timeToWork - (startTime - interruptionTime);

        isWorking = false;

    }

    /**
     * Redemarre la tondeuse pour le bon nombre de temps
     */
    public void restart() {

        if (!hasBeenInterrupted) start();
        start(leftFromLastWork);

    }

    public boolean hasBeenInterrupted() {
        return hasBeenInterrupted;
    }

    public boolean isWorking() {
        return isWorking;
    }

    public int getSizeGrass() {
        return sizeGrass;
    }

    public void setSizeGrass(int sizeGrass) {
        this.sizeGrass = sizeGrass;
    }

}
