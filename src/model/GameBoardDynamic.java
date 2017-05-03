package model;

/**
 * <p>This class is a dynamic implementation of GameBoard.
 * Its size will increase when living cells comes close to the edge. This is done
 * by searching the edges for living cells and increasing the width and height variables
 * accordingly. The static byte arrays is larger than what these variables shows, and when
 * the variables gets near the actual array size, new larger arrays will be allocated
 * and the data copied over.
 *
 * <p>The byte arrays store information about the state of a cell and how many living
 * neighbours it has. The neighbour count for a cell is updated when a nearby cell is
 * set alive. This count is read in the next simulation step and cleared when
 * makeNextGenerationCurrent() is called.
 *
 * <p>There are some specific reasons for why we chose this solution instead of using
 * a dynamic List implementation from the Java Collection library:
 * <ul>
 * <li>The available lists don't support primitive types, and since we want our application
 * to be able to load the largest common patterns, storing the cell data
 * efficiently is key. The numeric data wrapper for a byte may occupy up to 16 bytes,
 * depending on the platform, and compared to only using one single byte, wrapping the
 * data in an object like this, is not something we want.
 * <li>Smaller memory usage per cell enables more of the board to fit in the processors cache,
 * which in turn causes fewer cache misses and faster simulation.
 * <li>The different List implementations all perform internal bounding checks on data access.
 * By making sure our simulator-implementations only do work inside of the allocated arrays,
 * we can skip these bounding checks for every cell read/write and further increase performance.
 * <li>We gain more control over the dynamic resizing by doing it this way. Reallocation is never
 * done mid-simulation and is only performed when strictly needed. Checks for when the board
 * should expand is not done on every read/write to a cell, but rather on a small selection of
 * cells near the edges, one time per simulation step.
 * </ul><p>
 * The Java Collection library offers allot of useful and convenient functionality, but for this specific
 * purpose we think our manual array implementation fits better. See the {@link GameBoardDynamicList}
 * class for a board implementation where Lists are used. We also wrote tests to support our claims, see the
 * GameBoardPerformanceTest class in the unitTests source folder.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 * @see GameBoard
 */
public class GameBoardDynamic extends GameBoard
{
    private int maxCellCount = 15000 * 15000;
    private int sizeExtension = 10;
    private byte[][] thisGeneration;
    private byte[][] nextGeneration;
    private int arrayWidth;
    private int arrayHeight;
    private Point boardStart;

    /**
     * The constructor calculates the array sizes and initialises them.
     * It also calculates a point of where the "inner" board starts.
     * @param width The initial width of the board.
     * @param height The initial height of the board.
     */
    public GameBoardDynamic(int width, int height)
    {
        super(width, height);
        arrayWidth = width + 2 * sizeExtension;
        arrayHeight = height + 2 * sizeExtension;

        thisGeneration = new byte [arrayHeight][arrayWidth];
        nextGeneration = new byte [arrayHeight][arrayWidth];
        boardStart = new Point((arrayWidth - width) / 2,(arrayHeight - height) / 2);
    }

    @Override
    public int getAmountOfLivingNeighbours(Point p)
    {
        return thisGeneration[boardStart.y + p.y][boardStart.x + p.x] % 10;
    }

    @Override
    public boolean isCellAliveInThisGeneration(Point p)
    {
        return thisGeneration[boardStart.y + p.y][boardStart.x + p.x] >= 10;
    }

    /**
     * Sets the state of a cell in the next generation.
     * NOTE: this method has the side effect of updating the living neighbour count of nearby cells.
     * It should therefor only be called once per simulation step.
     * @param state The state indicating whether the cell should be living (true) or dead (false).
     * @param p The position of the cell to be set.
     */
    @Override
    public void setStateInNextGeneration(boolean state, Point p)
    {
        setStateAndUpdateNeighbourCount(state, (boardStart.x + p.x), (boardStart.y + p.y), nextGeneration);
    }

    /**
     * Sets the state of a cell in the current generation.
     * NOTE: this method has the side effect of updating the living neighbour count of nearby cells.
     * This method is synchronized to prevent concurrency problems with the ThreadedSimulatorImpl implementation.
     * @param state The state indicating whether the cell should be living (true) or dead (false).
     * @param p The position of the cell to be set.
     */
    @Override
    public void editThisGeneration(boolean state, Point p)
    {
        synchronized(thisGeneration)
        {
            setStateAndUpdateNeighbourCount(state, (boardStart.x + p.x), (boardStart.y + p.y), thisGeneration);
        }
    }

