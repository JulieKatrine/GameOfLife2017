package model.BoardIO;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
/**
 * Parses a RLE file.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 */
public class RLEParser implements Parser
{
    private final int INVALID = -1;
    private final char DEAD_CELL = 'b';
    private final char LIVING_CELL = 'o';
    private final char END_OF_LINE = '$';

    private ArrayList<String> comments;
    private int width = INVALID;
    private int height = INVALID;
    private boolean[][] boardData;
    private String rule = "";

    public RLEParser()
    {
        comments = new ArrayList<String>();
    }

    public Pattern parse(Reader reader) throws IOException, PatternFormatException
    {
        int character;

        while ((character = reader.read()) != INVALID)
        {
            if (character == '#')
                readComment(reader);
            else if (character == 'x')
                readBoardDefinition(reader);

            if (boardData != null)
                readCellData(reader);
        }

        if (boardData != null)
            return createPattern();
        else
            throw new PatternFormatException("Failed to load board");
    }

    public void readComment(Reader reader) throws IOException
    {
        int character;
        StringBuilder comment = new StringBuilder();

        while ((character = reader.read()) != INVALID && character != '\n')
            comment.append((char) character);

        if(comment.toString().length() > 0)
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
            boardData = new boolean[height][width];
    }

    private void readCellData(Reader reader) throws IOException, PatternFormatException
    {
        int character;
        int number = 0;
        int row = 0;
        int index = 0;

        while ((character = reader.read()) != INVALID && character != '!')
        {
            if (Character.isDigit((char) character))
                number = number * 10 + (character - '0');

            else if (character == DEAD_CELL || character == LIVING_CELL)
            {
                number = Math.max(1, number);

                for (int x = index; x < (index+number); x++)
                    boardData[row][x] = (character == LIVING_CELL);

                index += number;
                number = 0;
            }
            else if (character == END_OF_LINE)
            {
                row += number > 0 ? number : 1;
                index = number = 0;
            }
        }
    }

    private Pattern createPattern()
    {
        Pattern p = new Pattern();
        p.setComments(comments);
        p.setCellData(boardData);
        return p;
    }
}
