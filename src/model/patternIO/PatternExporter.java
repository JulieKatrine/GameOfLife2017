package model.patternIO;

import java.io.*;

import static model.simulation.SimulationRule.DEFAULT_RULE_STRING;

/**
 * Handles exporting of a {@link Pattern} object to a .rle file.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 */
public class PatternExporter
{
    /**
     * Formats and writes the data in a given pattern to a file.
     * @param pattern The pattern to be exported.
     * @param destinationFile The output file.
     * @throws IOException If any problems occur while writing to the given file.
     */
    public void export(Pattern pattern, File destinationFile) throws IOException
    {
        StringBuilder sBuilder = new StringBuilder();

        addMetadataLine(sBuilder, "#N ", pattern.getName());
        addMetadataLine(sBuilder, "#O ", pattern.getAuthor());

        addComments(sBuilder, pattern.getComments());
        addBoardSizeAndRule(sBuilder, pattern.getCellData(), pattern.getRuleString());

        sBuilder.append(getCellDataAsString(pattern.getCellData()));

        FileWriter writer = new FileWriter(destinationFile);
        writer.write(sBuilder.toString());
        writer.close();
    }

    private void addComments(StringBuilder sb, String data)
    {
        if(data != null && !data.isEmpty())
            for(String comment : data.split("\n"))
                addMetadataLine(sb, "#C ", comment);
    }

    private void addMetadataLine(StringBuilder sb, String prefix, String data)
    {
        if(data != null && data.length() > 0)
            sb.append(prefix + data + "\n");
    }

    private void addBoardSizeAndRule(StringBuilder sb, boolean[][] data, String rule)
    {
        if(rule == null || rule.isEmpty())
            rule = DEFAULT_RULE_STRING;

        sb.append("x = " + data[0].length + ", y = " + data.length + ", rule = " + rule + "\n");
    }

    private String getCellDataAsString(boolean[][] data)
    {
        if(data == null)
            return "";

        StringBuilder sb = new StringBuilder();
        int rowLength = data[0].length;
        for(int y = 0; y < data.length; y++)
        {
            int count = 0;
            boolean cellState = data[y][0];
            for(int x = 0; x < rowLength; x++)
            {
                if(cellState == data[y][x])
                    count++;
                else
                {
                    sb.append(count == 1 ? "" : count);
                    sb.append(cellState ? 'o' : 'b');
                    cellState = data[y][x];
                    count = 1;
                }
            }

            // If the whole row was empty, only add the $
            if(!(count == rowLength && !cellState))
            {
                sb.append(count == 1 ? "" : count);
                sb.append(cellState ? 'o' : 'b');
            }
            sb.append('$');
        }

        // Replace the last $ with !
        sb.replace(sb.length()-1, sb.length(), "!");

        // Replace multiple occurrences of $ with n$
        for(int i = 0, count = 0; i < sb.length(); i++)
        {
            if(sb.charAt(i) == '$')
                count++;
            else if(count > 1)
            {
                sb.replace(i - count, i, count + "$");
                count = i = 0;
            }
            else count = 0;
        }

        return sb.toString();
    }
}
