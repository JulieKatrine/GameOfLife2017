package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GameBoardTest
{
    private Point[] testPositions =
    {
        new Point(0,0),
        new Point(1,1),
        new Point(2,2)
    };

    /**
     * This method specifies the board implementation to be used in every test.
     *
     * @param height The height of the board
     * @param width The width of the board
     * @return GameBoard
     */
    public static GameBoard getGameBoardImplementation(int width, int height)
    {
        return new GameBoardDynamic(width, height);
    }

    @Test
    void editAndReadThisGeneration()
    {
        GameBoard board = getGameBoardImplementation(3, 3);

        for(int i = 0; i < testPositions.length; i++)
        {
            board.editThisGeneration(true, testPositions[i]);
            assertEquals(true, board.isCellAliveInThisGeneration(testPositions[i]));
        }
    }

    @Test
    void setStateInNextGenerationAndMakeCurrent()
    {
        GameBoard board = getGameBoardImplementation(3, 3);

        for(int i = 0; i < testPositions.length; i++)
            board.setStateInNextGeneration(true, testPositions[i]);

        board.makeNextGenerationCurrent();

        for(int i = 0; i < testPositions.length; i++)
            assertEquals(true, board.isCellAliveInThisGeneration(testPositions[i]));
    }

    @Test
    void getAmountOfLivingNeighbours()
    {
        GameBoard board = getGameBoardImplementation(3, 3);

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
}