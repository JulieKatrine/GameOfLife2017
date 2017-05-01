package model.simulation;


/**
 * This interface is used to enable multiple rule implementations for the Simulator.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 * @see Simulator
 * @see Result
 */
public interface SimRule
{
    // This enum holds the different states a SimRule can return after being executed on a cell.
    enum Result
    {
        BIRTH,
        DEATH,
        SURVIVE
    }

    Result execute(int numberOfLivingNeighbors);
    String getStringRule();
}
