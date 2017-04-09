package model;

public class TestUtilities
{
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
