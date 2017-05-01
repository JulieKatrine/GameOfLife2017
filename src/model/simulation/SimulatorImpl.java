package model.simulation;

import model.GameBoard;
import model.Point;

/**
 * This simulator implementation executes a simulation on every cell in a given GameBoard.
 * It uses the GameBoards methods to access and update each cell according to a specific rule.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 * @deprecated This implementation is replaced by {@link SimulatorThreaded}
 * @see Simulator
 * @see GameBoard
 * @see SimRule
 */
public class SimulatorImpl extends Simulator
{
    public SimulatorImpl(SimRule rule)
    {
        super(rule);
    }

    /**
     * Executes the simulation on a defined board.
     *
     * Iterates through the whole board, gets the amount of living neighbours,
     * and checks and sets the results for the next generation, and makes next generation current.
     *
     * @param board The current board.
     */
    protected void executeOn(GameBoard board)
    {
        Point cellPos = new Point();
        for (cellPos.y = 0; cellPos.y < board.getHeight(); cellPos.y++)
        {
            for (cellPos.x = 0; cellPos.x < board.getWidth(); cellPos.x++)
            {
                int numberOfLivingNeighbors = board.getAmountOfLivingNeighbours(cellPos);

                SimRule.Result result = simulationRule.execute(numberOfLivingNeighbors);

                switch(result)
                {
                    case DEATH:
                        board.setStateInNextGeneration(false, cellPos);
                        break;

                    case BIRTH:
                        board.setStateInNextGeneration(true, cellPos);
                        break;

                    case SURVIVE:
                        board.setStateInNextGeneration(board.isCellAliveInThisGeneration(cellPos), cellPos);
                        break;
                }
            }
        }
        board.makeNextGenerationCurrent();
    }
}
