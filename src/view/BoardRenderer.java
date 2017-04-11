package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import model.GameBoard;

/**
 * Renders the board with Canvas.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 */
public abstract class BoardRenderer
{
    protected final Canvas canvas;
    protected final Camera camera;

    public BoardRenderer(Canvas canvas)
    {
        this.canvas = canvas;
        this.camera = new Camera(canvas);
    }

    public Camera getCamera()
    {
        return camera;
    }

    public void scaleViewToFitBoard(GameBoard board)
    {
        double maxCellWidth = canvas.getWidth() / (board.getWidth() + 2);
        double maxCellHeight = canvas.getHeight() / (board.getHeight() + 2);
        camera.reeset();
        camera.setZoom(Math.min(maxCellWidth, maxCellHeight));
    }

    public abstract void render(GameBoard board);

    public abstract void setDeadCellColor(Paint value);

    public abstract void setLivingCellColor(Paint value);

    public abstract void setGridColor(Paint value);
}
