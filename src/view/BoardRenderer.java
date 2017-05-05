package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.GameBoard;
import model.Point;

/**
 * This class renders a {@link GameBoard} using Canvas and a {@link Camera}.
 * The cells are drawn according to a {@link ColorProfile}, set from the ColoPickers in the main Stage.
 * It contains methods for drawing the grid and the cells, and for scaling the view.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 */
public class BoardRenderer
{
    protected final Canvas canvas;
    protected final Camera camera;
    private ColorProfile colorProfile;
    private double gridRenderThreshold = 4;
    private boolean scaleViewOnRender = false;

    public BoardRenderer(Canvas canvas)
    {
        this.canvas = canvas;
        this.camera = new Camera(canvas);
        this.colorProfile = new ColorProfile(Color.BLACK, Color.GREEN, Color.GRAY);
    }

    public Camera getCamera()
    {
        return camera;
    }

    /**
     * Renders the grid and the cells. Scales the view if the scaleViewOnRender parameter is set to true.
     * @param board The current board.
     */
    public void render(GameBoard board)
    {
        clearCanvas();
        scaleView(board);
        renderDeadCells(board);
        renderGrid(board);
        renderLivingCells(board);
    }

    private void scaleView(GameBoard board)
    {
        if(scaleViewOnRender)
            scaleViewToFitBoard(board);
    }

    public void scaleViewToFitBoard(GameBoard board)
    {
        double maxCellWidth = canvas.getWidth() / (board.getWidth() + 2);
        double maxCellHeight = canvas.getHeight() / (board.getHeight() + 2);
        camera.reset();
        camera.setZoom(Math.min(maxCellWidth, maxCellHeight));
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
        gc.setFill(colorProfile.getDeadColor());
        gc.fillRect(
                camPos.x,
                camPos.y,
                board.getWidth() * camera.getZoom(),
                board.getHeight() * camera.getZoom());
    }

    private void renderGrid(GameBoard board)
    {
        double cellSize = camera.getZoom();

        if(!colorProfile.renderGrid() || cellSize < gridRenderThreshold)
            return;

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setLineWidth((cellSize < 20) ? (cellSize - gridRenderThreshold) / 20.0 : 0.9);
        gc.setStroke(colorProfile.getGridColor());

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
        gc.setFill(colorProfile.getAliveColor());

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

    //Calculates the point of the first (top, left) visible cell in GameBoard.
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

    public ColorProfile getColorProfile()
    {
        return colorProfile;
    }

    public void setColorProfile(ColorProfile profile)
    {
        this.colorProfile = profile;
    }

    /**
     * Enables automatic scaling of the view on every render call.
     * @param state Whether or not the view should be scaled.
     */
    public void setScaleViewOnRender(boolean state)
    {
        this.scaleViewOnRender = state;
    }
}
