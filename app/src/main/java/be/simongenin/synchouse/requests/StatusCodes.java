package be.simongenin.synchouse.requests;


public final class StatusCodes {

    /**
     * Alarm
     */
    public final static int ALARM_TOTAL_START = 10;
    public final static int ALARM_PARTIAL_START = 11;
    public final static int ALARM_STOP = 12;
    public final static int ALARM_RING_START = 13;
    public final static int ALARM_RING_STOP = 14;
    public final static int ALARM_PROBLEM = 15;

    /**
     * Mower
     */
    public final static int MOWER_START = 20;
    public final static int MOWER_STOP = 21;
    public final static int MOWER_PROBLEM = 22;

    /**
     * Windows
     */
    public final static int SHUTTERS_OPEN = 30;
    public final static int SHUTTERS_CLOSE = 31;
    public final static int WINDOWS_OPEN = 32;
    public final static int WINDOWS_CLOSE = 33;
    public final static int SHUTTERS_PROBLEM = 34;
    public final static int WINDOWS_PROBLEM = 35;

    /**
     * Dryer
     */
    public final static int DRYER_PROGRAM = 40;
    public final static int DRYER_START = 41;
    public final static int DRYER_STOP = 42;
    public final static int DRYER_WATER_PROBLEM = 43;
    public final static int DRYER_ELECTRICAL_PROBLEM = 44;

    /**
     * Washing machine
     */
    public final static int WASHING_MACHINE_PROGRAM = 50;
    public final static int WASHING_MACHINE_START = 51;
    public final static int WASHING_MACHINE_STOP = 52;
    public final static int WASHING_MACHINE_WATER_PROBLEM = 53;
    public final static int WASHING_MACHINE_ELECTRICAL_PROBLEM = 54;

    /**
     * Dish washer
     */
    public final static int DISH_WASHER_PROGRAM = 60;
    public final static int DISH_WASHER_START = 61;
    public final static int DISH_WASHER_STOP = 62;
    public final static int DISH_WASHER_WATER_PROBLEM = 63;
    public final static int DISH_WASHER_ELECTRICAL_PROBLEM = 64;

}
