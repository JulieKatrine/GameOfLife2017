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

    public Simulator(SimRule rule)
    {
        this.simulationRule = rule;
    }

    public abstract void executeOn(GameBoard board);
}
