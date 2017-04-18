package model.BoardIO;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

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
                readBoardDefinitionAndInstantiateBoardDataArray(reader);

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

    private void readBoardDefinitionAndInstantiateBoardDataArray(Reader reader) throws IOException
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
            else if (rule.length() > 0 ||
                    Character.toUpperCase(character) == 'B' ||
                    Character.toUpperCase(character) == 'S' ||
                    Character.isDigit((char) character))
                rule += (char)character;
        }
        if (width != INVALID && height != INVALID){
            boardData = new boolean[height][width];
        }

    }

    private void readCellData(Reader reader) throws IOException, PatternFormatException
    {
        int character;
        int number = 0;
        int row = 0;
        int index = 0;

        while ((character = reader.read()) != INVALID && character != END_OF_DATA)
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
            else if (character == END_OF_ROW)
            {
                row += Math.max(1, number);
                index = number = 0;
            }
        }
    }

    private Pattern createPattern()
    {
        Pattern p = new Pattern();
        p.setMetadata(metadata);
        p.setCellData(boardData);
        p.setRuleString(getRuleInStandardFormat(rule));
        return p;
    }

    private String getRuleInStandardFormat(String rule)
    {
        rule = rule.trim().replaceAll(" ", "").toUpperCase();
        int indexOfB = rule.indexOf("B");
        int indexOfS = rule.indexOf("S");

        String birthNumbers = "";
        String survivalNumbers = "";

        if(indexOfB == -1 && indexOfS == -1)
            return "B" + rule.replaceAll("/", "/S");

        if(indexOfB != -1)
            for(indexOfB +=1; (indexOfB < rule.length() && Character.isDigit(rule.charAt(indexOfB))); indexOfB++)
                birthNumbers += rule.charAt(indexOfB);

        if(indexOfS != -1)
            for(indexOfS +=1; (indexOfS < rule.length() && Character.isDigit(rule.charAt(indexOfS))); indexOfS++)
                survivalNumbers += rule.charAt(indexOfS);

        return "B" + birthNumbers + "/S" + survivalNumbers;
    }
}
