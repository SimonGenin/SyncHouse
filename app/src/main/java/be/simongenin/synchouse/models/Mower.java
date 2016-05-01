package be.simongenin.synchouse.models;

import android.content.SharedPreferences;

public class Mower {

    private int sizeGrass;
    private boolean isWorking;


    public Mower() {

        sizeGrass = 2;

    }

    /**
     * Start if mower finished properly it's 4h
     * check if can start first, runtime exception
     */
    public void start() {

       isWorking = true;

    }

    /**
     * Stop the mower.
     * No matter what.
     */
    public void stop() {

        isWorking = false;

    }

    public int getSizeGrass() {
        return sizeGrass;
    }

    public void setSizeGrass(int sizeGrass) {
        this.sizeGrass = sizeGrass;
    }

    public boolean isWorking() {
        return isWorking;
    }

    public void setWorking(boolean working) {
        isWorking = working;
    }

    public void saveState(SharedPreferences preferences) {

        preferences.edit().putBoolean("mower_is_working", isWorking).apply();
        preferences.edit().putInt("size_grass", sizeGrass).apply();

    }

    public void retrieveState(SharedPreferences preferences) {

        isWorking = preferences.getBoolean("mower_is_working", false);
        sizeGrass = preferences.getInt("size_grass", 1);

    }

}
