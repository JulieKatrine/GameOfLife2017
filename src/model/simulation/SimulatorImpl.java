package model.simulation;

import model.GameBoard;

import java.util.ArrayList;
import java.util.List;

public class SimulatorImpl implements Simulator
{
    private List<SimRule> simulationRules;

    public SimulatorImpl()
    {
        simulationRules = new ArrayList<SimRule>();
    }

    @Override
    public void addSimulationRules(SimRule... rules)
    {
        for (SimRule i : rules)
        simulationRules.add(i);
    }

    public void executeOn(GameBoard board)
    {

    }
}
