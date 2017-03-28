package model;

/**
 * This abstract class holds the GOL generation data.
 * It enables different board implementations by hiding the underlying
 * data structure behind abstract methods.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
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
     */
    public GameBoard(int width, int height)
    {
        this.width = width;
        this.height = height;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    /**
     * Gets the amount of living neighbours at a given point in the current generation.
     * @param point The position from where to retrieve the neighbour count.
     * @return The amount of surrounding neighbours that are alive (0-8).
     */
    public abstract int getAmountOfLivingNeighbours(Point point);


    /**
     * Gets the state of cell at a given point in the current generation.
     * @param point The position to check.
     * @return A boolean indicating whether the cell is living (true) or dead (false).
     */
    public abstract boolean isCellAliveInThisGeneration(Point point);


    /**
     * Sets the state of a cell in the next generation.
     * @param state The state indicating whether the cell should be living (true) or dead (false).
     * @param point The position of the cell to be set.
     */
    public abstract void setStateInNextGeneration(boolean state, Point point);


    /**
     * Edits the state of a cell in the current generation.
     * Typically used when generating a board or changing cell states after a simulation.
     * @param state The state indicating whether the cell should be living (true) or dead (false).
     * @param point The position of the cell to be set.
     */
    public abstract void editThisGeneration(boolean state, Point point);


    /**
     * Makes the next generation become the current one.
     * This updates the underlying data structure, which is necessary after every simulation step.
     */
    public abstract void makeNextGenerationCurrent();
}
