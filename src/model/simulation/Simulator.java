package model.simulation;

import model.GameBoard;

/**
 * This class is used to execute a simulation on a given {@link GameBoard}.
 * The simulation is carried out according to a specific {@link SimRule}.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 * @see SimulatorThreaded
 * @see SimulatorImpl
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
     * Simulates the next generation on the given board according to the set {@link SimRule}.
     * Takes the time of the simulation and
     * @param board The {@link GameBoard} to be used under the simulation.
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
        long now = System.currentTimeMillis();
        generationsPerSecond = (int)(++generationCount / ((now - generationCountTimer) / 1000.0));

        if(now > generationCountTimer + 1000)
        {
            generationCount = 0;
            generationCountTimer = now;
        }
    }

    /**
     * @return The amount of generations this simulator is simulating per second.
     */
    public int getGenerationsPerSecond()
    {
        return generationsPerSecond;
    }

    /**
     * @return The amount of time the last simulation used in milliseconds.
     */
    public long getSimulationTime()
    {
        return simulationTimeInMilliSeconds;
    }

    /**
     * Sets the new active simulation rule.
     * @param simRule A rule.
     */
    public void setRule(SimRule simRule)
    {
        simulationRule = simRule;
    }

    /**
     * @return The current active simulation rule.
     */
    public SimRule getSimulationRule()
    {
        return simulationRule;
    }
}
