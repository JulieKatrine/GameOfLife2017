package model.BoardIO;

import model.GameBoard;
import model.GameBoardDynamic;
import model.GameBoardStatic;
import model.Point;

import java.util.ArrayList;

/**
 * Creates an object from a pattern.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 * @see PatternLoader
 */
public class Pattern
{
    private String name;
    private String[] comments;
    private boolean[][] cellData;

    /**
     * Lists comments that may appear in the file/URL.
     *
     * If the comment starts with an N, it is set to be the name of the pattern.
     *
     * @param commentList Takes in a list of lines starting with # -meaning it's a comment.
     */
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
        int width = cellData[0].length + 2;
        int height = cellData.length + 2;

        Point cellPos = new Point();
        GameBoard gameBoard = new GameBoardDynamic(width, height);

        for (cellPos.y = 1; cellPos.y < height-1; cellPos.y++)
            for (cellPos.x = 1; cellPos.x < width-1; cellPos.x++)
                gameBoard.editThisGeneration(cellData[cellPos.y-1][cellPos.x-1], cellPos);

        return gameBoard;
    }
}