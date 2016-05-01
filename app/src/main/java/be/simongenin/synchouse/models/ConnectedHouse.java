package be.simongenin.synchouse.models;


public class ConnectedHouse {

    private Alarm alarm;
    private Mower mower;
    private Windows windows;
    private DomesticMachine washingMachine;
    private DomesticMachine dryer;
    private DomesticMachine dishWasher;

    public ConnectedHouse () {

        alarm = new Alarm();
        mower = new Mower();
        windows = new Windows();
        washingMachine = new DomesticMachine();
        dryer = new DomesticMachine();
        dishWasher = new DomesticMachine();

    }

    public void saveState() {

    }

    public void retrieveState() {

    }

}
