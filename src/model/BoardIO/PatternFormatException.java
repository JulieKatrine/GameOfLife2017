package model.BoardIO;

/**
 * The exception thrown when a file parsers fails to load a pattern.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 * @see Parser
 */
public class PatternFormatException extends Exception
{
    /**
     * @param message The error message supplied when the exception is thrown.
     */
    public PatternFormatException(String message)
    {
        super(message);
    }
}
