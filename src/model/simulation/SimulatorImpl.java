package model.simulation;

import model.GameBoard;
import model.Point;

/**
 * Simulates the pattern according to the rules.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
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
    public void executeOn(GameBoard board)
    {
        Point cellPos = new Point();
        for (cellPos.y = 0; cellPos.y < board.getHeight(); cellPos.y++)
        {
            for (cellPos.x = 0; cellPos.x < board.getWidth(); cellPos.x++)
            {
                int numberOfLivingNeighbors = board.getAmountOfLivingNeighbours(cellPos);

                Result result = simulationRule.execute(numberOfLivingNeighbors);

                switch(result)
                {
                    case BIRTH:
                        board.setStateInNextGeneration(true, cellPos);
                        break;

                    case DEATH:
                        board.setStateInNextGeneration(false, cellPos);
                        break;

                    case UNCHANGED:
                        board.setStateInNextGeneration(board.isCellAliveInThisGeneration(cellPos), cellPos);
                        break;
                }
            }
        }
        board.makeNextGenerationCurrent();
    }
}
