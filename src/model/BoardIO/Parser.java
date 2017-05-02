package model.BoardIO;

import java.io.IOException;
import java.io.Reader;

 /**
  * This interface enables different parsers implementations for different file formats.
  *
  * @author Niklas Johansen
  * @author Julie Katrine Høvik
  * @see RLEParser
  * @see LIFEParser
  */
public interface Parser
{
    /**
     * Parses the content of a Reader and returns a {@link Pattern}.
     * @param reader A Reader.
     * @return A new Pattern object containing the board data.
     * @throws IOException If reading the file content fails.
     * @throws PatternFormatException If parsing the data fails.
     */
    Pattern parse(Reader reader) throws IOException, PatternFormatException;
}
