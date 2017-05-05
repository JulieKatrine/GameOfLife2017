package model.simulation;

/**
 * Interface used to enable multiple rule implementations for the {@link Simulator}.
 * The interface contains an enum of Results
 * and a final variable "DEFAULT_RULE_STRING" that is used throughout the application.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 * @see CustomRule
 * @see DefaultRule
 */
public interface SimulationRule
{
    String DEFAULT_RULE_STRING = "B3/S23";

    /**
     * The different states a SimulationRule can return after being executed on a cell.
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
