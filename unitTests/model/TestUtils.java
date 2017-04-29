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
     * Extracts the relevant cell data from a GameBoard to a string.
     * @param board The GameBoard
     * @return A string containing the cell data
     */
    public static String trimmedGameBoardToString(GameBoard board)
    {
        StringBuilder sBuilder = new StringBuilder();
        Point[] boundingBox = board.getBoundingBox();

        Point cellPos = new Point();
        for (cellPos.y = boundingBox[0].y; cellPos.y < boundingBox[1].y; cellPos.y++)
            for (cellPos.x = boundingBox[0].x; cellPos.x < boundingBox[1].x; cellPos.x++)
                sBuilder.append(board.isCellAliveInThisGeneration(cellPos) ? '1' : '0');

        return sBuilder.toString();
    }

    /**
     * Creates a string of the cell data in a GameBoard.
     * @param board The GameBoard
     * @return A string og 1 and 0 representing the cell data.
     */
    public static String gameBoardToString(GameBoard board)
    {
        StringBuilder sBuilder = new StringBuilder();
        Point cellPos = new Point();
        for (cellPos.y = 0; cellPos.y < board.getHeight(); cellPos.y++)
            for (cellPos.x = 0; cellPos.x < board.getWidth(); cellPos.x++)
                sBuilder.append(board.isCellAliveInThisGeneration(cellPos) ? '1' : '0');

        return sBuilder.toString();
    }


    /**
     * Fills the GameBoard with the array data.
     * @param board A GameBoard.
     * @param data A byte array with ones and zeros.
     */
    public static void addDataToGameBoard(GameBoard board, byte[][] data)
    {
        int width = Math.min(board.getWidth(), data[0].length);
        int height = Math.min(board.getHeight(), data.length);

        Point cellPos = new Point();
        for(cellPos.y = 0; cellPos.y < height; cellPos.y++)
            for(cellPos.x = 0; cellPos.x < width; cellPos.x++)
                board.editThisGeneration(data[cellPos.y][cellPos.x] == 1, cellPos);
    }

    public static void addDataToGameBoard(GameBoard dst, GameBoard src)
    {
        Point cellPos = new Point();
        for(cellPos.y = 0; cellPos.y < src.getHeight(); cellPos.y++)
            for(cellPos.x = 0; cellPos.x < src.getWidth(); cellPos.x++)
                dst.editThisGeneration(src.isCellAliveInThisGeneration(cellPos), cellPos);
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
