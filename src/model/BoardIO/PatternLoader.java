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
    public Pattern loadFromDisk(File file) throws IOException, PatternFormatException
    {
        return loadPattern(new FileReader(file), extractFileType(file.getName()));
    }

    /**
     * Loads a file form the local machine and returns a Pattern object.
     *
     * @param url The web address to the file.
     * @return A Pattern object parsed from the specified file.
     * @throws IOException If a problem occurs while reading the file content.
     * @throws PatternFormatException If a problem occurs while parsing the file.
     * @see Pattern
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

    private Pattern loadPattern(Reader reader, String fileType) throws IOException, PatternFormatException
    {
        for(FileType t : FileType.values())
            if(fileType.equals(t.name()))
                return t.getParser().parse(reader);

        throw new PatternFormatException("." + fileType + " files is currently not supported");
    }
}










