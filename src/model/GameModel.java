package model;

import model.simulation.DefaultRuleSet;
import model.simulation.Simulator;
import model.simulation.SimulatorImpl;

public class GameModel
{
    private GameBoard gameBoard;
    private Simulator simulator;

    public GameModel()
    {
        gameBoard = new GameBoardImpl(50, 50);
        simulator = new SimulatorImpl(new DefaultRuleSet());
    }

    public void simulateNextGeneration()
    {
        simulator.executeOn(gameBoard);
    }

    public GameBoard getGameBoard()
    {
        return gameBoard;
    }

    public void setGameBoard(GameBoard board)
    {
        this.gameBoard = board;
    }

    public void setSimulator(Simulator simulator)
    {
        this.simulator = simulator;
    }
}
