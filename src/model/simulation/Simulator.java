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

    private int generationCount;
    private long generationCountTimer;
    private int generationsPerSecond;

    /**
     * @param rule The rule to be used under simulation.
     */
    public Simulator(SimRule rule)
    {
        this.simulationRule = rule;
    }


    /**
     * Sets the starting point for the generationCountTimer.
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

    protected void increaseGenerationCount(){
        generationCount++;

        if(System.currentTimeMillis() > generationCountTimer + 1000)
        {
            generationsPerSecond = generationCount;
            generationCount = 0;
            generationCountTimer = System.currentTimeMillis();
        }
    }

    public int getGenerationsPerSecond()
    {
        return generationsPerSecond;
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
     * @param simRule The rule to be used under simulation
     */
    public void setRule(SimRule simRule)
    {
        simulationRule = simRule;
    }

    public SimRule getSimulationRule()
    {
        return simulationRule;
    }

    /**
     * Executes a simulation on the given board according to the set rule.
     * @param board The GameBoard to be used under the simulation.
     */
    public abstract void executeOn(GameBoard board);
}
