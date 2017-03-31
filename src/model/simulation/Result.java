package model.simulation;

/**
 * This enum holds the different states a SimRule can return after being executed on a cell.
 * 
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 * @see SimRule
 * @see Simulator
 */

public enum Result
{
    BIRTH,
    DEATH,
    SURVIVE
}
