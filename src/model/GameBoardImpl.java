package model;

public class GameBoardImpl extends GameBoard
{
    private boolean[][] thisGeneration;
    private boolean[][] nextGeneration;

    public GameBoardImpl(int width, int height)
    {
        super(width, height);
    }

    @Override
    public int getAmountOfLivingNeighbors(Point p)
    {
        return 0;
    }

    @Override
    public boolean isCellAliveInThisGeneration(Point p)
    {
        return false;
    }

    @Override
    public void setStateInNextGeneration(boolean state, Point p)
    {

    }

    @Override
    public void makeNextGenerationCurrent()
    {

    }
}
