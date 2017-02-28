package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.GameBoard;
import model.Point;

public class BoardRendererImpl extends BoardRenderer
{
    private Color deadCellColor = Color.PINK;
    private Color aliveCellColor = Color.BLACK;
    private Color gridColor = Color.GRAY;

    public BoardRendererImpl(Canvas canvas)
    {
        super(canvas);
        camera.move(canvas.getWidth() / 2, canvas.getHeight() / 2);
    }

    @Override
    public void render(GameBoard board)
    {
        double cellSize = camera.getZoom();
        Point cellPos = new Point();
        Point centerOffset = new Point((int)(board.getWidth() * cellSize / 2), (int)(board.getHeight() * cellSize / 2 ));
        GraphicsContext gc = canvas.getGraphicsContext2D();

        clearCanvas();

        for (cellPos.y = 0; cellPos.y < board.getHeight(); cellPos.y++)
        {
            for (cellPos.x = 0; cellPos.x < board.getWidth(); cellPos.x++)
            {
                gc.setFill(board.isCellAliveInThisGeneration(cellPos) ? aliveCellColor : deadCellColor);
                gc.fillRect(
                        camera.getPosition().x - centerOffset.x + cellPos.x * camera.getZoom(),
                        camera.getPosition().y - centerOffset.y + cellPos.y * camera.getZoom(),
                        camera.getZoom(),
                        camera.getZoom());
            }
        }

        renderGrid(board.getWidth(), board.getHeight(), centerOffset);
    }

    private void clearCanvas()
    {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void renderGrid(int width, int height, Point offset)
    {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(gridColor);

        double cellSize = camera.getZoom();

        for(int y = 0; y <= height; y++)
            gc.strokeLine(
                    camera.getPosition().x - offset.x,
                    camera.getPosition().y - offset.y + y * cellSize,
                    camera.getPosition().x - offset.x + width * cellSize,
                    camera.getPosition().y - offset.y + y * cellSize);

        for(int x = 0; x <= width; x++)
            gc.strokeLine(
                    camera.getPosition().x - offset.x + x * cellSize,
                    camera.getPosition().y - offset.y,
                    camera.getPosition().x - offset.x + x * cellSize,
                    camera.getPosition().y - offset.y + height * cellSize);
    }
}
