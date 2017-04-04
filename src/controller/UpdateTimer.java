package controller;

/**
 * This class is used for creating timed updates.
 * A separate thread runs independently from the JavaFX thread and calls
 * the Action interface at given intervals. The class enables faster updates
 * than JavaFxs AnimationTimer, which is limited to 60 frames per second.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 */
public class UpdateTimer
{
    private Object runLock;
    private Action action;
    private int delayInMilliseconds = 500;
    private long lastTime;
    private boolean running;

    public UpdateTimer()
    {
        this.runLock = new Object();
        createUpdateThread();
    }

    /**
     * Creates and starts a new thread.
     * This thread will call the actions call() method at given intervals while
     * the "running" flag is true. While being false, it makes the thread wait
     * for the runLock object to be notified by either setRunning() or triggerUpdate().
     */
    private void createUpdateThread()
    {
        new Thread(() ->
        {
            while(true)
            {
                if(!running)
                    waitToStart();

                if(System.currentTimeMillis() > lastTime + delayInMilliseconds)
                {
                    action.call();
                    lastTime = System.currentTimeMillis();
                }
            }

        }).start();
    }

    /**
     * This method will block until the runLock object gets notified.
     */
    private void waitToStart()
    {
        synchronized(runLock)
        {
            try
            {
                runLock.wait();
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param action The action to be performed at every update.
     */
    public void setOnUpdateAction(Action action)
    {
        this.action = action;
    }

    /**
     * @return True if the timer is running.
     */
    public boolean isRunning()
    {
        return running;
    }

    /**
     * Sets the delay between updates.
     * @param delayInMS Delay in milliseconds.
     */
    public void setDelayBetweenUpdates(int delayInMS)
    {
        delayInMilliseconds = delayInMS;
    }

    /**
     * Enables starting and stopping of the timer.
     * @param state The state of the timer.
     */
    public void setRunning(boolean state)
    {
        running = state;
        if(running)
        {
            synchronized(runLock)
            {
                runLock.notify();
            }
        }
    }

    /**
     * Triggers a single update.
     */
    public void triggerUpdate()
    {
        synchronized(runLock)
        {
            runLock.notify();
        }
    }
}