    /**
     * Sets the state of a specified cell and increments/decrements the living-
     * neighbour-count on the eight adjacent cells.
     * @param state True = alive / false = dead
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @param generation The specified generation.
     */
    private void setStateAndUpdateNeighbourCount(boolean state, int x, int y, byte[][] generation)
    {
        if(state)
        {
            generation[y][x] += 10;
            generation[y-1][x-1]++;
            generation[y-1][x]++;
            generation[y-1][x+1]++;
            generation[y][x-1]++;
            generation[y][x+1]++;
            generation[y+1][x-1]++;
            generation[y+1][x]++;
            generation[y+1][x+1]++;
        }
        else if(generation[y][x] >= 10)
        {
            generation[y][x] %= 10;
            generation[y-1][x-1]--;
            generation[y-1][x]--;
            generation[y-1][x+1]--;
            generation[y][x-1]--;
            generation[y][x+1]--;
            generation[y+1][x-1]--;
            generation[y+1][x]--;
            generation[y+1][x+1]--;
        }
    }

    /**
     * Makes the next generation become the current one.
     * It also clears the old neighbour count and increases the
     * board size if necessary.
     */
    @Override
    public void makeNextGenerationCurrent()
    {
        byte[][] temp = thisGeneration;
        thisGeneration = nextGeneration;
        nextGeneration = temp;

        clearNeighbourCount();
        increaseBoardSizeIfNecessary();
    }

    /**
     * Resets the living-neighbour-count on all cells in the nextGeneration.
     */
    private void clearNeighbourCount()
    {
        int width = boardStart.x + super.width;
        int height = boardStart.y + super.height;

        for(int y = boardStart.y; y < height; y++)
            for(int x = boardStart.x; x < width; x++)
                nextGeneration[y][x] = 0;
    }

    /**
     * Searches along the edges for living cells and increases the board size if necessary.
     * If the new size is close to the actual array size, new larger arrays will be allocated.
     */
    public void increaseBoardSizeIfNecessary()
    {
        if(arrayWidth * arrayHeight >= maxCellCount)
            return;

        boolean extendX = false;
        boolean extendY = false;
        int rightEdge = boardStart.x + width;
        int bottomEdge = boardStart.y + height;

        // Search along left and right edge.
        for(int y = boardStart.y; y < bottomEdge; y++)
            if(thisGeneration[y][boardStart.x] > 0 || thisGeneration[y][rightEdge] > 0)
            {
                width += 2;
                extendX = true;
                break;
            }

        //Search along top and bottom edge.
        for(int x = boardStart.x; x < rightEdge; x++)
            if(thisGeneration[boardStart.y][x] > 0 || thisGeneration[bottomEdge][x] > 0)
            {
                height += 2;
                extendY = true;
                break;
            }

        // Calculates a new start position if the size was increased.
        if(extendX || extendY)
            boardStart = new Point((arrayWidth - width) / 2,(arrayHeight - height) / 2);

        // Increases the underlying array size if the start position is close to the edge.
        if(boardStart.x <= 4 || boardStart.y <= 4)
            increaseArraySize(boardStart.x <= 4, boardStart.y <= 4);
    }

    /**
     * Allocates new larger arrays and copies the data over.
     */
    private void increaseArraySize(boolean xDir, boolean yDir)
    {
        try
        {
            arrayWidth  += (xDir) ? sizeExtension * 2 : 0;
            arrayHeight += (yDir) ? sizeExtension * 2 : 0;

            Point newBoardStart = new Point((arrayWidth - width) / 2,(arrayHeight - height) / 2);
            byte[][] newThisGeneration = new byte[arrayHeight][arrayWidth];
            byte[][] newNextGeneration = new byte[arrayHeight][arrayWidth];

            for(int y = 0; y < height; y++)
                for (int x = 0; x < width; x++)
                    newThisGeneration[newBoardStart.y + y][newBoardStart.x + x] =
                            thisGeneration[boardStart.y + y][boardStart.x + x];

            thisGeneration = newThisGeneration;
            nextGeneration = newNextGeneration;
            boardStart = newBoardStart;
            sizeExtension += sizeExtension;
        }
        catch (OutOfMemoryError e)
        {
            // Set the new limit
            maxCellCount = arrayWidth * arrayHeight;
            e.printStackTrace();
        }
    }

    @Override
    protected GameBoard getNewInstance(int width, int height)
    {
        return new GameBoardDynamic(width, height);
    }

}