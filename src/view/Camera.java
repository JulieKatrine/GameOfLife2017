package view;

import model.GameBoard;
import model.Point;

public class Camera
{
    private Point position;
    private double zoom;

    public Camera()
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
        return zoom;
    }

    public Point getCenterOffsetRenderingPosition(GameBoard board)
    {
        Point offsetPos = new Point();
        offsetPos.x = (int)(position.x - (board.getWidth() * zoom) / 2);
        offsetPos.y = (int)(position.y - (board.getHeight()* zoom) / 2);
        return offsetPos;
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
