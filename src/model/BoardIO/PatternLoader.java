package model.BoardIO;

import model.GameBoard;
import model.GameBoardImpl;
import model.Point;

import java.io.*;
import java.net.URL;
import java.util.Random;

public class PatternLoader
{
    public GameBoard newRandomBoard(int width, int height)
    {
        GameBoard gameBoard = newEmptyBoard(width, height);
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

    public GameBoard newEmptyBoard(int width, int height)
    {
        return new GameBoardImpl(width, height);
    }

    public Pattern loadFromDisk(File file) throws IOException, FileNotSupportedException
    {
        return loadGameBoard(new FileReader(file), extractFileType(file.getName()));
    }

    public Pattern loadFromURL(String url) throws IOException, FileNotSupportedException
    {
        URL destination = new URL(url);
        return loadGameBoard(new InputStreamReader(destination.openConnection().getInputStream()), extractFileType(url));
    }

    private String extractFileType(String fileName)
    {
        int lastPosition = fileName.lastIndexOf('.');
        if (lastPosition > 0)
            return fileName.substring(lastPosition);
        else
            return "";
    }

    private Pattern loadGameBoard(Reader reader, String fileType) throws IOException, FileNotSupportedException
    {
        switch (fileType)
        {
            case ".rle":
                return (new RLEParser()).parse(reader);

            default:
                throw new FileNotSupportedException(fileType + " it not supported.");
        }
    }

}










