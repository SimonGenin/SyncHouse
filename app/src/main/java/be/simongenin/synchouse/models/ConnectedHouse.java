package be.simongenin.synchouse.models;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ConnectedHouse {

    public Alarm alarm;
    public Mower mower;
    public Windows windows;
    public DomesticMachine washingMachine;
    public DomesticMachine dryer;
    public DomesticMachine dishWasher;

    private SharedPreferences prefs;

    public ConnectedHouse (Context context) {

        alarm = new Alarm();
        mower = new Mower();
        windows = new Windows();
        washingMachine = new DomesticMachine();
        dryer = new DomesticMachine();
        dishWasher = new DomesticMachine();

        prefs = PreferenceManager.getDefaultSharedPreferences(context);

    }

    public void saveState() {

        alarm.saveState(prefs);
        mower.saveState(prefs);
        windows.saveState(prefs);
        washingMachine.saveState(DomesticMachine.Type.WASHING_MACHINE, prefs);
        dryer.saveState(DomesticMachine.Type.DRYER, prefs);
        dishWasher.saveState(DomesticMachine.Type.DISH_WASHER, prefs);

    }

    public void retrieveState() {

        alarm.retrieveState(prefs);
        mower.retrieveState(prefs);
        windows.retrieveState(prefs);
        washingMachine.retrieveState(DomesticMachine.Type.WASHING_MACHINE, prefs);
        dryer.retrieveState(DomesticMachine.Type.DRYER, prefs);
        dishWasher.retrieveState(DomesticMachine.Type.DISH_WASHER, prefs);

    }

}
