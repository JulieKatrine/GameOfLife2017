package model;

import model.simulation.*;

/**
 * This class contains the applications main game logic and data.
 * It supplies getters and setter for the {@link GameBoard} and {@link Simulator} objects, as well
 * as a simulateNextGeneration() method to carry out the logic on the data.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 */

public class GameModel
{
    private GameBoard gameBoard;
    private Simulator simulator;

    /**
     * The constructor sets up a default GameBoard and Simulator with the DefaultRule.
     */
    public GameModel()
    {
        gameBoard = new GameBoardDynamic(GameBoard.DEFAULT_BOARD_WIDTH, GameBoard.DEFAULT_BOARD_HEIGHT);
        simulator = new ThreadedSimulatorImpl(new DefaultRule());
    }

    /**
     * Simulates the next generation on the set board with the set rule.
     * */
    public void simulateNextGeneration()
    {
        simulator.simulateNextGenerationOn(getGameBoard());
    }

    public GameBoard getGameBoard()
    {
        return gameBoard;
    }
    
    public Simulator getSimulator()
    {
        return simulator;
    }

    public void setGameBoard(GameBoard board)
    {
        this.gameBoard = board;
    }
}