package controller;

import javafx.animation.AnimationTimer;

public class UpdateTimer extends AnimationTimer
{
    private UpdatableObject updatableObj;
    private int delayInMilliseconds = 500;
    private long lastTime;
    private boolean running = false;

    public UpdateTimer(UpdatableObject obj)
    {
        updatableObj = obj;
    }

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