package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameBoardTest
{
    @Test
    void getAmountOfLivingNeighbors()
    {
        GameBoard board = new GameBoardImpl(3, 3);
        Point testPosition = new Point(1, 1);
        assertEquals(0, board.getAmountOfLivingNeighbors(testPosition), "Test empty board");

        byte[][] b = {
                {1, 0, 1},
                {0, 1, 0},
                {1, 1, 1}
        };
        board = arrayToGameBoard(b);

        testPosition = new Point(0, 0);
        assertEquals(1, board.getAmountOfLivingNeighbors(testPosition),"Test cell with 1 neighbour");

        testPosition = new Point(1, 1);
        assertEquals(5, board.getAmountOfLivingNeighbors(testPosition),"Test cell with 5 neighbours");

        testPosition = new Point(2, 2);
        assertEquals(2, board.getAmountOfLivingNeighbors(testPosition),"Test cell with 2 neighbours");
    }

    @Test
    void isCellAliveInThisGeneration()
    {
        GameBoard board = new GameBoardImpl(3, 3);

        byte[][] b = {
                {1, 0, 1},
                {0, 1, 0},
                {1, 1, 1}
        };
        board = arrayToGameBoard(b);

        Point testPosition = new Point(1, 1);
        assertEquals(true, board.isCellAliveInThisGeneration(testPosition),"Tests with value = 1");

        testPosition = new Point(2, 1);
        assertEquals(false, board.isCellAliveInThisGeneration(testPosition),"Tests with value = 0");
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