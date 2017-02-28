package model;

import model.simulation.DefaultRuleSet;
import model.simulation.Simulator;
import model.simulation.SimulatorImpl;

public class GameModel
{
    private BoardLoader boardLoader;
    private GameBoard gameBoard;
    private Simulator simulator;

    public GameModel()
    {
        boardLoader = new BoardLoader();
        gameBoard = boardLoader.newEmptyBoard(50,50);
        simulator = new SimulatorImpl();

        //Implements DefaultRuleSet with possibilities to change or add new rules later in the project.
        simulator.addSimulationRules(new DefaultRuleSet());
    }

    public void simulateNextGeneration()
    {
        simulator.executeOn(gameBoard);
    }

    public GameBoard getGameBoard()
    {
        return gameBoard;
    }

    public void loadNewRandomBoard(int width, int height)
    {
        gameBoard = boardLoader.newRandomBoard(width, height);
    }
}
