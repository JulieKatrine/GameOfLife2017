package model.BoardIO;

import java.io.*;

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

        addMetadata(sBuilder, "#N ", pattern.getName());
        addMetadata(sBuilder, "#O ", pattern.getAuthor());

        for(String comment : pattern.getComments().split("\n"))
            addMetadata(sBuilder, "#C ", comment);

        addBoardSizeAndRule(sBuilder, pattern.getCellData(), pattern.getRuleString());

        sBuilder.append(getCellDataAsString(pattern.getCellData()));

        FileWriter writer = new FileWriter(destinationFile);
        writer.write(sBuilder.toString());
        writer.close();
    }

    private void addMetadata(StringBuilder sb, String prefix, String data)
    {
        if(data != null && data.length() > 0)
            sb.append(prefix + data + "\n");
    }

    private void addBoardSizeAndRule(StringBuilder sb, boolean[][] data, String rule)
    {
        if(rule == null || rule.length() == 0)
            rule = "B3/S23";

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
            if((count == rowLength && !cellState) == false)
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
