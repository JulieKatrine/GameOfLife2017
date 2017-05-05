package model.patternIO;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is a parser implementation for .rle files.
 * Its parse() method reads data from a Reader and returns a {@link Pattern} object.
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 * @see Parser
 * @see FileFormat
 * @see PatternLoader
 */
public class RLEParser implements Parser
{
    private final int INVALID = -1;
    private final char DEAD_CELL = 'b';
    private final char LIVING_CELL = 'o';
    private final char END_OF_ROW = '$';
    private final char END_OF_DATA = '!';

    private List<String> metadata;
    private int width = INVALID;
    private int height = INVALID;
    private boolean[][] boardData;
    private String rule = "";

    public RLEParser()
    {
        metadata = new ArrayList<>();
    }

    /**
     * Reads a Reader object character-by-character and returns the parsed data in a Pattern object.
     * @param reader A Reader object of type FileReader, InputStreamReader, etc.
     * @return A Pattern object.
     * @throws IOException If a problem occurs while reading the file content.
     * @throws PatternFormatException If the format of the file don't match the RLE standard.
     */
    public Pattern parse(Reader reader) throws IOException, PatternFormatException
    {
        int character;
        while ((character = reader.read()) != INVALID)
        {
            if (character == '#')
                readMetadata(reader);
            else if (character == 'x')
                readBoardDefinitionAndInstantiateBoardDataArray(reader);

            if (boardData != null)
                readCellData(reader);
        }

        if (boardData != null)
            return createPattern();
        else
            throw new PatternFormatException(PatternFormatException.ErrorCode.GENERAL_LOADING_ERROR);
    }

    /**
     * Reads the rest of the line and adds it to the metadata list.
     * @param reader The reader to read the data from.
     * @throws IOException If reading from the reader fails.
     */
    private void readMetadata(Reader reader) throws IOException
    {
        int character;
        StringBuilder dataBuilder = new StringBuilder();

        while ((character = reader.read()) != INVALID && character != '\n')
            dataBuilder.append((char) character);

        if(dataBuilder.toString().length() > 0)
            metadata.add(dataBuilder.toString());
    }

    /**
     * Reads the board definition line (x = n, y = m ...) and instantiates
     * the boardData array if the dimensions are loaded correctly.
     * @param reader The reader to read the data from.
     * @throws IOException If the reading fails.
     * @throws PatternFormatException If it fails to load the pattern dimensions.
     */
    private void readBoardDefinitionAndInstantiateBoardDataArray(Reader reader)
            throws IOException, PatternFormatException
    {
        int character;
        int number = 0;
        while ((character = reader.read()) != INVALID && character != '\n')
        {
            if (height == INVALID)
            {
                if (Character.isDigit((char) character))
                {
                    number = number * 10 + (character - '0');
                }
                else if (number != 0)
                {
                    if (width == INVALID)
                        width = number;
                    else
                        height = number;

                    number = 0;
                }
            }
            else if (rule.length() > 0 ||
                    Character.toUpperCase(character) == 'B' ||
                    Character.toUpperCase(character) == 'S' ||
                    Character.isDigit((char) character))
                rule += (char)character;
        }

        if(width == INVALID || height == INVALID)
            throw new PatternFormatException(PatternFormatException.ErrorCode.PATTERN_SIZE_NOT_DEFINED);

        boardData = new boolean[height][width];
    }

    /**
     * Parses the cell data character-by-character and adds it to the boardData array.
     * @param reader The reader to read the data from.
     * @throws IOException If the reading fails.
     * @throws PatternFormatException If the cell data is defined outside of the given dimensions.
     */
    private void readCellData(Reader reader) throws IOException, PatternFormatException
    {
        int row = 0;
        int index = 0;
        int number = 0;
        int character;

        while ((character = reader.read()) != INVALID && character != END_OF_DATA)
        {
            if (Character.isDigit((char) character))
            {
                number = number * 10 + (character - '0');
            }
            else if (character == LIVING_CELL)
            {
                number = Math.max(1, number);

                if(index + number > boardData[0].length)
                    throw new PatternFormatException(PatternFormatException.ErrorCode.ERROR_IN_CELL_DATA);

                for (int x = index; x < (index + number); x++)
                    boardData[row][x] = true;

                index += number;
                number = 0;
            }
            else if(character == DEAD_CELL)
            {
                index += Math.max(1, number);
                number = 0;
            }
            else if (character == END_OF_ROW)
            {
                row += Math.max(1, number);
                index = number = 0;
            }
        }
    }

    /**
     * Creates a new Pattern object from the parsed data.
     * @return A Pattern object.
     * @throws PatternFormatException If the rule is in a unknown format.
     */
    private Pattern createPattern() throws PatternFormatException
    {
        Pattern p = new Pattern();
        p.setMetadata(metadata);
        p.setCellData(boardData);
        p.setRuleString(RuleStringFormatter.format(rule));
        return p;
    }
}
