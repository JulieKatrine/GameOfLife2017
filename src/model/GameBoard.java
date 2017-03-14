package model;

public abstract class GameBoard
{
    private int width;
    private int height;

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

    public abstract int getAmountOfLivingNeighbors(Point p);
    public abstract boolean isCellAliveInThisGeneration(Point p);
    public abstract void setStateInNextGeneration(boolean state, Point p);
    public abstract void editThisGeneration(boolean state, Point p);
    public abstract void makeNextGenerationCurrent();
}
