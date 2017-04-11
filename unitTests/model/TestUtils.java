package model;

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
        Point[] boundingBox = getGameBoardBoundingBox(board);

        Point cellPos = new Point();
        for (cellPos.y = boundingBox[0].y; cellPos.y <= boundingBox[1].y; cellPos.y++)
            for (cellPos.x = boundingBox[0].x; cellPos.x <= boundingBox[1].x; cellPos.x++)
                sBuilder.append(board.isCellAliveInThisGeneration(cellPos) ? '1' : '0');

        return sBuilder.toString();
    }

    /**
     * Finds the top, bottom, left and right border of where the cell data.
     * @param board The GameBoard
     * @return A Point array. First element = start, last element = stop.
     */
    private static Point[] getGameBoardBoundingBox(GameBoard board)
    {
        Point start = new Point(board.getWidth(), board.getHeight());
        Point stop = new Point(0, 0);

        Point cellPos = new Point();
        for (cellPos.y = 0; cellPos.y < board.getHeight(); cellPos.y++)
            for (cellPos.x = 0; cellPos.x < board.getWidth(); cellPos.x++)
                if(board.isCellAliveInThisGeneration(cellPos))
                {
                    start.x = Math.min(cellPos.x, start.x);
                    start.y = Math.min(cellPos.y, start.y);
                    stop.x = Math.max(cellPos.x, stop.x);
                    stop.y = Math.max(cellPos.y, stop.y);
                }

        return new Point[] {start, stop};
    }
}
