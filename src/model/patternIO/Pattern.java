package model.patternIO;

import model.*;
import model.simulation.CustomRule;
import model.simulation.DefaultRule;
import model.simulation.SimulationRule;

import java.util.List;

import static model.simulation.SimulationRule.DEFAULT_RULE_STRING;

/**
 * This class wraps relevant information of a loaded pattern into a convenient object.
 * It supplies getters and setters for this information, as well as a method for generating a {@link GameBoard}
 * from the cell data.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 * @see Parser
 * @see PatternLoader
 */
public class Pattern
{
    private String ruleString;
    private String name;
    private String author;
    private String origin;
    private String comments;
    private boolean[][] cellData;

    /**
     * Adds the loaded metadata to the pattern.
     * The first character in a line determines what kind of metadata it holds.
     * N - Patterns name
     * O - The author of the pattern
     * C - Other information
     * D - Other information
     * @param metadataList A list of metadata lines.
     */
    public void setMetadata(List<String> metadataList)
    {
        StringBuilder commentBuilder = new StringBuilder();
        for(String string : metadataList)
        {
            if(string.length() > 2)
            {
                string = string.replaceAll("#", "");
                char prefix = string.charAt(0);
                String line = string.substring(1).trim();

                switch (prefix)
                {
                    case 'N': name = line;   break;
                    case 'O': author = line; break;
                    case 'C': commentBuilder.append(line + '\n'); break;
                    case 'D': commentBuilder.append(line + '\n'); break;
                }
            }
        }
        comments = commentBuilder.toString();
    }

    public void setCellData(boolean[][] cellData)
    {
        this.cellData = cellData;
    }

    public void setRuleString(String ruleString)
    {
        this.ruleString = ruleString;
    }

    public void setOrigin(String origin)
    {
        this.origin = origin;
    }

    public String getName()
    {
        return name;
    }

    public String getOrigin()
    {
        return origin;
    }

    public String getComments()
    {
        return comments;
    }

    public String getAuthor()
    {
        return author;
    }

    public boolean[][] getCellData()
    {
        return cellData;
    }

    public String getRuleString()
    {
        return ruleString;
    }

    /**
     * Returns a SimulationRule object from the patterns rule string.
     * @return A new SimulationRule
     * @see CustomRule
     * @see DefaultRule
     */
    public SimulationRule getRule()
    {
        if(ruleString != null && !ruleString.equals(DEFAULT_RULE_STRING))
            return new CustomRule(ruleString);
        else
            return new DefaultRule();
    }

    /**
     * @return A String containing the pattern name, author and additional information.
     */
    public String getAllMetadata()
    {
        return (name != null ? ("Name: " + name + '\n') : "")  +
                (author != null ? ("Author: " + author + '\n') : "") +
                (ruleString != null ? ("Rule: " + ruleString + '\n') : "Default ruleset: " + DEFAULT_RULE_STRING + "\n") +
                "Size: " + cellData[0].length + " x " + cellData.length + "\n" +
                (!comments.isEmpty() ? ("\nComments: \n" + comments) : "");
    }

    /**
     * Generates a {@link GameBoard} object from the patterns cell data.
     * The method adds a border of dead cells around the pattern.
     * @return A GameBoard of the type {@link GameBoardDynamic}.
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