package model.BoardIO;

import java.io.IOException;
import java.io.Reader;

 /**
  * Parses a file or URL.
  * 
  * @author Niklas Johansen
  * @author Julie Katrine Høvik
  */
public interface Parser
{
    Pattern parse(Reader reader) throws IOException, PatternFormatException;
}
