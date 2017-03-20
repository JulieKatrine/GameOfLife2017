package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.GameBoard;
import model.Point;

/**
 * Renders the board with Canvas.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 */
public class BoardRendererImpl extends BoardRenderer
{
    private Color deadCellColor = Color.PINK;
    private Color aliveCellColor = Color.BLACK;
    private Color gridColor = Color.BLACK;

    /**
     * Focuses the camera to the middle of the board.
     *
     * TODO: fill me in
     * @param canvas fill me in
     */
    public BoardRendererImpl(Canvas canvas)
    {
        super(canvas);
        camera.move(canvas.getWidth() / 2, canvas.getHeight() / 2);
    }

    /**
     * Clears the board, and then renders respectively the dead cells, the grid and the living cells.
     * @param board The current board.
     */
    @Override
    public void render(GameBoard board)
    {
        clearCanvas();
        renderDeadCells(board);
        renderGrid(board);
        renderLivingCells(board);
    }

    private void clearCanvas()
    {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void renderDeadCells(GameBoard board)
    {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Point camPos = camera.getCenterOffsetRenderingPosition(board);
        gc.setFill(deadCellColor);
        gc.fillRect(
                camPos.x,
                camPos.y,
                board.getWidth() * camera.getZoom(),
                board.getHeight() * camera.getZoom());
    }

    private void renderGrid(GameBoard board)
    {
        double cellSize = camera.getZoom();
        Point camPos = camera.getCenterOffsetRenderingPosition(board);

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setLineWidth((cellSize < 30) ? cellSize / 30.0 : 0.9);
        gc.setStroke(gridColor);

        for(int y = 0; y <= board.getHeight(); y++)
            gc.strokeLine(
                    camPos.x,
                    camPos.y + y * cellSize,
                    camPos.x + board.getWidth() * cellSize,
                    camPos.y + y * cellSize);

        for(int x = 0; x <= board.getWidth(); x++)
            gc.strokeLine(
                    camPos.x + x * cellSize,
                    camPos.y,
                    camPos.x + x * cellSize,
                    camPos.y + board.getHeight() * cellSize);
    }

    private void renderLivingCells(GameBoard board)
    {
        double cellSize = camera.getZoom();
        Point camPos = camera.getCenterOffsetRenderingPosition(board);
        Point cellPos = new Point();

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(aliveCellColor);

        for (cellPos.y = 0; cellPos.y < board.getHeight(); cellPos.y++)
        {
            for (cellPos.x = 0; cellPos.x < board.getWidth(); cellPos.x++)
            {
                if(board.isCellAliveInThisGeneration(cellPos))
                {
                    gc.fillRect(
                            camPos.x + cellPos.x * cellSize,
                            camPos.y + cellPos.y * cellSize,
                            cellSize - 0.5,
                            cellSize - 0.5);
                }
            }
        }
    }
}