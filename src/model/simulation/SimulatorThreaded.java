package model.simulation;

import model.GameBoard;
import model.Point;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * <p>This implementation uses multithreading to carry out a simulation on a given GameBoard.
 * It uses the GameBoards methods to access and update each cell according to a specific rule.
 * The implementation creates a fixed pool of threads to be used for simulating different regions of
 * a GameBoard simultaneously. The amount of threads to be used is determined by the boards size
 * and the amount of available cores.
 *
 * <p>Every thread is given a horizontal region of the GameBoard to process. The amount of rows for each
 * thread is determined by the calculated thread count. Since the underlying data structure is stored
 * horizontally in memory, we heavily reduce the amount of cache misses by making each thread work on a set
 * of rows, instead of a set of columns.
 *
 * <p>The simulateNextGenerationOn() method uses a CountDownLatches to make sure all threads are done processing before
 * the generation is finalized with GameBoards makeNextGenerationCurrent(). Another latch is used to make
 * sure no threads are accessing the same data at the same time. Synchronization problems will only occur if a
 * slow running thread is accessing data at its to first rows, and a faster thread updates its last rows in
 * the adjacent region. This is solved by blocking all further processing until all threads are past their
 * second row and not allowing any threads to have fewer rows than 4. Boards with a smaller height than 8
 * will only utilize one thread.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 * @see Simulator
 * @see GameBoard
 * @see SimRule
 * @see Result
 */

public class SimulatorThreaded extends Simulator
{
    private ExecutorService executorService;
    private CountDownLatch simulationExecutedLatch;
    private CountDownLatch synchLatch;
    private int availableProcessors;

    /**
     * The constructor takes in a SimRule and sets up the simulator.
     * It retrieves the amount of available cores (doubled if CPU is hyperthreaded)
     * and creates a fixed thread pool with this amount. It also adds a shutdown hook
     * for the ExecutorService to be shut down gracefully on application exit.
     *
     * @param rule The rule to be used under simulation.
     */
    public SimulatorThreaded(SimRule rule)
    {
        super(rule);
        this.availableProcessors = Runtime.getRuntime().availableProcessors();
        this.executorService = Executors.newFixedThreadPool(availableProcessors);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdownExecutor));
    }


    /**
     * This method simulates a single generation on the given board.
     * It creates the appropriate amount of Workers, submits them to the
     * ExecutorService and waits for all threads to finish processing.
     * @param board The board to be used for simulation.
     */
    @Override
    protected void executeOn(GameBoard board)
    {
        int numberOfThreads = Math.max(1, Math.min(availableProcessors, (board.getHeight() / 4)));
        int rowsPerThread = board.getHeight() / numberOfThreads;

        simulationExecutedLatch = new CountDownLatch(numberOfThreads);
        synchLatch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++)
        {
            int additionalRows = 0;
            if(i == numberOfThreads - 1)
                additionalRows = board.getHeight() % numberOfThreads;

            Worker worker = new Worker(board,i * rowsPerThread, rowsPerThread + additionalRows);
            executorService.execute(worker);
        }

        try
        {
            // Wait until all threads are finished before proceeding.
            simulationExecutedLatch.await();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        board.makeNextGenerationCurrent();
    }

    /**
     * Shuts down the executor service.
     */
    private void shutdownExecutor()
    {
        executorService.shutdown();
        try
        {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS))
                executorService.shutdownNow();
        }
        catch (InterruptedException e)
        {
            executorService.shutdownNow();
        }
    }

    /**
     * This private class is used for simulating a region of a GameBoard.
     * It takes in a GameBoard object and parameters for the region to be processed.
     */
    public class Worker implements Runnable
    {
        private GameBoard board;
        private int numberOfRows;
        private int rowIndexStart;

        /**
         * @param board The board to be used for simulation.
         * @param rowIndexStart The starting row index.
         * @param numberOfRows The amount of rows to be processed from the given index.
         */
        public Worker(GameBoard board, int rowIndexStart, int numberOfRows)
        {
            this.board = board;
            this.numberOfRows = numberOfRows;
            this.rowIndexStart = rowIndexStart;
        }

        /**
         * Runs the simulation and counts the latch down.
         */
        @Override
        public void run()
        {
            simulate();
            simulationExecutedLatch.countDown();
        }

        /**
         * This method iterates over and updates all the cells in its region.
         * It waits until all threads are done simulating their first two rows
         * before proceeding. This is done to prevent synchronization issues, and
         * works as a faster alternative to either using synchronized method calls or
         * atomic data wrappers.
         */
        private void simulate()
        {
            Point cellPos = new Point();
            for (cellPos.y = rowIndexStart; cellPos.y < rowIndexStart + numberOfRows; cellPos.y++)
            {
                for (cellPos.x = 0; cellPos.x < board.getWidth(); cellPos.x++)
                {
                    int numberOfLivingNeighbors = board.getAmountOfLivingNeighbours(cellPos);

                    Result result = simulationRule.execute(numberOfLivingNeighbors);

                    switch(result)
                    {
                        case DEATH:
                            board.setStateInNextGeneration(false, cellPos);
                            break;

                        case BIRTH:
                            board.setStateInNextGeneration(true, cellPos);
                            break;

                        case SURVIVE:
                            board.setStateInNextGeneration(board.isCellAliveInThisGeneration(cellPos), cellPos);
                            break;
                    }
                }

                // Done with the second row, wait for the other threads.
                if(cellPos.y == rowIndexStart + 1)
                {
                    try
                    {
                        synchLatch.countDown();
                        synchLatch.await();
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
