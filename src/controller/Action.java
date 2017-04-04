package controller;

/**
 * This interface is used for passing actions from one object to another.
 * @see UpdateTimer
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 */

public interface Action
{
    /**
     * The action to be carried out.
     */
    void call();
}
