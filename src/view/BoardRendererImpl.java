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
        Point cellPos = new Point();

        for (cellPos.y = 0; cellPos.y < board.getHeight(); cellPos.y++)
        {
            for (cellPos.x = 0; cellPos.x < board.getWidth(); cellPos.x++)
            {
                if (board.isCellAliveInThisGeneration(cellPos))
                    gc.setFill(Color.BLACK);
                else
                    gc.setFill(Color.PINK);

                gc.fillRect(cellPos.x * super.cellSize, cellPos.y * super.cellSize, super.cellSize - 1, super.cellSize - 1);
            }
        }
    }

    private void clearCanvas()
    {
        // TODO: If we decide to only draw living cells, we have to clear the canvas with a colored rectangle first
    }

    private void renderGrid(int width, int height)
    {
        // TODO: Render a grid of lines
    }
}
