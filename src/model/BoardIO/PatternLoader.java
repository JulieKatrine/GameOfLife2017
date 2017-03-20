package model.BoardIO;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
/**
 * Loads a pattern from disk or URL.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 */
public class PatternLoader
{
    /**
     * Loads a file with a pattern from the disk.
     *
     * @param file
     * @return a Pattern
     * @throws IOException
     * @throws PatternFormatException
     */
    public Pattern loadFromDisk(File file) throws IOException, PatternFormatException
    {
        return loadPattern(new FileReader(file), extractFileType(file.getName()));
    }

    /**
     * Loads a URL with a pattern.
     *
     * @param url
     * @return a Pattern
     * @throws IOException
     * @throws PatternFormatException
     */
    public Pattern loadFromURL(String url) throws IOException, PatternFormatException
    {
        URL destination = new URL(url);
        URLConnection urlConnection = destination.openConnection();
        return loadPattern(new InputStreamReader(urlConnection.getInputStream()), extractFileType(url));
    }

    private String extractFileType(String fileName)
    {
        int dotPosition = fileName.lastIndexOf('.') + 1;
        if (dotPosition > 0 && dotPosition < fileName.length())
            return fileName.substring(dotPosition);
        else
            return "";
    }

    /**
     * TODO: Fill in here.
     */
    public enum FileType
    {
        rle { Parser getParser()
                { return new RLEParser();
                }
            },
        fil { Parser getParser()
                { return new RLEParser();
                }
            };
        abstract Parser getParser();
    }

    private Pattern loadPattern(Reader reader, String fileType) throws IOException, PatternFormatException
    {
        for(FileType t : FileType.values())
            if(fileType.equals(t.name()))
                return t.getParser().parse(reader);

        throw new PatternFormatException("." + fileType + " files is currently not supported");
    }
}










