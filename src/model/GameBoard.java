package model;

public abstract class GameBoard
{
    protected int WIDTH;
    protected int HEIGHT;

    public GameBoard(int width, int height)
    {
        WIDTH = width;
        HEIGHT = height;
    }

    public int getHEIGHT(){
        return HEIGHT;
    }

    public int getWIDTH(){
        return WIDTH;
    }

    public abstract int getAmountOfLivingNeighbors(Point p);

    public abstract boolean isCellAliveInNextGeneration(Point p);

    public abstract void setStateInNextGeneration(boolean state, Point p);

    public abstract void makeNextGenerationCurrent();

}
