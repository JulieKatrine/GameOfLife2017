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
        return 0;
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
