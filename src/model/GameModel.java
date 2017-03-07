package model;

import model.simulation.DefaultRuleSet;
import model.simulation.Simulator;
import model.simulation.SimulatorImpl;

import java.io.File;
import java.io.IOException;

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

    public void loadGameBoardFromDisk(File file) throws IOException, FileNotSupportedException
    {
        gameBoard = boardLoader.loadFromDisk(file);
    }

    public void loadGameBoardFromURL(String url) throws IOException, FileNotSupportedException
    {
        gameBoard = boardLoader.loadFromURL(url);
    }
}
