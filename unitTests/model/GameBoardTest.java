package model;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameBoardTest
{
    private Point[] testPositions =
    {
        new Point(0,0),
        new Point(1,1),
        new Point(2,2)
    };

    @Test
    void editAndReadThisGeneration()
    {
        GameBoard board = TestUtils.getGameBoardImplementation(3, 3);

        for(int i = 0; i < testPositions.length; i++)
        {
            board.editThisGeneration(true, testPositions[i]);
            assertEquals(true, board.isCellAliveInThisGeneration(testPositions[i]));
        }
    }

    @Test
    void setStateInNextGenerationAndMakeCurrent()
    {
        GameBoard board = TestUtils.getGameBoardImplementation(3, 3);

        for(int i = 0; i < testPositions.length; i++)
            board.setStateInNextGeneration(true, testPositions[i]);

        board.makeNextGenerationCurrent();

        assertEquals("100010001", TestUtils.gameBoardToString(board));
    }

    @Test
    void getAmountOfLivingNeighbours()
    {
        GameBoard board = TestUtils.getGameBoardImplementation(3, 3);

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
        GameBoard controlBoard = TestUtils.getGameBoardImplementation(4, 4);
        TestUtils.addRandomCellDataTo(controlBoard);

        String controlString = TestUtils.gameBoardToString(controlBoard);
        int controlCode = controlBoard.hashCode();

        for(int i = 0; i < 1000000; i++)
        {
            GameBoard testBoard = TestUtils.getGameBoardImplementation(4, 4);
            TestUtils.addRandomCellDataTo(testBoard);

            if(controlCode == testBoard.hashCode())
                assertEquals(controlString, TestUtils.gameBoardToString(testBoard));
        }
    }
}