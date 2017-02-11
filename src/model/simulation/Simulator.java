package model.simulation;

import model.GameBoard;

public interface Simulator
{
    void addSimulationRules(SimRule ... rules);
    void executeOn(GameBoard board);
}
