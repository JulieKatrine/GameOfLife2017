package model;


import model.simulation.ThreadedSimulatorImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class is a dynamic implementation of GameBoard utilizing Collection Lists.
 * It works in the same way as GameBoardDynamic, but stores the data in a List of ArrayLists.
 * This implementation only serves the purpose of showing List-usage and thread-safing with atomic
 * data wrappers and synchronized blocks. See the {@link GameBoardDynamic} class for a faster
 * and more memory efficient solution.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 * @deprecated This implementation had been replaced by {@link GameBoardDynamic}
 * @see GameBoard
 */

public class GameBoardDynamicList extends GameBoard
{
    private final boolean atomicDataStorage;
    private List<List<Cell>> data;
    private Point boardStart;
    private int[][] neighbourIndex = {{-1,-1}, {0,-1}, {1,-1}, {-1,0}, {1,0}, {-1,1}, {0,1}, {1,1}};

    /**
     * @param width The width of the board.
     * @param height The height of the board.
     * @param atomicDataStorage Whether or not the cell data should be stored in atomic wrappers.
     */
    public GameBoardDynamicList(int width, int height, boolean atomicDataStorage)
    {
        super(width, height);
        this.atomicDataStorage = atomicDataStorage;
        this.data = new ArrayList<>();
        this.boardStart = new Point(1,1);

        for(int y = 0; y < height + 2; y++)
        {
            data.add(new ArrayList<>());
            for (int x = 0; x < width + 2; x++)
                data.get(y).add(getNewCell());
        }
    }

    private Cell getNewCell()
    {
        if(atomicDataStorage)
            return new AtomicCell();
        else
            return new ConcurrentByteCell();
    }

    @Override
    public int getAmountOfLivingNeighbours(Point p)
    {
        return data.get(p.y + boardStart.y).get(p.x + boardStart.x).getNeighbourCount();
    }

    @Override
    public boolean isCellAliveInThisGeneration(Point p)
    {
        return data.get(p.y + boardStart.y).get(p.x + boardStart.x).isAliveInThisGen();
    }

    @Override
    public void setStateInNextGeneration(boolean state, Point p)
    {
        p.add(boardStart);

        data.get(p.y).get(p.x).setNextGen(state);

        for(int i = 0; i < 8 && state; i++)
            data.get(p.y + neighbourIndex[i][1]).get(p.x + neighbourIndex[i][0]).incrementNextGen();

        p.sub(boardStart);
    }

    @Override
    public void editThisGeneration(boolean state, Point p)
    {
        p.add(boardStart);

        if(state)
        {
            for(int i = 0; i < 8; i++)
                data.get(p.y + neighbourIndex[i][1]).get(p.x + neighbourIndex[i][0]).incrementThisGen();
        }
        else if(data.get(p.y).get(p.x).isAliveInThisGen())
        {
            for(int i = 0; i < 8; i++)
                data.get(p.y + neighbourIndex[i][1]).get(p.x + neighbourIndex[i][0]).decrementThisGen();
        }

        data.get(p.y).get(p.x).setThisGen(state);

        p.sub(boardStart);
    }

    @Override
    public void makeNextGenerationCurrent()
    {
        for(List<Cell> row : data)
            for(Cell cell : row)
                cell.updateState();

        increaseBoardSizeIfNecessary();
    }

    private void increaseBoardSizeIfNecessary()
    {
        if(livingCellsInTopOrBottomRow())
        {
            List<Cell> topRow = new ArrayList<>();
            List<Cell> bottomRow = new ArrayList<>();

            for(int i = 0; i < data.get(0).size(); i++)
            {
                topRow.add(getNewCell());
                bottomRow.add(getNewCell());
            }

            data.add(0, topRow);
            data.add(bottomRow);
            height += 2;
        }

        if(livingCellsInLeftOrRightColumn())
        {
            for(List<Cell> row : data)
            {
                row.add(0, getNewCell());
                row.add(getNewCell());
            }
            width += 2;
        }
    }

    private boolean livingCellsInTopOrBottomRow()
    {
        for(Cell cell : data.get(1))
            if(cell.isAliveInThisGen())
                return true;

        for(Cell cell : data.get(data.size() - 2))
            if(cell.isAliveInThisGen())
                return true;

        return false;
    }

    private boolean livingCellsInLeftOrRightColumn()
    {
        for(List<Cell> row: data)
            if(row.get(1).isAliveInThisGen() || row.get(row.size() - 2).isAliveInThisGen())
                return true;

        return false;
    }

    @Override
    protected GameBoard getNewInstance(int width, int height)
    {
        return new GameBoardDynamicList(width, height, atomicDataStorage);
    }

    private abstract class Cell
    {
        public abstract int getNeighbourCount();
        public abstract boolean isAliveInThisGen();
        public abstract void setNextGen(boolean state);
        public abstract void setThisGen(boolean state);
        public abstract void incrementNextGen();
        public abstract void incrementThisGen();
        public abstract void decrementThisGen();
        public abstract void updateState();
    }

    /**
     * This cell implementation uses atomic data wrappers to ensure thread safe simulation.
     * NOTE: The current ThreadedSimulatorImpl implementation is made to not cause any concurrency problems
     * and none of these thread-safe cell implementations are needed. They where made to show how
     * it can be done. Our tests show how this type of lock-based synchronization is slow and unnecessary for
     * this particular problem.
     * See the performance results in GameBoardPerformanceTest in the unitTests source folder.
     * @see ThreadedSimulatorImpl
     */
    private class AtomicCell extends Cell
    {
        private AtomicInteger thisGen;
        private AtomicInteger nextGen;

        public AtomicCell()
        {
            thisGen = new AtomicInteger(0);
            nextGen = new AtomicInteger(0);
        }

        public void setThisGen(boolean state)
        {
            thisGen.set(state ? thisGen.intValue() + 10 : thisGen.intValue() % 10);
        }

        public void setNextGen(boolean state)
        {
            nextGen.set(state ? nextGen.intValue() + 10 : nextGen.intValue() % 10);
        }

        public void incrementNextGen() { nextGen.incrementAndGet(); }
        public void incrementThisGen() { thisGen.incrementAndGet(); }
        public void decrementThisGen() { thisGen.decrementAndGet(); }
        public int getNeighbourCount()    { return thisGen.intValue() % 10; }
        public boolean isAliveInThisGen() { return thisGen.intValue() >= 10; }

        public void updateState()
        {
            thisGen.set(nextGen.intValue());
            nextGen.set(0);
        }
    }

    /**
     * This cell implementation stores the cell data in two bytes,
     * making it the smallest way of storing a cell when using object Lists.
     * This implementation shows how the synchronized block can be used to ensure
     * thread safe simulation.
     */
    private class ConcurrentByteCell extends Cell
    {
        private byte thisGen;
        private byte nextGen;

        public void setThisGen(boolean state)
        {
            synchronized(this) { thisGen = state ? (byte)(thisGen + 10) : (byte)(thisGen % 10); }
        }

        public void setNextGen(boolean state)
        {
            synchronized(this) { nextGen = state ? (byte) (nextGen + 10) : (byte) (nextGen % 10); }
        }

        public void incrementNextGen() { synchronized(this) { nextGen++; } }
        public void incrementThisGen() { synchronized(this) { thisGen++; } }
        public void decrementThisGen() { synchronized(this) { thisGen--; } }
        public int getNeighbourCount()    { return thisGen % 10; }
        public boolean isAliveInThisGen() { return thisGen >= 10; }

        public void updateState()
        {
            thisGen = nextGen;
            nextGen = 0;
        }
    }
}
