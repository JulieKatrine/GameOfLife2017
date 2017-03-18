package model.simulation;

import model.GameBoard;

public abstract class Simulator
{
    protected SimRule simulationRule;

    public Simulator(SimRule rule)
    {
        this.simulationRule = rule;
    }

    public abstract void executeOn(GameBoard board);
}
