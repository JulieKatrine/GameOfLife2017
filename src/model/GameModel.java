package model;

import javafx.application.Platform;
import model.simulation.DefaultRuleSet;
import model.simulation.SimRule;
import model.simulation.Simulator;
import model.simulation.ThreadedSimulator;

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
    private Action action;
    private Object runLock;
    private int delayInMilliseconds = 500;
    private long simulationTimer;
    private boolean running;
    private long notifyControllerTimer;

    /**
     * The constructor sets up a default GameBoard with size (50, 50) and a default
     * Simulator with the DefaultRuleSet.
     */
    public GameModel()
    {
        gameBoard = new GameBoardDynamic(GameBoard.DEFAULT_BOARD_WIDTH, GameBoard.DEFAULT_BOARD_HEIGHT);
        simulator = new ThreadedSimulator(new DefaultRuleSet());
        runLock = new Object();
        createSimulationThread();
    }

    private void createSimulationThread()
    {
        new Thread(() ->
        {
            while(true)
            {
                if(!running)
                    waitToStart();

                if(System.currentTimeMillis() > simulationTimer + delayInMilliseconds)
                {
                    simulator.executeOn(gameBoard);
                    notifyController();
                    simulationTimer = System.currentTimeMillis();
                }
            }

        }).start();
    }

    private void waitToStart()
    {
        synchronized (runLock)
        {
            try
            {
                runLock.wait();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void notifyController()
    {
        if(System.currentTimeMillis() > notifyControllerTimer + 16)
        {
            Platform.runLater(() -> action.call());
            notifyControllerTimer = System.currentTimeMillis();
        }
    }

    /**
     * A setter for changing how often the board should be updated.
     *
     * @param delayInMS The defined delay in milliseconds.
     */
    public void setDelayBetweenUpdates(int delayInMS)
    {
        delayInMilliseconds = delayInMS;
    }

    public boolean isRunning()
    {
        return running;
    }

    public void setRunning(boolean state)
    {
        running = state;
        if(running)
        {
            synchronized (runLock)
            {
                runLock.notify();
            }
        }
    }

    /**
     * Simulates the next generation on the set GameBoard with set Simulator.
     */
    public void simulateNextGeneration()
    {
        synchronized (runLock)
        {
            runLock.notify();
        }
    }

    public GameBoard getGameBoard()
    {
        return gameBoard;
    }

    public void setGameBoard(GameBoard board)
    {
        this.gameBoard = board;
    }

    public void setOnSimulationDone(Action action)
    {
        this.action = action;
    }

    public void setRule(SimRule simulatorRule)
    {
        simulator.setRule(simulatorRule);
    }
}