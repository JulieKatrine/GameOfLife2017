package view;

import model.Point;

public class Camera
{
    private Point position;

    public Camera ()
    {
        position = new Point(0, 0);
    }

    public Point getPosition()
    {
        return position;
    }

    public void moveX(double x)
    {
        position.x += x;
    }

    public void moveY(double y)
    {
        position.y += y;
    }

}
