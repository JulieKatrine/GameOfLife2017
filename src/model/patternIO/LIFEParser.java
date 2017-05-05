package model.patternIO;

import model.Point;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import static model.simulation.SimulationRule.DEFAULT_RULE_STRING;

/**
 * This class is a parser implementation for .life and .lif files.
 * It reads data from a supplied Reader object  and returns a {@link Pattern} object.
 * The parser supports Life 1.05, Life 1.06 and XLife 2.0.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 * @see Parser
 * @see FileFormat
 * @see PatternLoader
 */
public class LIFEParser implements Parser
{
    private List<String> metadata;
    private String format;
    private String rule = DEFAULT_RULE_STRING;
    private boolean[][] boardData;

    public LIFEParser()
    {
        metadata = new ArrayList<>();
    }

    /**
     * Reads the reader object and returns a new {@link Pattern} from the parsed data.
     * @param reader A Reader object of type FileReader, InputStreamReader, etc.
     * @return A Pattern object.
     * @throws IOException If a problem occurs while reading the file content.
     * @throws PatternFormatException If parsing the file content fails.
     */
    @Override
    public Pattern parse(Reader reader) throws IOException, PatternFormatException
    {
        BufferedReader bufferedReader = new BufferedReader(reader);

        String line;
        while ((line = bufferedReader.readLine()) != null)
        {
            if (line.length() > 2)
            {
                char prefix = Character.toUpperCase(line.charAt(1));
                String cleanLine = line.substring(2).trim();

                if (prefix == 'D' || prefix == 'C' || prefix == 'O')
                {
                    metadata.add(line);
                }
                else if (prefix == 'L')
                {
                    format = cleanLine;
                    if (format.contains("1.06"))
                        parse106BoardData(bufferedReader);
                }
                else if (prefix == 'R')
                {
                    rule = RuleStringFormatter.format(cleanLine);
                }
                else if (prefix == 'P')
                {
                    if(format == null)
                        throw new PatternFormatException(PatternFormatException.ErrorCode.FORMAT_VERSION_NOT_SPECIFIED);

                    if (format.contains("1.05"))
                        parse105BoardData(bufferedReader, line);
                }
            }
        }

        if (boardData == null)
            throw new PatternFormatException(PatternFormatException.ErrorCode.GENERAL_LOADING_ERROR);

        return createPattern();
    }

    /**
     * Parses the board data from a Life 1.05 formatted file.
     * @param reader The reader to read the data from.
     * @param firstBlockStartLine The first #P line that is read.
     * @throws IOException If reading from the reader fails.
     * @throws PatternFormatException If parsing the file content fails.
     */
    private void parse105BoardData(BufferedReader reader, String firstBlockStartLine) throws IOException, PatternFormatException
    {
        List<Point> cellBlockStartPoints = new ArrayList<>();
        List<String> lines = new ArrayList<>();
        lines.add(firstBlockStartLine);

        // Add all proceeding lines to the line list
        for(String line; (line = reader.readLine()) != null;)
            if(!line.isEmpty())
                lines.add(line);

        int maxX = 0, minX = 0, maxY = 0, minY = 0, rowCount = 1;
        Point blockStartPoint = new Point();
        for(String line : lines)
        {
            if(line.charAt(0) == '#')
            {
                // Get the block staring point and save it
                blockStartPoint = getPointFromStringNumbers(line.substring(2).trim().split(" "));
                cellBlockStartPoints.add(blockStartPoint);

                // Find the minimum x and y points
                minX = Math.min(minX, blockStartPoint.x);
                minY = Math.min(minY, blockStartPoint.y);

                // Reset the row count
                rowCount = 1;
            }
            else
            {
                // Find the maximum x and y points
                maxX = Math.max(maxX, blockStartPoint.x + line.length());
                maxY = Math.max(maxY, blockStartPoint.y + (rowCount++));
            }
        }

        // Calculate the width and height from the max and min points
        int width = maxX - minX;
        int height = maxY - minY;

        // Throw a PatternFormatException if the size is not set correctly
        if(width < 1 || height < 1)
            throw new PatternFormatException(PatternFormatException.ErrorCode.PATTERN_SIZE_NOT_DEFINED);

        // Instantiates the array with the calculated sizes
        boardData = new boolean[height][width];

        int rowIndex = 0, cellBlockCount = 0, startX = 0, startY = 0;
        for(String line : lines)
        {
            if(line.charAt(0) == '#')
            {
                // Gets the start point and calculates the position in the array
                Point start = cellBlockStartPoints.get(cellBlockCount++);
                startX = (start.x < 0) ? Math.abs(start.x - minX) : Math.abs(minX) + start.x;
                startY = (start.y < 0) ? Math.abs(start.y - minY) : Math.abs(minY) + start.y;
                rowIndex = 0;
            }
            else
            {
                // Throws an exception if the cell data is defined outside of the given array dimensions.
                if(startX + line.length() > boardData[0].length || startY + rowIndex >= boardData.length)
                    throw new PatternFormatException(PatternFormatException.ErrorCode.ERROR_IN_CELL_DATA);

                // Adds the cell data to the array
                for(int i = 0; i < line.length(); i++)
                    boardData[startY + rowIndex][startX + i] = (line.charAt(i) == '*');

                rowIndex++;
            }
        }
    }

