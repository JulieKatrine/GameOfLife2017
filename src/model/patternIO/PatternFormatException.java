package model.patternIO;

import java.util.Arrays;

/**
 * The exception thrown when loading or exporting a {@link Pattern} fails.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 * @see PatternLoader
 * @see Parser
 */
public class PatternFormatException extends Exception
{
    public enum ErrorCode
    {
        FORMAT_VERSION_NOT_SPECIFIED,
        FILE_FORMAT_NOT_SUPPORTED,
        PATTERN_SIZE_NOT_DEFINED,
        GENERAL_LOADING_ERROR,
        UNKNOWN_RULE_FORMAT,
        ERROR_IN_CELL_DATA,
        WRONG_PATH_PREFIX,
        NO_LIVING_CELLS,
        NOT_SPECIFIED
    }

    private ErrorCode errorCode = ErrorCode.NOT_SPECIFIED;

    public PatternFormatException(ErrorCode code)
    {
        this.errorCode = code;
    }

    public String getErrorMessage()
    {
        switch (errorCode)
        {
            case GENERAL_LOADING_ERROR:
                return "Failed to load the pattern.\nThe file may be missing important data or have bad internal formatting.";

            case ERROR_IN_CELL_DATA:
                return "Failed to load the pattern.\nAn error was discovered in the file content.";

            case PATTERN_SIZE_NOT_DEFINED:
                return "Failed to load the pattern.\nInformation about the pattern size is missing or corrupted.";

            case UNKNOWN_RULE_FORMAT:
                return "Failed to load the pattern.\nThe supplied rule has an unknown format.";

            case FORMAT_VERSION_NOT_SPECIFIED:
                return "Failed to load the pattern.\nThe format version is not specified in the file.";

            case NO_LIVING_CELLS:
                return "This pattern contains no living cells.";

            case FILE_FORMAT_NOT_SUPPORTED:
                return "This format is not yet supported.\nTry loading a file in any of these formats: " +
                        Arrays.toString(FileFormat.getFileFormats());

            default:
                return "Failed to load the pattern.";
        }
    }

    public ErrorCode getErrorCode()
    {
        return errorCode;
    }
}
