package model.simulation;

import com.sun.org.apache.regexp.internal.RE;
import model.GameBoard;
import model.Point;

import java.util.ArrayList;
import java.util.List;

public class SimulatorImpl implements Simulator
{
    private List<SimRule> simulationRules;

    public SimulatorImpl()
    {
        simulationRules = new ArrayList<SimRule>();
    }

    @Override
    public void addSimulationRules(SimRule... rules)
    {
        for (SimRule i : rules)
        simulationRules.add(i);
    }

    public void executeOn(GameBoard board)
    {
        for (int y = 0; y < board.getHeight(); y++)
        {
            for (int x = 0; x < board.getWidth(); x++)
            {
                Point thisPoint = new Point(x, y);
                int count = board.getAmountOfLivingNeighbors(thisPoint);

                for (SimRule rule : simulationRules)
                {
                    Result result = rule.execute(count);

                    switch (result) {
                        case BIRTH:
                            board.setStateInNextGeneration(true, thisPoint);
                            break;
                        case DEATH:
                            board.setStateInNextGeneration(false, thisPoint);
                            break;
                        case UNCHANGED:
                            board.setStateInNextGeneration(board.isCellAliveInThisGeneration(thisPoint), thisPoint);
                            break;
                    }

                }
            }
        }
        board.makeNextGenerationCurrent();
    }
}
