package controller;

import javafx.animation.AnimationTimer;

public class UpdateTimer extends AnimationTimer
{
    private UpdatableObject updatebleObj;
    private int delayInMilliseconds = 500;
    private long lastTime;

    public UpdateTimer(UpdatableObject obj)
    {
        updatebleObj = obj;
    }

    @Override
    public void handle(long nowInNanoSeconds)
    {
        long nowInMilliseconds = (nowInNanoSeconds/1000000);
        if (nowInMilliseconds > (lastTime + (delayInMilliseconds) ) )
        {
            updatebleObj.triggerUpdate();
            lastTime = nowInMilliseconds;
        }
    }

    public void setDelayBetweenUpdates(int delayInMS)
    {

    }

}