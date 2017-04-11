package model;

import javafx.scene.canvas.Canvas;
import org.junit.jupiter.api.Test;
import view.Camera;

import static org.junit.jupiter.api.Assertions.*;

class BoardEditorTest
{
    @Test
    void edit()
    {
        Camera camera = new Camera(new Canvas(50, 50));
        camera.setZoom(10);
        BoardEditor editor = new BoardEditor(camera);
        GameBoard board = TestUtils.getGameBoardImplementation(5,5);

        editor.edit(board, new Point(5,5),true);
        editor.edit(board, new Point(25,25),true);
        editor.edit(board, new Point(45,45),true);

        assertEquals(true, board.isCellAliveInThisGeneration(new Point(0,0)));
        assertEquals(true, board.isCellAliveInThisGeneration(new Point(2,2)));
        assertEquals(true, board.isCellAliveInThisGeneration(new Point(4,4)));
    }
}