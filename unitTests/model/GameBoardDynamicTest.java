package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameBoardDynamicTest extends GameBoardTestBase
{
    @Override
    protected GameBoard getGameBoardInstance(int width, int height)
    {
        return new GameBoardDynamic(width, height);
    }

    @Test
    void increaseBoardSizeIfNecessary()
    {
        GameBoardDynamic board = new GameBoardDynamic(10, 10);

        for(int i = 0; i < 20; i++)
        {
            board.editThisGeneration(true, new Point(board.getWidth() - 1,board.getHeight() - 1));
            board.increaseBoardSizeIfNecessary();
        }

        assertEquals(50, board.getWidth());
        assertEquals(50, board.getHeight());
    }
}
