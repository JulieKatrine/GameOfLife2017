package model.simulation;

import model.GameBoard;

/**
 * This class is used to execute a simulation on a given GameBoard.
 * The simulation is carried out according to a specific rule.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 * @see SimRule
 * @see GameBoard
 */
public abstract class Simulator
{
    protected SimRule simulationRule;
    private double simulationTimeInMilliSeconds;
    private long startTime;

    public Simulator(SimRule rule)
    {
        this.simulationRule = rule;
    }

    public void setRule(SimRule simRule)
    {
        simulationRule = simRule;
    }

    protected void startTimer()
    {
        startTime = System.nanoTime();
    }

    protected void stopTimer()
    {
        simulationTimeInMilliSeconds = (System.nanoTime() - startTime) / 1000000.0;
    }

    public double getTime()
    {
        return simulationTimeInMilliSeconds;
    }

    public abstract void executeOn(GameBoard board);
}
