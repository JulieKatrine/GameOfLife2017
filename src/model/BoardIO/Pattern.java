package model.BoardIO;

import model.GameBoard;
import model.GameBoardImpl;
import model.Point;

import java.util.ArrayList;

public class Pattern
{
    private String name;
    private String[] comments;
    private boolean[][] cellData;

    public void setComments(ArrayList<String> commentList)
    {
        comments = new String[commentList.size()];
        for(int i = 0; i < commentList.size(); i++)
        {
            comments[i] = commentList.get(i);

            if(comments[i].charAt(0) == 'N')
                name = comments[i].substring(1);
        }
    }

    public void setCellData(boolean[][] cellData)
    {
        this.cellData = cellData;
    }

    public String getName()
    {
        return name;
    }

    public boolean[][] getCellData()
    {
        return cellData;
    }

    public GameBoard getGameBoard()
    {
        int width = cellData[0].length;
        int height = cellData.length;

        Point cellPos = new Point();
        GameBoard gameBoard = new GameBoardImpl(width, height);

        for (cellPos.y = 0; cellPos.y < height; cellPos.y++)
        {
            for (cellPos.x = 0; cellPos.x < width; cellPos.x++)
            {
                boolean stateOfThisCell = (cellData[cellPos.y][cellPos.x]);
                gameBoard.setStateInNextGeneration(stateOfThisCell, cellPos);
            }
        }

        gameBoard.makeNextGenerationCurrent();
        return gameBoard;
    }


}