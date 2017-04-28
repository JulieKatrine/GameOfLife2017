package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public abstract class GameBoardTestBase
{
    private Point[] testPositions =
    {
        new Point(0,0),
        new Point(1,1),
        new Point(2,2)
    };

    protected abstract GameBoard getGameBoardInstance(int width, int height);

    @Test
    void editAndReadThisGeneration()
    {
        GameBoard board = getGameBoardInstance(3, 3);

        for(int i = 0; i < testPositions.length; i++)
        {
            board.editThisGeneration(true, testPositions[i]);
            assertEquals(true, board.isCellAliveInThisGeneration(testPositions[i]));
        }
    }

    @Test
    void setStateInNextGenerationAndMakeCurrent()
    {
        GameBoard board = getGameBoardInstance(3, 3);
        for(int i = 0; i < testPositions.length; i++)
            board.setStateInNextGeneration(true, testPositions[i]);

        board.makeNextGenerationCurrent();

        assertEquals("100010001", TestUtils.trimmedGameBoardToString(board));
    }

    @Test
    void getAmountOfLivingNeighbours()
    {
        GameBoard board = getGameBoardInstance(3, 3);
        assertEquals(0, board.getAmountOfLivingNeighbours(new Point(1, 1)), "Test empty board");

        board.editThisGeneration(true, new Point(0,0));
        board.editThisGeneration(true, new Point(2,0));
        board.editThisGeneration(true, new Point(1,1));
        board.editThisGeneration(true, new Point(0,2));
        board.editThisGeneration(true, new Point(1,2));
        board.editThisGeneration(true, new Point(2,2));

        assertEquals(1, board.getAmountOfLivingNeighbours(new Point(0, 0)),"Test cell with 1 neighbour");
        assertEquals(5, board.getAmountOfLivingNeighbours(new Point(1, 1)),"Test cell with 5 neighbours");
        assertEquals(2, board.getAmountOfLivingNeighbours(new Point(2, 2)),"Test cell with 2 neighbours");
    }

    @Test
    void testHashCode()
    {
        GameBoard controlBoard = getGameBoardInstance(4, 4);
        TestUtils.addRandomCellDataTo(controlBoard);

        String controlString = TestUtils.trimmedGameBoardToString(controlBoard);
        int controlCode = controlBoard.hashCode();

        for(int i = 0; i < 1000000; i++)
        {
            GameBoard testBoard = getGameBoardInstance(4, 4);
            TestUtils.addRandomCellDataTo(testBoard);

            if(controlCode == testBoard.hashCode())
                assertEquals(controlString, TestUtils.trimmedGameBoardToString(testBoard));
        }
    }

    @Test
    void getPopulation()
    {
        GameBoard board = getGameBoardInstance(4, 4);
        TestUtils.addDataToGameBoard(board, new byte[][]
        {
            {1,0,1,0},
            {0,1,0,1},
            {1,0,1,0},
            {0,1,0,1}
        });

        assertEquals(8, board.getPopulation());
    }

    @Test
    void getDeepCopy()
    {
        GameBoard original = getGameBoardInstance(10, 10);
        TestUtils.addRandomCellDataTo(original);

        GameBoard copy = original.getDeepCopy();

        assertNotEquals(original, copy);
        assertEquals(TestUtils.trimmedGameBoardToString(original), TestUtils.trimmedGameBoardToString(copy));
    }

    @Test
    void trimmedCopy()
    {
        GameBoard original = getGameBoardInstance(6, 6);
        TestUtils.addDataToGameBoard(original, new byte[][]
        {
            {0,0,0,0,0,0},
            {0,0,1,0,0,0},
            {0,0,1,0,1,0},
            {0,1,0,1,0,0},
            {0,0,0,0,0,0},
            {0,0,0,0,0,0}
        });

        GameBoard trimmedCopy = original.trimmedCopy();

        assertNotEquals(original, trimmedCopy);
        assertEquals("010001011010", TestUtils.gameBoardToString(trimmedCopy));
    }

    @Test
    void trimmedCopyWithPadding()
    {
        GameBoard original = getGameBoardInstance(4, 4);
        TestUtils.addDataToGameBoard(original, new byte[][]
        {
            {0,0,0,0},
            {0,1,0,1},
            {1,0,1,0},
            {0,0,0,0}
        });

        GameBoard trimmedCopy = original.trimmedCopy(1);

        assertNotEquals(original, trimmedCopy);
        assertEquals("000000001010010100000000", TestUtils.gameBoardToString(trimmedCopy));
    }


    @Test
    void getBoundingBoxPopulated()
    {
        GameBoard board = getGameBoardInstance(4, 4);
        TestUtils.addDataToGameBoard(board, new byte[][]
        {
            {0,0,0,0},
            {0,1,0,1},
            {0,0,1,0},
            {0,0,0,0}
        });

        Point[] boundingBox = board.getBoundingBox();

        assertEquals(1, boundingBox[0].x); // Start x
        assertEquals(1, boundingBox[0].y); // Start y
        assertEquals(4, boundingBox[1].x); // Stop  x
        assertEquals(3, boundingBox[1].y); // Stop  y
    }

    @Test
    void getBoundingBoxEmpty()
    {
        GameBoard board = getGameBoardInstance(3, 3);
        TestUtils.addDataToGameBoard(board, new byte[][]
        {
            {0,0,0},
            {0,0,0},
            {0,0,0}
        });

        Point[] boundingBox = board.getBoundingBox();

        // Should return full size if no living cells are found.
        assertEquals(0, boundingBox[0].x); // Start x
        assertEquals(0, boundingBox[0].y); // Start y
        assertEquals(3, boundingBox[1].x); // Stop  x
        assertEquals(3, boundingBox[1].y); // Stop  y
    }
}