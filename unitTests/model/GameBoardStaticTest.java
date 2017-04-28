package model;

public class GameBoardStaticTest extends GameBoardTestBase
{
    @Override
    protected GameBoard getGameBoardInstance(int width, int height)
    {
        return new GameBoardStatic(width, height);
    }
}
