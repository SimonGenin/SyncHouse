package be.simongenin.synchouse.models;


import java.util.Calendar;

import be.simongenin.synchouse.exceptions.ForbiddenHoursException;

public class DomesticMachine {

    private boolean isPromoDay;

    public enum problem { ELECTRICITY, WATER, ELECTRICITY_AND_WATER , NONE };

    protected problem currentProblem;

    protected final int allowedStartHour = 22 * 60;
    protected final int allowedEndHour = 6 * 60;

    protected boolean isWorking;
    protected int startTime;

    public DomesticMachine() {

        currentProblem =  problem.NONE;

    }

    public void start() {

        int hourOfDay = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minutesOfHour = Calendar.getInstance().get(Calendar.MINUTE);

        startTime = hourOfDay * 60 + minutesOfHour;

        if ( startTime >= allowedStartHour || startTime < allowedEndHour || isPromoDay ) {

            isWorking = true;

        } else {

            throw new ForbiddenHoursException();

        }

    }

    public void stop() {

        isWorking = false;

    }


}
