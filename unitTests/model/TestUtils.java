package model;

import java.util.Random;

public class TestUtils
{
    /**
     * This method specifies the board implementation to be used in every test.
     * @param height The height of the board
     * @param width The width of the board
     * @return GameBoard
     */
    public static GameBoard getGameBoardImplementation(int width, int height)
    {
        return new GameBoardDynamic(width, height);
    }

    /**
     * Extracts the relevant cell data from a GameBoard to a string
     * @param board The GameBoard
     * @return A string containing the cell data
     */
    public static String gameBoardToString(GameBoard board)
    {
        StringBuilder sBuilder = new StringBuilder();
        Point[] boundingBox = board.getBoundingBox();

        Point cellPos = new Point();
        for (cellPos.y = boundingBox[0].y; cellPos.y < boundingBox[1].y; cellPos.y++)
            for (cellPos.x = boundingBox[0].x; cellPos.x < boundingBox[1].x; cellPos.x++)
                sBuilder.append(board.isCellAliveInThisGeneration(cellPos) ? '1' : '0');

        return sBuilder.toString();
    }

    public static void addRandomCellDataTo(GameBoard board)
    {
        Random r = new Random();
        Point cellPos = new Point();
        for (cellPos.y = 0; cellPos.y < board.getHeight(); cellPos.y++)
            for (cellPos.x = 0; cellPos.x < board.getWidth(); cellPos.x++)
                board.editThisGeneration(r.nextBoolean(), cellPos);
    }

}
