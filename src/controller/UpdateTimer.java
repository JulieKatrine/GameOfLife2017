package controller;

import javafx.animation.AnimationTimer;

public class UpdateTimer extends AnimationTimer
{
    private UpdatableObject updatebleObj;
    private int delayInMilliseconds = 1000;
    private long time;

    public UpdateTimer(UpdatableObject obj)
    {
        updatebleObj = obj;
    }

    @Override
    public void handle(long now)
    {

    }

    public void setDelayBetweenUpdates(int delayInMS)
    {

    }

}