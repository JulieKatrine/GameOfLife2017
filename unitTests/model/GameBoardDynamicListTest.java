package model;

public class GameBoardDynamicListTest extends GameBoardTestBase
{
    @Override
    protected GameBoard getGameBoardInstance(int width, int height)
    {
        return new GameBoardDynamicList(width, height, true);
    }
}
