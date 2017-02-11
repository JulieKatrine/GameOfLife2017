package model;

import java.util.Random;

public class BoardLoader
{
    public GameBoard newRandomBoard(int width, int height)
    {
        GameBoard gameBoard = new GameBoardImpl(width, height);
        Point cellPos = new Point();
        Random rnd = new Random();

        for (cellPos.y = 0; cellPos.y < height; cellPos.y++)
        {
            for (cellPos.x = 0; cellPos.x < width; cellPos.x++)
            {
                //Generates a random true or false value
                boolean stateOfThisCell = rnd.nextBoolean();
                gameBoard.setStateInNextGeneration(stateOfThisCell, cellPos);
            }
        }

        gameBoard.makeNextGenerationCurrent();
        return gameBoard;
    }
}
