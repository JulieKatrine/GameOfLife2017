package model;

import model.BoardIO.PatternLoader;
import model.BoardIO.FileNotSupportedException;
import model.simulation.DefaultRuleSet;
import model.simulation.Simulator;
import model.simulation.SimulatorImpl;

import java.io.File;
import java.io.IOException;

public class GameModel
{
    private PatternLoader boardLoader;
    private GameBoard gameBoard;
    private Simulator simulator;

    public GameModel()
    {
        boardLoader = new PatternLoader();
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

    public void setGameBoard(GameBoard board)
    {
        gameBoard = board;
    }
}
