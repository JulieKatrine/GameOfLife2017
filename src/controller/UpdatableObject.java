package controller;

/**
 * TODO: Is this interface neccesary? it is only implemented by the Controller, and used once in UpdateTimer.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 */
public interface UpdatableObject
{
    void triggerControllerUpdate();
}
