package be.simongenin.synchouse.models;

public class ModelTester {

    public static void main(String[] args) {

        Mower mower = new Mower();
        mower.setSizeGrass(15);
        mower.start();

        mower.interrupt();
        mower.restart();

    }

}
