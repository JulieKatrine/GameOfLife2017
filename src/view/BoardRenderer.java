package view;

import javafx.scene.canvas.Canvas;
import model.GameBoard;

public abstract class BoardRenderer
{
    protected final Canvas canvas;
    protected final Camera camera;

    public BoardRenderer(Canvas canvas)
    {
        this.canvas = canvas;
        this.camera = new Camera();
    }

    public Camera getCamera()
    {
        return camera;
    }

    public abstract void render(GameBoard board);
}
