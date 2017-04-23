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
 * @see Pattern
 */
public class PatternLoader
{
    /**
     * Loads a file from a prefixed path and returns a Pattern object.
     * The supported prefixes ar FILE:, STREAM:, and URL:.
     *
     * @param path The prefixed path.
     * @return A Pattern object parsed from the specified file.
     * @throws IOException If a problem occurs while reading the file content.
     * @throws PatternFormatException If a problem occurs while parsing the file.
     */
    public Pattern loadFromPrefixedPath(String path) throws IOException, PatternFormatException
    {
        if(path.startsWith("FILE:"))
            return loadFile(new File(path.substring(5)));
        else if (path.startsWith("URL:"))
            return loadURL(path.substring(4));
        else if(path.startsWith("STREAM:"))
            return loadAsStream(path.substring(7));
        else
            throw new PatternFormatException(PatternFormatException.ErrorCode.WRONG_PATH_PREFIX);
    }

    /**
     * Loads a file form the local machine and returns a Pattern object.
     *
     * @param file The file object to be loaded.
     * @return A Pattern object parsed from the specified file.
     * @throws IOException If a problem occurs while reading the file content.
     * @throws PatternFormatException If a problem occurs while parsing the file.
     */
    public Pattern loadFile(File file) throws IOException, PatternFormatException
    {
        return loadPattern(new FileReader(file), "FILE:" + file.getPath());
    }

    /**
     * Loads a file as a stream and returns a Pattern object.
     * This method is used for loading patterns from the project directory or from
     * packed JAR files.
     *
     * @param path The path to the file.
     * @return A Pattern object parsed from the specified file.
     * @throws IOException If a problem occurs while reading the file content.
     * @throws PatternFormatException If a problem occurs while parsing the file.
     */
    public Pattern loadAsStream(String path) throws IOException, PatternFormatException
    {
        return loadPattern(new InputStreamReader(getClass().getResourceAsStream(path)), "STREAM:" + path);
    }

    /**
     * Loads a file form a web address and returns a Pattern object.
     *
     * @param url The web address to the file.
     * @return A Pattern object parsed from the specified file.
     * @throws IOException If a problem occurs while reading the file content.
     * @throws PatternFormatException If a problem occurs while parsing the file.
     */
    public Pattern loadURL(String url) throws IOException, PatternFormatException
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
            try
            {
                Pattern pattern = parser.parse(reader);
                pattern.setOrigin(path);
                return  pattern;
            }
            catch(PatternFormatException e)
            {
                // Close the reader before passing on the exception
                reader.close();
                throw e;
            }
        }
        else
            throw new PatternFormatException(PatternFormatException.ErrorCode.FILE_FORMAT_NOT_SUPPORTED);
    }

    public static String extractFileType(String fileName)
    {
        int dotPosition = fileName.lastIndexOf('.') + 1;
        if (dotPosition > 0 && dotPosition < fileName.length())
            return fileName.substring(dotPosition).toLowerCase();
        else
            return "";
    }
}










