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
        camera.moveX(canvas.getWidth() / 2);
        camera.moveY(canvas.getHeight() / 2);
    }

    @Override
    public void render(GameBoard board)
    {
        clearCanvas();
        Point centerOffset = new Point((int)(board.getWidth() * cellSize / 2), (int)(board.getHeight() * cellSize / 2 ));
        renderGrid(board.getWidth(), board.getHeight(), centerOffset);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        Point cellPos = new Point();

        for (cellPos.y = 0; cellPos.y < board.getHeight(); cellPos.y++)
        {
            for (cellPos.x = 0; cellPos.x < board.getWidth(); cellPos.x++)
            {
                gc.setFill(board.isCellAliveInThisGeneration(cellPos) ? Color.BLACK : Color.WHITE);
                gc.fillRect(
                        camera.getPosition().x - centerOffset.x + cellPos.x * super.cellSize,
                        camera.getPosition().y - centerOffset.y + cellPos.y * super.cellSize,
                        super.cellSize - 1,
                        super.cellSize - 1);
            }
        }
    }

    private void clearCanvas()
    {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void renderGrid(int width, int height, Point offset)
    {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setStroke(Color.GRAY);

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
