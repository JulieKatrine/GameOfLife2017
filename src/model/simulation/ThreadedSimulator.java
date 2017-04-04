package model.simulation;

import model.GameBoard;
import model.Point;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by julie on 04-Apr-17.
 */
public class ThreadedSimulator extends Simulator
{
    private ExecutorService executorService;
    private int availableProcessors;
    private CountDownLatch simulationExecutedLatch;
    private CountDownLatch pastSafetyRowForSynchronization;

    public ThreadedSimulator(SimRule simRule)
    {
        super(simRule);
        availableProcessors = Runtime.getRuntime().availableProcessors();
        executorService = Executors.newFixedThreadPool(availableProcessors);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> executorService.shutdown()));
    }

    @Override
    public void executeOn(GameBoard board)
    {
        startTimer();
        int numberOfThreads = Math.max(1, Math.min(availableProcessors, (board.getHeight()/4)));
        int rowsPerThread = board.getHeight()/numberOfThreads;
        simulationExecutedLatch = new CountDownLatch(numberOfThreads);
        pastSafetyRowForSynchronization = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++)
        {
            int additionalRows = 0;
            if(i == numberOfThreads -1)
                additionalRows = board.getHeight() % numberOfThreads;

            Worker worker = new Worker(board, i*rowsPerThread, rowsPerThread+additionalRows);
            executorService.execute(worker);
        }

        try {
            simulationExecutedLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        board.makeNextGenerationCurrent();
        stopTimer();
    }

    /*----------NEW CLASS ---------------*/

    public class Worker implements Runnable
    {
        private GameBoard board;
        private int rowIndexStart;
        private int numberOfRows;

        public Worker(GameBoard board, int rowIndexStart, int numberOfRows)
        {
            this.board = board;
            this.numberOfRows = numberOfRows;
            this.rowIndexStart = rowIndexStart;
        }

        @Override
        public void run()
        {
            simulate();
            simulationExecutedLatch.countDown();
        }

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
                if(cellPos.y == rowIndexStart+2) {
                    try {
                        pastSafetyRowForSynchronization.countDown();
                        pastSafetyRowForSynchronization.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
