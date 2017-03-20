package model.BoardIO;

/**
 * Holds the exceptions of the program.
 *
 * @author Niklas Johansen
 * @auther Julie Katrine Høvik
 */
public class PatternFormatException extends Exception
{
    /**
     * The exception thrown when a file or URL is imported.
     *
     * @param message
     */
    public PatternFormatException(String message)
    {
        super(message);
    }
}
