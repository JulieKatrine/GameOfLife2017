package model;

import model.simulation.DefaultRuleSet;
import model.simulation.Simulator;
import model.simulation.SimulatorImpl;

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
        gameBoard = new GameBoardDynamic(50, 50);
        simulator = new SimulatorImpl(new DefaultRuleSet());
    }

    /**
     * Simulates the next generation on the set GameBoard with set Simulator.
     */
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