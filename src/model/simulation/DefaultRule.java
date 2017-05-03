package model.simulation;

/**
 * Implements the original rule from Conway's Game of Life.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 * @see SimulationRule
 * @see Result
 * @see Simulator
 */
public class DefaultRule implements SimulationRule
{
    public Result execute(int numberOfLivingNeighbors)
    {
        if(numberOfLivingNeighbors == 3)
            return Result.BIRTH;
        else if(numberOfLivingNeighbors != 2)
            return Result.DEATH;
        else
            return Result.SURVIVE;
    }

    @Override
    public String getStringRule()
    {
        return "B3/S23";
    }
}
