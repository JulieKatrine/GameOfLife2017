package controller;

import model.GameBoard;
import model.GameBoardImpl;
import model.Point;

import java.util.Random;

public class BoardLoader
{
    public GameBoard newRandomBoard()
    {
        Random rnd = new Random();
        GameBoard gameBoard = new GameBoardImpl(50, 50);

        for (int y = 0; y < gameBoard.getHeight(); y++)
        {
            for (int x = 0; x < gameBoard.getWidth(); x++)
            {
                //Generates a random true or false value
                boolean stateOfThisCell = rnd.nextBoolean();
                gameBoard.setStateInNextGeneration(stateOfThisCell, new Point(x, y));
            }
        }
        gameBoard.makeNextGenerationCurrent();
        return gameBoard;
    }
}
