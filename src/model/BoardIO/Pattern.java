package model.BoardIO;

import model.GameBoard;
import model.GameBoardDynamic;
import model.Point;
import model.simulation.CustomRule;
import model.simulation.DefaultRuleSet;
import model.simulation.SimRule;

import java.util.ArrayList;

/**
 * This class wraps relevant information of a loaded pattern into a convenient Pattern object.
 * It supplies getters and setters for this information, as well as a method for generating a GameBoard
 * from the cell data.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 * @see Parser
 * @see PatternLoader
 */
public class Pattern
{
    private String name;
    private String author;
    private String comments;
    private boolean[][] cellData;
    private String rule;

    /**
     * Adds the loaded metadata to the Pattern object.
     * The first character in a line determines what kind of metadata it holds.
     * N - Patterns name
     * O - The author of the pattern
     * C - Other information
     *
     * @param metadataList A list of metadata lines.
     */
    public void setMetadata(ArrayList<String> metadataList)
    {
        StringBuilder commentBuilder = new StringBuilder();
        for(String string : metadataList)
        {
            if(string.length() > 0)
            {
                char prefix = string.charAt(0);
                String line = string.substring(1);

                switch (prefix)
                {
                    case 'N': name = line;   break;
                    case 'O': author = line; break;
                    case 'C': commentBuilder.append(line + '\n'); break;
                }
            }
        }
        comments = commentBuilder.toString();
    }

    public void setCellData(boolean[][] cellData)
    {
        this.cellData = cellData;
    }

    public void setRule(String rule){
        this.rule = rule;
    }

    public String getName()
    {
        return name;
    }

    public boolean[][] getCellData()
    {
        return cellData;
    }

    public SimRule getRule()
    {
        if(rule != null)
            return new CustomRule(rule);
        else
            return new DefaultRuleSet();
    }

    /**
     * @return A String containing the pattern name, author and additional information
     */
    public String getAllMetadata()
    {
        return (name != null ? ("Name: " + name + '\n') : "")  +
                (author != null ? ("Author: " + author + '\n') : "") +
                (rule != null ? ("Rule: " + rule + '\n') : "Default ruleset: B3/S23 \n") +
                (comments != null ? ("\nComments: \n" + comments) : "");

    }

    /**
     * Generates a GameBoard object from the patterns cell data.
     * The method adds a border of dead cells around the pattern.
     * @return a GameBoard of the type GameBoardDynamic
     */
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