package model.patternIO;

/**
 * This enum consists of the applications supported file formats.
 * Every type entry is associated with a specific {@link Parser} implementation.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 * @see RLEParser
 * @see LIFEParser
 */

public enum FileFormat
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
    public static String[] getFileFormats()
    {
        String[] types = new String[FileFormat.values().length];

        for(int i=0; i < types.length; i++)
            types[i] = "*." + FileFormat.values()[i].name();

        return types;
    }
}
