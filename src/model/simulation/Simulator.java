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

    /**
     * @param rule The rule to be used under simulation.
     */
    public Simulator(SimRule rule)
    {
        this.simulationRule = rule;
    }

    /**
     * @param simRule The rule to be used under simulation
     */
    public void setRule(SimRule simRule)
    {
        simulationRule = simRule;
    }

    /**
     * Sets the starting point for the timer.
     * It is used by the simulator implementations to acquire a simulation time.
     */
    protected void startTimer()
    {
        startTime = System.nanoTime();
    }

    /**
     * Calculates the elapsed time since startTimer() was called.
     * It is used by the simulator implementations to acquire a simulation time.
     */
    protected void stopTimer()
    {
        simulationTimeInMilliSeconds = (System.nanoTime() - startTime) / 1000000.0;
    }

    /**
     * Returns the total time of the last executed simulation.
     * @return Time in milliseconds
     */
    public double getTime()
    {
        return simulationTimeInMilliSeconds;
    }

    /**
     * Executes a simulation on the given board according to the set rule.
     * @param board The GameBoard to be used under the simulation.
     */
    public abstract void executeOn(GameBoard board);
}
