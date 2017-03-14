package model.BoardIO;

import java.io.IOException;
import java.io.Reader;

public interface Parser
{
    Pattern parse(Reader reader) throws IOException, PatternFormatException;
}
