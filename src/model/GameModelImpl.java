package model;

import model.simulation.DefaultRuleSet;
import model.simulation.Simulator;
import model.simulation.SimulatorImpl;

public class GameModelImpl implements GameModel
{
    private GameBoard gameBoard;
    private Simulator simulator;

    public GameModelImpl()
    {
        //Implements DefaultRuleSet with possibilities to change or add new rules later in the project.
    SimulatorImpl simulator = new SimulatorImpl();
        DefaultRuleSet defaultRuleSet = new DefaultRuleSet();
    simulator.addSimulationRules(defaultRuleSet);
    }

    @Override
    public void simulateNextGeneration()
    {
        simulator.executeOn(gameBoard);
    }

    @Override
    public void setGameBoard(GameBoard board)
    {
    gameBoard = board;
    }

    @Override
    public GameBoard getGameBoard()
    {
        return gameBoard;
    }

}
