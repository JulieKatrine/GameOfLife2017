package model.simulation;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

/**
 * Implements the original rules in Conway's Game of Life.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 */
public class DefaultRuleSet implements SimRule
{

    /**
     * Decides the state of a cell in the next generation according to the default rule set.
     *
     * @param numberOfLivingNeighbors The amount of living neighbours of a cell.
     * @return Result One out of three possible results: BIRTH, DEATH or UNCHANGED.
     */
    public Result execute(int numberOfLivingNeighbors)
    {
        if(numberOfLivingNeighbors == 3)
            return Result.BIRTH;
        else if(numberOfLivingNeighbors != 2)
            return Result.DEATH;
        else
            return Result.SURVIVE;
    }
}
