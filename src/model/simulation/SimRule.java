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
    Result execute(int numberOfLivingNeighbors);
}
