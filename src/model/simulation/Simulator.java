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
     * Simulates the next generation on the given board according to the set rule.
     * Takes the time of the simulation and
     * @param board The GameBoard to be used under the simulation.
     */
    public void simulateNextGenerationOn(GameBoard board)
    {
        long startTime = System.nanoTime();

        executeOn(board);

        simulationTimeInMilliSeconds = (System.nanoTime() - startTime) / 1000000.0;

        calculateGenerationPerSecond();
    }

    protected abstract void executeOn(GameBoard board);

    private void calculateGenerationPerSecond()
    {
        generationCount++;

        long now = System.currentTimeMillis();
        if(now > generationCountTimer + 1000)
        {
            generationsPerSecond = (int)(generationCount * ((now - generationCountTimer) / 1000.0));
            generationCount = 0;
            generationCountTimer = now;
        }
    }

    public int getGenerationsPerSecond()
    {
        return generationsPerSecond;
    }

    public double getSimulationTime()
    {
        return simulationTimeInMilliSeconds;
    }

    public void setRule(SimRule simRule)
    {
        simulationRule = simRule;
    }

    public SimRule getSimulationRule()
    {
        return simulationRule;
    }
}
