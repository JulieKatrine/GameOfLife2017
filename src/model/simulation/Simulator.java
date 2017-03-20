package model.simulation;

import model.GameBoard;

/**
 * Simulates the pattern according to the rules.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 */
public abstract class Simulator
{
    protected SimRule simulationRule;

    public Simulator(SimRule rule)
    {
        this.simulationRule = rule;
    }

    public abstract void executeOn(GameBoard board);
}
