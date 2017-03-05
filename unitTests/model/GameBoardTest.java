package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameBoardTest
{
    @Test
    void getAmountOfLivingNeighbors()
    {
        GameBoard board = new GameBoardImpl(3, 3);
        Point testPosition = new Point(1, 1);
        assertEquals(0, board.getAmountOfLivingNeighbors(testPosition), "Test empty board");

        board = arrayToGameBoard(new byte[][]
        {
                {1,0,1},
                {0,1,0},
                {1,1,1}
        });

        testPosition = new Point(1, 1);
        assertEquals(5, board.getAmountOfLivingNeighbors(testPosition),"Test cell with 5 neighbours");

        testPosition = new Point(0, 0);
        assertEquals(1, board.getAmountOfLivingNeighbors(testPosition),"Test cell with 1 neighbour");

        testPosition = new Point(2, 2);
        assertEquals(2, board.getAmountOfLivingNeighbors(testPosition),"Test cell with 2 neighbours");
    }

    @Test
    void isCellAliveInThisGeneration()
    {

    }

    @Test
    void setStateInNextGeneration()
    {

    }

    @Test
    void makeNextGenerationCurrent()
    {

    }

    private GameBoard arrayToGameBoard(byte[][] array)
    {
        GameBoard gameBoard = new GameBoardImpl(array[0].length, array.length);

        for(int y = 0; y < array.length; y++)
            for(int x = 0; x < array[0].length; x++)
                gameBoard.setStateInNextGeneration(array[y][x] == 1, new Point(x, y));

        gameBoard.makeNextGenerationCurrent();
        return gameBoard;
    }
}