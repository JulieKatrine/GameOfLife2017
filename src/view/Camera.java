package view;

import javafx.scene.canvas.Canvas;
import model.GameBoard;
import model.Point;

import java.util.ArrayList;
import java.util.List;

public class Camera
{
    private double zoom;
    private Canvas canvas;

    private double posX = 0;
    private double posY = 0;

    public Camera(Canvas canvas)
    {
        this.zoom = 20;
        this.canvas = canvas;
    }

    public double getZoom()
    {
        return zoom;
    }

    public Point getCenterOffsetRenderingPosition(GameBoard board)
    {
        Point offsetPos = new Point();

        double boardWidth = board.getWidth() * zoom;
        double boardHeight = board.getHeight() * zoom;

        double windowCenterX = canvas.getWidth() / 2;
        double windowCenterY = canvas.getHeight() / 2;

        offsetPos.x = (int)(windowCenterX - boardWidth * (posX + 0.5));
        offsetPos.y = (int)(windowCenterY - boardHeight * (posY + 0.5));

        return offsetPos;
    }

    public void setZoom(double newZoomValue)
    {

        zoom = Math.max(newZoomValue, 1);
    }

    public void move(double x, double y)
    {
        posX -= x / canvas.getWidth();
        posY -= y / canvas.getHeight();                                                            ;

        posX = Math.min(1, Math.max(-1, posX));
        posY = Math.min(1, Math.max(-1, posY));
    }
}
