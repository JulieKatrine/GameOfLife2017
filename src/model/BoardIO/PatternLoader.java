package model.BoardIO;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * This class consists of methods for loading patterns from files.
 * A file can be obtained through a web address or machine local path.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 */
public class PatternLoader
{
    /**
     * Loads a file form the local machine and returns a Pattern object.
     *
     * @param file The file object to be loaded.
     * @return A Pattern object parsed from the specified file.
     * @throws IOException If a problem occurs while reading the file content.
     * @throws PatternFormatException If a problem occurs while parsing the file.
     * @see Pattern
     */
    public Pattern load(File file) throws IOException, PatternFormatException
    {
        return loadPattern(new FileReader(file), "FILE:" + file.getPath());
    }

    /**
     * Loads a file form a web address and returns a Pattern object.
     *
     * @param url The web address to the file.
     * @return A Pattern object parsed from the specified file.
     * @throws IOException If a problem occurs while reading the file content.
     * @throws PatternFormatException If a problem occurs while parsing the file.
     * @see Pattern
     */
    public Pattern load(String url) throws IOException, PatternFormatException
    {
        URL destination = new URL(url);
        URLConnection urlConnection = destination.openConnection();
        return loadPattern(new InputStreamReader(urlConnection.getInputStream()), "URL:" + url);
    }

    private Pattern loadPattern(Reader reader, String path) throws IOException, PatternFormatException
    {
        String fileType = extractFileType(path);
        Parser parser = null;

        for(FileType t : FileType.values())
            if(fileType.equals(t.name()))
                parser = t.getParser();

        if(parser != null)
        {
            Pattern pattern = parser.parse(reader);
            pattern.setOrigin(path);
            return  pattern;
        }
        else
            throw new PatternFormatException("." + fileType + " files is currently not supported");
    }

    private String extractFileType(String fileName)
    {
        int dotPosition = fileName.lastIndexOf('.') + 1;
        if (dotPosition > 0 && dotPosition < fileName.length())
            return fileName.substring(dotPosition).toLowerCase();
        else
            return "";
    }

}










