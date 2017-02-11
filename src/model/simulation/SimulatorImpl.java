package model.simulation;

import model.GameBoard;
import model.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimulatorImpl implements Simulator
{
    private List<SimRule> simulationRules;

    public SimulatorImpl()
    {
        simulationRules = new ArrayList<>();
    }

    @Override
    public void addSimulationRules(SimRule... rules)
    {
        simulationRules.addAll(Arrays.asList(rules));
    }

    public void executeOn(GameBoard board)
    {
        Point cellPos = new Point();
        for (cellPos.y = 0; cellPos.y < board.getHeight(); cellPos.y++)
        {
            for (cellPos.x = 0; cellPos.x < board.getWidth(); cellPos.x++)
            {
                int numberOfNeighbors = board.getAmountOfLivingNeighbors(cellPos);

                Result result = Result.UNCHANGED;

                for (SimRule rule : simulationRules)
                {
                    result = rule.execute(numberOfNeighbors);
                    if(result != Result.UNCHANGED)
                        break;
                }

                handleCellResult(board, result, cellPos);
            }
        }
        board.makeNextGenerationCurrent();
    }

    private void handleCellResult(GameBoard board, Result result, Point cellPos)
    {
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
