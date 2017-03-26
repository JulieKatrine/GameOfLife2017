package model;

/**
 * <p>This class is a dynamic implementation of GameBoard.
 * Its size will increase when living cells comes close to the edge. This is done
 * by searching the edges for living cells and increasing the width and height variables
 * accordingly. The static byte arrays is larger than what these variables says, and when
 * the variables gets near the actual array size, new larger arrays will be allocated
 * and the data copied over.
 *
 * <p>The byte arrays store information about the state of a cell and how many living
 * neighbours it has. The neighbour count for a cell is updated when a nearby cell is
 * set alive. This count is read in the next simulation step and cleared when
 * makeNextGenerationCurrent() is called.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 * @see GameBoard
 */
public class GameBoardDynamic extends GameBoard
{
    private byte[][] thisGeneration;
    private byte[][] nextGeneration;
    private int arrayWidth;
    private int arrayHeight;
    private int sizeExtension = 10;
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
     * @param p The position of the cell to be set,
     */
    @Override
    public void setStateInNextGeneration(boolean state, Point p)
    {
        setStateAndUpdateNeighbourCount(state, (boardStart.x + p.x), (boardStart.y + p.y), nextGeneration);
    }

    /**
     * Sets the state of a cell in the current generation.
     * NOTE: this method has the side effect of updating the living neighbour count of nearby cells.
     * It should therefor only be called once per simulation step.
     * @param state The state indicating whether the cell should be living (true) or dead (false).
     * @param p The position of the cell to be set,
     */
    @Override
    public void editThisGeneration(boolean state, Point p)
    {
        setStateAndUpdateNeighbourCount(state, (boardStart.x + p.x), (boardStart.y + p.y), thisGeneration);
    }

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
        else
            generation[y][x] %= 10;
    }

    /**
     * Makes the next generation become the current one.
     * It also performs a check to see if the board size should be increased
     * and a clearing of the old neighbour count.
     */
    @Override
    public void makeNextGenerationCurrent()
    {
        byte[][] temp = thisGeneration;
        thisGeneration = nextGeneration;
        nextGeneration = temp;

        if(isExtensionRequired())
            extendBoardSize();

        clearNeighbourCount();
    }

    private void clearNeighbourCount()
    {
        int width = boardStart.x + super.width;
        int height = boardStart.y + super.height;

        for(int y = boardStart.y; y < height; y++)
            for(int x = boardStart.x; x < width; x++)
                nextGeneration[y][x] = 0;
    }

    private boolean isExtensionRequired()
    {
        int width = boardStart.x + super.width;
        int height = boardStart.y + super.height;

        for(int y = boardStart.y; y < height; y++)
            if(thisGeneration[y][boardStart.x] > 0 || thisGeneration[y][width] > 0)
                return true;

        for(int x = boardStart.x; x < width; x++)
            if(thisGeneration[boardStart.y][x] > 0 || thisGeneration[height][x] > 0)
                return true;

        return false;
    }

    private void extendBoardSize()
    {
        width += 2;
        height += 2;
        boardStart = new Point((arrayWidth - width) / 2,(arrayHeight - height) / 2);

        if(boardStart.x <= 0 && boardStart.y <= 0)
            extendArraySize();
    }

    private void extendArraySize()
    {
        arrayWidth += sizeExtension *2;
        arrayHeight += sizeExtension *2;

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
}