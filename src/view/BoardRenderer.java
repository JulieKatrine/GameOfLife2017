package view;

import javafx.scene.canvas.Canvas;
import model.GameBoard;

public abstract class BoardRenderer
{
    protected final Canvas canvas;
    protected double cellSize = 20;

    public BoardRenderer(Canvas canvas)
    {
        this.canvas = canvas;
    }

    public void setCellSize(double size)
    {
        cellSize = size;
    }

    public abstract void render(GameBoard board);
}
