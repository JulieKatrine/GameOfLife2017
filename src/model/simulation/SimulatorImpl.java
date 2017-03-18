package model.simulation;

import model.GameBoard;
import model.Point;

public class SimulatorImpl extends Simulator
{
    public SimulatorImpl(SimRule rule)
    {
        super(rule);
    }

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
