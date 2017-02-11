package model;

public class GameBoardImpl extends GameBoard
{
    private boolean[][] thisGeneration;
    private boolean[][] nextGeneration;

    public GameBoardImpl(int width, int height)
    {
        super(width, height);
        thisGeneration = new boolean[height][width];
        nextGeneration = new boolean[height][width];
    }

    @Override
    public int getAmountOfLivingNeighbors(Point p)
    {
        Point startPoint = new Point(Math.max(p.x-1, 0), Math.max(p.y-1, 0));
        Point stopPoint = new Point(Math.min(p.x+2, super.getWidth()), Math.min(p.y+2, super.getHeight()));
        Point cellPos = new Point();
        int countNeighbors = 0;

        for(cellPos.y = startPoint.y; cellPos.y < stopPoint.y; cellPos.y++)
        {
            for (cellPos.x = startPoint.x; cellPos.x < stopPoint.x; cellPos.x++)
            {
                if (isCellAliveInThisGeneration(cellPos))
                    countNeighbors++;
            }
        }

        if (isCellAliveInThisGeneration(p))
            countNeighbors--;

        return countNeighbors;
    }

    @Override
    public boolean isCellAliveInThisGeneration(Point p)
    {
        return thisGeneration[p.y][p.x];
    }

    @Override
    public void setStateInNextGeneration(boolean state, Point p)
    {
        nextGeneration[p.y][p.x] = state;
    }

    @Override
    public void makeNextGenerationCurrent()
    {
        //Changing nextGeneration to thisGeneration and temporarily fill nextGeneration with the old thisGeneration array.
        boolean[][] temp = thisGeneration;
        thisGeneration = nextGeneration;
        nextGeneration = temp;
    }
}