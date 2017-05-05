package model.simulation;

import model.GameBoard;

/**
 * This abstract class is used to execute a simulation on a given {@link GameBoard}.
 * The simulation is carried out according to a specific {@link SimulationRule}.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 * @see ThreadedSimulatorImpl
 * @see SimulatorImpl
 */
public abstract class Simulator
{
    protected SimulationRule simulationRule;
    private long simulationTimeInMilliSeconds;

    private int generationCount;
    private long generationCountTimer;
    private int generationsPerSecond;

    /**
     * @param rule The rule to be used for simulation.
     */
    public Simulator(SimulationRule rule)
    {
        this.simulationRule = rule;
    }

    /**
     * Simulates the next generation on the given board according to the set {@link SimulationRule}.
     * Measures the time of the simulation and calculates the amount of generations per second
     * by calling the private method calculateGenerationPerSecond().
     *
     * @param board The {@link GameBoard} to be used under the simulation.
     */
    public void simulateNextGenerationOn(GameBoard board)
    {
        long startTime = System.currentTimeMillis();

        executeOn(board);

        simulationTimeInMilliSeconds = (System.currentTimeMillis() - startTime);

        calculateGenerationPerSecond();
    }

    protected abstract void executeOn(GameBoard board);

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
     * @param simulationRule A rule.
     */
    public void setRule(SimulationRule simulationRule)
    {
        this.simulationRule = simulationRule;
    }

    /**
     * @return The current active simulation rule.
     */
    public SimulationRule getSimulationRule()
    {
        return simulationRule;
    }
}
