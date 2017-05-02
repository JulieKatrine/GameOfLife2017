package model.simulation;

/**
 * Interface used to enable multiple rule implementations for the {@link Simulator}.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 * @see CustomRule
 * @see DefaultRuleSet
 */
public interface SimRule
{
    /**
     * The different states a SimRule can return after being executed on a cell.
     */
    enum Result
    {
        BIRTH,
        DEATH,
        SURVIVE
    }

    /**
     * Returns a {@link Result} dependant on the amount of living neighbour cells.
     * @param numberOfLivingNeighbors The amount of living neighbours.
     * @return A {@link Result}.
     */
    Result execute(int numberOfLivingNeighbors);

    /**
     * Returns a standard formatted string representing this rule.
     * @return A rule string.
     */
    String getStringRule();
}
