package model;

/**
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 */
public class GameBoardDynamic extends GameBoard
{
    private byte[][] thisGeneration;
    private byte[][] nextGeneration;
    private int arrayWidth;
    private int arrayHeight;
    private int sizeExtension = 10;
    private Point boardStart;

    public GameBoardDynamic(int width, int height)
    {
        super(width, height);
        arrayWidth = width+(sizeExtension *2);
        arrayHeight = height+(sizeExtension *2);

        thisGeneration = new byte [arrayHeight][arrayWidth];
        nextGeneration = new byte [arrayHeight][arrayWidth];
        boardStart = new Point(((arrayWidth-width)/2),((arrayHeight-height)/2));
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

    @Override
    public void setStateInNextGeneration(boolean state, Point p)
    {
        setStateAndUpdateNeighbourCount(state, (boardStart.x + p.x), (boardStart.y + p.y), nextGeneration);
    }

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

            generation[y-1][x-1] ++;
            generation[y-1][x] ++;
            generation[y-1][x+1] ++;
            generation[y][x-1] ++;
            generation[y][x+1] ++;
            generation[y+1][x-1] ++;
            generation[y+1][x] ++;
            generation[y+1][x+1] ++;
        }else{
            generation[y][x] %= 10;
        }
    }

    @Override
    public void makeNextGenerationCurrent() {
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

            for(int y = boardStart.y; y<height; y++)
            {
                if(thisGeneration[y][boardStart.x] >= 1 || thisGeneration[y][width] >= 1)
                    return true;
            }
            for(int x = boardStart.x; x < width; x++)
            {
                if(thisGeneration[boardStart.y][x] >= 1 || thisGeneration[height][x] >= 1)
                    return true;
            }
            return false;
    }

    private void extendBoardSize()
    {
        super.width += 2;
        super.height+= 2;
        boardStart = new Point(((arrayWidth-width)/2),((arrayHeight-height)/2));

        if(boardStart.x <= 0 && boardStart.y <= 0)
            extendArraySize();
    }

    private void extendArraySize() {
        arrayWidth += sizeExtension *2;
        arrayHeight += sizeExtension *2;

        Point newBoardStart = new Point(((arrayWidth-width)/2),((arrayHeight-height)/2));
        byte[][] newThisGeneration = new byte[arrayHeight][arrayWidth];
        byte[][] newNextGeneration = new byte[arrayHeight][arrayWidth];

        for(int y = 0; y<height; y++)
            for (int x = 0; x<width; x++)
                newThisGeneration[newBoardStart.y + y][newBoardStart.x + x] =
                        thisGeneration[boardStart.y + y][boardStart.x + x];

        thisGeneration = newThisGeneration;
        nextGeneration = newNextGeneration;
        boardStart = newBoardStart;
        sizeExtension += sizeExtension;
    }

}