    /**
     * Parses the board data from a Life 1.06 formatted file.
     * @param reader The reader to read the data from.
     * @throws IOException If reading from the reader fails.
     * @throws PatternFormatException If it fails to load the board dimensions.
     */
    private void parse106BoardData(BufferedReader reader) throws IOException, PatternFormatException
    {
        List<Point> points = new ArrayList<>();

        // Reads all the lines and adds the numbers to the point list.
        for(String line; (line = reader.readLine()) != null;)
            if(!line.isEmpty())
                points.add(getPointFromStringNumbers(line.trim().split(" ")));

        // Finds the max and min points
        int maxX = 0, minX = 0, maxY = 0, minY = 0;
        for(Point p : points)
        {
            minX = Math.min(minX, p.x);
            minY = Math.min(minY, p.y );
            maxX = Math.max(maxX, p.x + 1);
            maxY = Math.max(maxY, p.y + 1);
        }

        // Calculates the width and height from the max and min points
        int width = maxX - minX;
        int height = maxY - minY;

        // Throws a PatternFormatException if the size is not set correctly
        if(width < 1 || height < 1)
            throw new PatternFormatException(PatternFormatException.ErrorCode.PATTERN_SIZE_NOT_DEFINED);

        // Instantiates the array with the calculated sizes
        boardData = new boolean[height][width];

        // Sets the point in the array representing zero in the Life universe
        int zeroX = Math.abs(minX);
        int zeroY = Math.abs(minY);

        // Adds the point data to the array
        points.forEach(p -> boardData[zeroY + p.y][zeroX + p.x] = true);
    }

    /**
     * Parses the strings in the array and creates a new Point object
     * @param numbers A string array containing two text numbers.
     * @return A Point object
     * @throws PatternFormatException If it fails to parse the two numbers.
     */
    private Point getPointFromStringNumbers(String[] numbers) throws PatternFormatException
    {
        try
        {
            return new Point(Integer.parseInt(numbers[0]), Integer.parseInt(numbers[1]));
        }
        catch (NumberFormatException | NullPointerException | IndexOutOfBoundsException e)
        {
            throw new PatternFormatException(PatternFormatException.ErrorCode.ERROR_IN_CELL_DATA);
        }
    }

    /**
     * Creates a new Pattern object from the parsed data.
     * @return A Pattern object.
     */
    private Pattern createPattern()
    {
        Pattern p = new Pattern();
        p.setMetadata(metadata);
        p.setCellData(boardData);
        p.setRuleString(rule);
        return p;
    }
}
