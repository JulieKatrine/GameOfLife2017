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
    private double gridRenderThreshold = 4;

    /**
     * Focuses the camera to the middle of the board.
     *
     * TODO: fill me in
     * @param canvas fill me in
     */
    public BoardRendererImpl(Canvas canvas)
    {
        super(canvas);
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

        if(cellSize < gridRenderThreshold)
            return;

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setLineWidth((cellSize < 20) ? (cellSize - gridRenderThreshold) / 20.0 : 0.9);
        gc.setStroke(gridColor);

        Point startPoint = calculateStartPoint(board);
        Point stopPoint = calculateStopPoint(board, startPoint);
        Point camPos = camera.getCenterOffsetRenderingPosition(board);

        for(int y = startPoint.y; y <= stopPoint.y; y++)
            gc.strokeLine(
                    camPos.x,
                    camPos.y + y * cellSize,
                    camPos.x + board.getWidth() * cellSize,
                    camPos.y + y * cellSize);

        for(int x = startPoint.x; x <= stopPoint.x; x++)
            gc.strokeLine(
                    camPos.x + x * cellSize,
                    camPos.y,
                    camPos.x + x * cellSize,
                    camPos.y + board.getHeight() * cellSize);
    }

    private void renderLivingCells(GameBoard board)
    {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(aliveCellColor);

        Point startPoint = calculateStartPoint(board);
        Point stopPoint = calculateStopPoint(board, startPoint);
        Point camPos = camera.getCenterOffsetRenderingPosition(board);
        Point cellPos = new Point();
        double cellSize = camera.getZoom();

        for (cellPos.y = startPoint.y; cellPos.y < stopPoint.y; cellPos.y++)
        {
            double yCoordinate = camPos.y + cellPos.y * cellSize;

            for (cellPos.x = startPoint.x; cellPos.x < stopPoint.x; cellPos.x++)
            {
                if(board.isCellAliveInThisGeneration(cellPos))
                {
                    gc.fillRect(
                            camPos.x + cellPos.x * cellSize,
                            yCoordinate,
                            cellSize - 0.5,
                            cellSize - 0.5);
                }
            }
        }
    }

    /**
        Calculates the point of the first (top, left) visible cell in GameBoard.
     */
    private Point calculateStartPoint(GameBoard board)
    {
        Point camPos = camera.getCenterOffsetRenderingPosition(board);
        Point startPos = new Point(0,0);
        startPos.x = Math.min(board.getWidth()  - 1, camPos.x < 0 ? (int)(-camPos.x / camera.getZoom()) : 0);
        startPos.y = Math.min(board.getHeight() - 1, camPos.y < 0 ? (int)(-camPos.y / camera.getZoom()) : 0);
        return startPos;
    }

    /**
     Calculates the point of the last (bottom, right) visible cell in GameBoard.
     */
    private Point calculateStopPoint(GameBoard board, Point startPos)
    {
        Point stopPos = new Point(board.getWidth(),board.getHeight());
        stopPos.x = Math.min(board.getWidth(),  startPos.x + (int)(canvas.getWidth()  / camera.getZoom()) + 1);
        stopPos.y = Math.min(board.getHeight(), startPos.y + (int)(canvas.getHeight() / camera.getZoom()) + 1);
        return stopPos;
    }
}