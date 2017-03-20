package model.simulation;
/**
 * Implements the original rules in Game of Life.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 */
public class DefaultRuleSet implements SimRule
{
    /**
     * Decides the state of a cell in the next generation, by following the default rule set.
     *
     * @param numberOfLivingNeighbors The amount of living neighbours of a cell.
     * @return one out of three results; Birth, death or unchanged.
     */
    public Result execute(int numberOfLivingNeighbors)
    {
        if(numberOfLivingNeighbors == 3)
            return Result.BIRTH;
        else if(numberOfLivingNeighbors != 2)
            return Result.DEATH;
        else
            return Result.UNCHANGED;
    }
}
