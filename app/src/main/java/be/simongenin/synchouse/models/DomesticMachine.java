package be.simongenin.synchouse.models;


public class DomesticMachine {

    public enum problem { ELECTRICITY, WATER, NONE };

    protected problem currentProblem;



    public DomesticMachine() {

        currentProblem = problem.NONE;

    }

}
