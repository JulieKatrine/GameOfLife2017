package view;

import model.Point;

public class Camera
{
    private Point position;
    private double zoom;

    public Camera ()
    {
        this.position = new Point(0, 0);
        this.zoom = 20;
    }

    public Point getPosition()
    {
        return position;
    }

    public double getZoom()
    {
        return  zoom;
    }

    public void setZoom(double newZoomValue)
    {
        zoom = Math.max(newZoomValue, 1);
    }

    public void move(double x, double y)
    {
        position.x += x;
        position.y += y;
    }
}
