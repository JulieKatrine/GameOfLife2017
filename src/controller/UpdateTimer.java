package controller;

import javafx.animation.AnimationTimer;

/**
 * Calculates and decides when to update of the board.
 *
 * The class extends AnimationTimer.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 */
public class UpdateTimer extends AnimationTimer
{
    private UpdatableObject updatableObj;
    private int delayInMilliseconds = 500;
    private long lastTime;
    private boolean running = false;

    /**
     * The constructor that takes in an UpdatableObject.
     *
     * TODO: Fill me in
     * @param obj fill me in
     */
    public UpdateTimer(UpdatableObject obj)
    {
        updatableObj = obj;
    }

    /**
     * Updates the board after a defined amount of milliseconds.
     *
     * If enough time (delayinMilliseconds) has pasted since the last update, or the start -
     * this method calls updatableObj.triggerControllerUpdate(); and updates the time.
     *
     * @param nowInNanoSeconds This exact time in nanoseconds.
     */
    @Override
    public void handle(long nowInNanoSeconds)
    {
        long nowInMilliseconds = (nowInNanoSeconds / 1000000);
        if (nowInMilliseconds > (lastTime + delayInMilliseconds))
        {
            updatableObj.triggerControllerUpdate();
            lastTime = nowInMilliseconds;
        }
    }

    /**
     * A setter for changing how often the board should be updated.
     *
     * @param delayInMS The defined delay in milliseconds.
     */
    public void setDelayBetweenUpdates(int delayInMS)
    {
        delayInMilliseconds = delayInMS;
    }

    public boolean isRunning()
    {
        return running;
    }

    public void setRunning(boolean state)
    {
        running = state;
        if(running)
            start();
        else
            stop();
    }
}