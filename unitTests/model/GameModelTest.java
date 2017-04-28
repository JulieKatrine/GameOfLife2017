package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameModelTest
{
    @Test
    void gameBoardGetterAndSetter()
    {
        GameModel gameModel = new GameModel();
        GameBoard board = new GameBoardDynamic(10, 10);
        gameModel.setGameBoard(board);
        assertEquals(board, gameModel.getGameBoard());
    }

    @Test
    void simulateNextGeneration()
    {
        GameModel gameModel = new GameModel();

        // Create a glider pattern
        gameModel.getGameBoard().editThisGeneration(true, new Point(1,0));
        gameModel.getGameBoard().editThisGeneration(true, new Point(2,1));
        gameModel.getGameBoard().editThisGeneration(true, new Point(0,2));
        gameModel.getGameBoard().editThisGeneration(true, new Point(1,2));
        gameModel.getGameBoard().editThisGeneration(true, new Point(2,2));

        // Simulate one generation of the pattern
        gameModel.simulateNextGeneration();

        assertEquals("101011010", TestUtils.trimmedGameBoardToString(gameModel.getGameBoard()));
    }
}