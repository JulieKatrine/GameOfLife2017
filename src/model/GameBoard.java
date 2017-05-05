package model;

/**
 * This abstract class holds the GOL generation data.
 * It enables different board implementations by hiding the underlying
 * data structure behind abstract methods.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 * @see GameBoardDynamic
 * @see GameBoardDynamicList
 * @see GameBoardStatic
 */

public abstract class GameBoard
{
    public static final int DEFAULT_BOARD_WIDTH = 50;
    public static final int DEFAULT_BOARD_HEIGHT = 50;
    protected int width;
    protected int height;

    /**
     * @param width The width of the board
     * @param height The height of the board
     * @throws IllegalArgumentException If size of the board is less than one.
     */
    public GameBoard(int width, int height)
    {
        if(width < 1 || height < 1)
            throw new IllegalArgumentException("Size must be more than 0, was: " + Math.min(width, height));

        this.width = width;
        this.height = height;
    }

    /**
     * @return The current width of the board.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * @return The current height of the board.
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Gets the amount of living neighbours at a given point in the current generation.
     * NOTE: Bounding checks are not performed before data access and an IndexOutOfBoundsException
     * will be thrown if the coordinates are out of bounds.
     * @param point The position from where to retrieve the neighbour count.
     * @return The amount of surrounding neighbours that are alive (0-8).
     */
    public abstract int getAmountOfLivingNeighbours(Point point);

    /**
     * Gets the state of cell at a given point in the current generation.
     * NOTE: Bounding checks are not performed before data access and an IndexOutOfBoundsException
     * will be thrown if the coordinates are out of bounds.
     * @param point The position to check.
     * @return A boolean indicating whether the cell is living (true) or dead (false).
     */
    public abstract boolean isCellAliveInThisGeneration(Point point);

    /**
     * Sets the state of a cell in the next generation.
     * NOTE: Bounding checks are not performed before data access and an IndexOutOfBoundsException
     * will be thrown if the coordinates are out of bounds.
     * @param state The state indicating whether the cell should be living (true) or dead (false).
     * @param point The position of the cell to be set.
     */
    public abstract void setStateInNextGeneration(boolean state, Point point);

    /**
     * Edits the state of a cell in the current generation.
     * Typically used when generating a board or changing cell states after a simulation.
     * NOTE: Bounding checks are not performed before data access and an IndexOutOfBoundsException
     * will be thrown if the coordinates are out of bounds.
     * @param state The state indicating whether the cell should be living (true) or dead (false).
     * @param point The position of the cell to be set.
     */
    public abstract void editThisGeneration(boolean state, Point point);

    /**
     * Makes the next generation become the current one.
     * This updates the underlying data structure, which is necessary after every simulation step.
     */
    public abstract void makeNextGenerationCurrent();

    /**
     * Counts the living cells in this generation.
     * @return The total amount of living cells.
     */
    public int getPopulation()
    {
        int count = 0;
        Point pos = new Point();
        for(pos.y = 0; pos.y < height; pos.y++)
            for(pos.x = 0; pos.x < width; pos.x++)
                if(isCellAliveInThisGeneration(pos))
                    count++;
        return count;
    }

    /**
     * Generates a unique hash code from the current generation.
     * This is useful for comparing the similarity between boards.
     * This implementation is based on the Arrays.hashCode() algorithm.
     * @return A hash code representing this generation.
     * @see controller.AnimationExportController
     * @see controller.PatternEditorController
     */
    @Override
    public int hashCode()
    {
        int code = 1;
        Point pos = new Point();
        for(pos.y = 0; pos.y < height; pos.y++)
            for(pos.x = 0; pos.x < width; pos.x++)
                code = 31 * code + (isCellAliveInThisGeneration(pos) ? 1231 : 1237);

        return code;
    }

    /**
     * Creates and returns a new instance of this board containing the same cell data.
     * @return A deep copy of the GameBoard.
     */
    public GameBoard deepCopy()
    {
        return getSubBoard(new Point(0,0), new Point(width, height), 0);
    }

    /**
     * @return A trimmed copy of the GameBoard.
     */
    public GameBoard trimmedCopy()
    {
        return trimmedCopy(0);
    }

    /**
     * Creates a trimmed copy of the GameBoard with a padded layer of dead cells around it.
     * @param padding The amount of empty cells around the pattern
     * @return A trimmed copy of the GameBoard.
     */
    public GameBoard trimmedCopy(int padding)
    {
        padding = Math.max(0, padding);
        Point[] bBox = getBoundingBox();
        return getSubBoard(bBox[0], bBox[1], padding);
    }

    /**
     * Finds the top-left and bottom-right corners of the living cells in
     * the current generation.
     * @return A Point array. First element = start, last element = stop.
     */
    public Point[] getBoundingBox()
    {
        Point stop = new Point();
        Point cellPos = new Point();
        Point start = new Point(width, height);

        for (cellPos.y = 0; cellPos.y < height; cellPos.y++)
            for (cellPos.x = 0; cellPos.x < width; cellPos.x++)
                if(isCellAliveInThisGeneration(cellPos))
                {
                    start.x = Math.min(cellPos.x, start.x);
                    start.y = Math.min(cellPos.y, start.y);
                    stop.x  = Math.max(cellPos.x + 1, stop.x);
                    stop.y  = Math.max(cellPos.y + 1, stop.y);
                }

        // If no living cells where found, return full size.
        if(start.x == width || start.y == height)
        {
            start = new Point();
            stop = new Point(width, height);
        }

        return new Point[] {start, stop};
    }

    /**
     * Creates a new board containing a copy of an area of this board from the given start and stop coordinates.
     * @param start The start point of the sub area on the GameBoard.
     * @param stop The end point of the sub area on the GameBoard.
     * @return A sub area of the GameBoard
     */
    private GameBoard getSubBoard(Point start, Point stop, int padding)
    {
        Point size = Point.sub(stop, start).add(padding * 2);
        GameBoard subBoard = getNewInstance(size.x, size.y);

        Point pos = new Point();
        for(pos.y = start.y; pos.y < stop.y; pos.y++)
            for(pos.x = start.x; pos.x < stop.x; pos.x++)
                subBoard.editThisGeneration(this.isCellAliveInThisGeneration(pos), Point.sub(pos, start).add(padding));

        return subBoard;
    }

    /**
     * This method returns a new instance of the current one.
     * @param width The width of the new GameBoard.
     * @param height The height of the new GameBoard.
     * @return A new GameBoard instance.
     */
    protected abstract GameBoard getNewInstance(int width, int height);
}
