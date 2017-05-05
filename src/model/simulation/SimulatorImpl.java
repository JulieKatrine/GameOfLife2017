package model.simulation;

import model.GameBoard;
import model.Point;

/**
 * This simulator implementation executes a simulation on every cell in a given {@link GameBoard}.
 * It uses the boards methods to access and update each cell according to a specific {@link SimulationRule}.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 * @deprecated This implementation is replaced by {@link ThreadedSimulatorImpl}
 * @see Simulator
 * @see SimulationRule
 */
public class SimulatorImpl extends Simulator
{
    public SimulatorImpl(SimulationRule rule)
    {
        super(rule);
    }

    /**
     * Executes the simulation on the given board.
     * Iterates through the whole board, gets the amount of living neighbours for each cell
     * and checks and sets the results for the next generation. It then makes the simulated
     * generation the current active one.
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

                switch(simulationRule.execute(numberOfLivingNeighbors))
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
