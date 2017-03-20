package model.simulation;

/**
 * Holds the different rulesets.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 */
public interface SimRule
{
    Result execute(int numberOfLivingNeighbors);
}
