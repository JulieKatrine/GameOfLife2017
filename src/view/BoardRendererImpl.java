package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.GameBoard;
import model.Point;

public class BoardRendererImpl extends BoardRenderer
{
    public BoardRendererImpl(Canvas canvas)
    {
        super(canvas);
    }

    @Override
    public void render(GameBoard board)
    {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        for (int y = 0; y < board.getHeight(); y++)
        {
            for (int x = 0; x < board.getWidth(); x++)
            {
                if (board.isCellAliveInThisGeneration(new Point(x, y)))
                    gc.setFill(Color.BLACK);
                else
                    gc.setFill(Color.PINK);

                gc.fillRect(x * 20, y * 20, 19, 19);
            }
        }
    }

    private void clearCanvas()
    {

    }

    private void renderGrid(int width, int height)
    {

    }
}
