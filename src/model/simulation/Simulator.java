package model.simulation;

import model.GameBoard;

/**
 * This class is used to execute a simulation on a given GameBoard.
 * The simulation is carried out according to a specific rule.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 * @see SimulatorThreaded
 * @see SimulatorImpl
 * @see SimRule
 * @see GameBoard
 */
public abstract class Simulator
{
    protected SimRule simulationRule;
    private long simulationTimeInMilliSeconds;

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

    protected abstract void executeOn(GameBoard board);

    /**
     * Simulates the next generation on the given board according to the set rule.
     * Takes the time of the simulation and
     * @param board The GameBoard to be used under the simulation.
     */
    public void simulateNextGenerationOn(GameBoard board)
    {
        long startTime = System.currentTimeMillis();

        executeOn(board);

        simulationTimeInMilliSeconds = (System.currentTimeMillis() - startTime);

        calculateGenerationPerSecond();
    }

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

    public long getSimulationTime()
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
