package model;

/**
 * This class wraps a X and Y coordinate into one basic object.
 * Its main purpose is to get down the parameter count in method calls
 * and further increase readability.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 */

public class Point
{
    public int x;
    public int y;

    public Point(){}

    public Point(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
}
