package view;

import javafx.scene.canvas.Canvas;
import model.GameBoard;

public abstract class BoardRender
{
    protected Canvas canvas;
    protected double cellSize = 20;

    public BoardRender(Canvas canvas)
    {
        this.canvas = canvas;
    }

    public void setCellSize(double size)
    {
        cellSize = size;
    }

    public void render(GameBoard board)
    {

    }
}
