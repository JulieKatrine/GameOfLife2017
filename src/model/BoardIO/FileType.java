package model.BoardIO;

/**
 * This enum consists of the applications supported file types.
 * Every type entry is associated with a specific parser implementation.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 */

public enum FileType
{
    rle  { Parser getParser() { return new RLEParser();}},
    lif  { Parser getParser() { return new LIFEParser();}},
    life { Parser getParser() { return new LIFEParser();}};

    /**
     * @return The associated parser for the specified file type.
     * @see Parser
     */
    abstract Parser getParser();

    /**
     * @return A String array with every supported file on the form "*.type".
     */
    public static String[] getFileTypes()
    {
        String[] types = new String[FileType.values().length];

        for(int i=0; i < types.length; i++)
            types[i] = "*." + FileType.values()[i].name();

        return types;
    }
}
