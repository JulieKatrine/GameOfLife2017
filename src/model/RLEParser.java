package model;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

public class RLEParser
{
    private final int INVALID = -1;
    private final char DEAD_CELL = 'b';
    private final char LIVING_CELL = 'o';
    private final char END_OF_LINE = '$';

    private ArrayList<String> comments;
    private int width = INVALID;
    private int height = INVALID;
    private char[][] boardData;
    private String rule = "";

    public RLEParser()
    {
        comments = new ArrayList<String>();
    }

    public GameBoard parse(Reader reader) throws IOException
    {
        int character;

        while ((character = reader.read()) != INVALID)
        {
            if (character == '#')
                readComment(reader);
            else if (character == 'x')
                readBoardDefinition(reader);
            // TODO: Throw exeption.
            // else System.err.println("Cannot read line.");

            if (boardData != null)
                readCellData(reader);
        }

        if (boardData != null) {
            System.out.println("Test: " + width + " " + height + " " + rule);
            return createGameBoard();
        }
        else
            //TODO: Throw exeption.
            return null;
    }


    public void readComment(Reader reader) throws IOException
    {
        int character;
        StringBuilder comment = new StringBuilder();

        while ((character = reader.read()) != INVALID && character != '\n')
            comment.append((char) character);

        comments.add(comment.toString());
    }

    private void readBoardDefinition(Reader reader) throws IOException
    {
        int character;
        int number = 0;
        while ((character = reader.read()) != INVALID && character != '\n')
        {
            if (height == INVALID)
            {
                if (Character.isDigit((char) character))
                    number = number * 10 + (character - '0');
                else if (number != 0)
                {
                    if (width == INVALID)
                        width = number;
                    else
                        height = number;

                    number = 0;
                }
            }
            else if (character == 'B' || rule.length() > 0)
            {
                rule += (char)character;
                //TODO: Create rule decoder.
            }
        }
        if (width != INVALID && height != INVALID)
            boardData = new char[height][width];
    }

    private void readCellData(Reader reader) throws IOException
    {
        int character;
        int number = 0;
        int y = 0;
        int index = 0;

        while ((character = reader.read()) != INVALID && character != '!')
        {
            if (Character.isDigit((char) character))
                number = number * 10 + (character - '0');

            else if (character == DEAD_CELL || character == LIVING_CELL)
            {
                if (number == 0)
                    number = 1;

                for (int x = index; x<(index+number); x++)
                    boardData[y][x] = (char)character;

                index += number;
                number = 0;
            }
            else if (character == END_OF_LINE)
            {
                y++;
                index = 0;
            }
            //else //TODO: Throw exception
        }
    }

    private GameBoard createGameBoard()
    {
        Point cellPos = new Point();
        GameBoard gameBoard = new GameBoardImpl(width, height);

        for (cellPos.y = 0; cellPos.y < height; cellPos.y++)
        {
            for (cellPos.x = 0; cellPos.x < width; cellPos.x++)
            {
                boolean stateOfThisCell = (boardData[cellPos.y][cellPos.x] == LIVING_CELL);
                gameBoard.setStateInNextGeneration(stateOfThisCell, cellPos);
            }
        }

        gameBoard.makeNextGenerationCurrent();
        return gameBoard;
    }

}
