package model.BoardIO;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

/**
 * This class is a parser implementation for .rle files.
 * Its parse() method reads data from a Reader and returns a Pattern object.
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

    private ArrayList<String> metadata;
    private int width = INVALID;
    private int height = INVALID;
    private boolean[][] boardData;
    private String rule = "";

    public RLEParser()
    {
        metadata = new ArrayList<>();
    }

    /**
     * Reads a Reader object character by character and returns the parsed data in a Pattern object.
     *
     * @param reader A Reader object of type FileReader, InputStreamReader, etc.
     * @return A Pattern object.
     * @throws IOException If a problem occurs while reading the file content.
     * @throws PatternFormatException If the format of the file don't match the RLE standard.
     * @see Pattern
     */
    public Pattern parse(Reader reader) throws IOException, PatternFormatException
    {
        int character;

        while ((character = reader.read()) != INVALID)
        {
            if (character == '#')
                readMetadata(reader);
            else if (character == 'x')
                readBoardDefinition(reader);

            if (boardData != null)
                readCellData(reader);
        }

        if (boardData != null)
            return createPattern();
        else
            throw new PatternFormatException("Failed to load pattern");
    }

    private void readMetadata(Reader reader) throws IOException
    {
        int character;
        StringBuilder dataBuilder = new StringBuilder();

        while ((character = reader.read()) != INVALID && character != '\n')
            dataBuilder.append((char) character);

        if(dataBuilder.toString().length() > 0)
            metadata.add(dataBuilder.toString());
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
                rule += (char)character;
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
        p.setMetadata(metadata);
        p.setCellData(boardData);
        return p;
    }
}
