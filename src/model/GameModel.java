package model;

import model.simulation.*;

/**
 * This class packs the applications main game logic and data into one neat object.
 * It supplies getters and setter for the GameBoard and Simulator objects, as well
 * as a simulateNextGeneration() method to carry out the logic on the data.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 * @see Simulator
 * @see GameBoard
 */

public class GameModel
{
    private GameBoard gameBoard;
    private Simulator simulator;

    /**
     * The constructor sets up a default GameBoard with size (50, 50) and a default
     * Simulator with the DefaultRuleSet.
     */
    public GameModel()
    {
        gameBoard = new GameBoardDynamic(GameBoard.DEFAULT_BOARD_WIDTH, GameBoard.DEFAULT_BOARD_HEIGHT);
        simulator = new ThreadedSimulator(new DefaultRuleSet());
    }

    public void simulateNextGeneration()
    {
        simulator.executeOn(getGameBoard());
    }

    public GameBoard getGameBoard()
    {
        return gameBoard;
    }

    public void setGameBoard(GameBoard board)
    {
        this.gameBoard = board;
    }

    public void setRule(SimRule simulatorRule)
    {
        simulator.setRule(simulatorRule);
    }
}