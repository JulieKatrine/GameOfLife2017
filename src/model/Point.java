package model;

/**
 * This class wraps a X and Y coordinate into one basic object.
 * It's main purpose is to decrease the parameter count in method calls
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

    ///////////////// MATH FUNCTIONS /////////////////

    public Point add(int v) { x += v; y += v; return this; }
    public Point sub(int v) { x -= v; y -= v; return this; }
    public Point mul(double v) { x *= v; y *= v; return this; }
    public Point div(double v) { x /= v; y /= v; return this; }

    public Point add(Point v) { x += v.x; y += v.y; return this; }
    public Point sub(Point v) { x -= v.x; y -= v.y; return this; }
    public Point mul(Point v) { x *= v.x; y *= v.y; return this; }
    public Point div(Point v) { x /= v.x; y /= v.y; return this; }

    public static Point sub(Point a, int v) { return new Point(a.x - v, a.y - v); }
    public static Point add(Point a, int v) { return new Point(a.x + v, a.y + v); }
    public static Point div(Point a, int v) { return new Point(a.x / v, a.y / v); }
    public static Point mul(Point a, int v) { return new Point(a.x * v, a.y * v); }

    public static Point sub(Point a, Point b) { return new Point(a.x - b.x, a.y - b.y); }
    public static Point add(Point a, Point b) { return new Point(a.x + b.x, a.y + b.y); }
    public static Point div(Point a, Point b) { return new Point(a.x / b.x, a.y / b.y); }
    public static Point mul(Point a, Point b) { return new Point(a.x * b.x, a.y * b.y); }
}
