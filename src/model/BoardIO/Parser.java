package model.BoardIO;

import java.io.IOException;
import java.io.Reader;

 /**
  * This class parsers data from a Reader and returns a Pattern object.
  * @see Pattern
  *
  * @author Niklas Johansen
  * @author Julie Katrine Høvik
  */
public interface Parser
{
    Pattern parse(Reader reader) throws IOException, PatternFormatException;
}
