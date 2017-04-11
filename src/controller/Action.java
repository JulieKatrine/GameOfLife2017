package controller;

/**
 * This interface is used for passing actions from one object to another.
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 * @see UpdateTimer
 */
@FunctionalInterface
public interface Action
{
    /**
     * The action to be carried out.
     */
    void call();
}
