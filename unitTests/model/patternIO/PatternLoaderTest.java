package model.patternIO;

import model.TestUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PatternLoaderTest
{
    @Test
    void testRLEFormatFileLoading() throws IOException, PatternFormatException
    {
        PatternLoader loader = new PatternLoader();
        Pattern pattern  = loader.loadAsStream("/patterns/boat.rle");
        String cellData = TestUtils.trimmedGameBoardToString(pattern.getGameBoard());

        assertEquals("110101010", cellData);
        assertEquals("Boat", pattern.getName());
        assertEquals("Comment\n", pattern.getComments());
        assertEquals("B3/S23", pattern.getRule().getStringRule());
        assertEquals("STREAM:/patterns/boat.rle", pattern.getOrigin());
    }

    @Test
    void testFILE105Loading() throws IOException, PatternFormatException
    {
        PatternLoader loader = new PatternLoader();
        Pattern pattern  = loader.loadAsStream("/patterns/boat_105.lif");
        String cellData = TestUtils.trimmedGameBoardToString(pattern.getGameBoard());

        assertEquals("110101010", cellData);
        assertEquals("Comment\n", pattern.getComments());
        assertEquals("B3/S23", pattern.getRule().getStringRule());
        assertEquals("STREAM:/patterns/boat_105.lif", pattern.getOrigin());
    }

    @Test
    void testFILE106Loading() throws IOException, PatternFormatException
    {
        PatternLoader loader = new PatternLoader();
        Pattern pattern = loader.loadAsStream("/patterns/boat_106.lif");
        String cellData = TestUtils.trimmedGameBoardToString(pattern.getGameBoard());

        assertEquals("110101010", cellData);
        assertEquals("B3/S23", pattern.getRule().getStringRule());
        assertEquals("STREAM:/patterns/boat_106.lif", pattern.getOrigin());
    }

    @Test
    void testNonSupportedFileFormat()
    {
        PatternFormatException exception = assertThrows(PatternFormatException.class, () ->
        {
            PatternLoader loader = new PatternLoader();
            loader.loadAsStream("/patterns/nonSupportedTestPattern.abc");
        });

        assertEquals(PatternFormatException.ErrorCode.FILE_FORMAT_NOT_SUPPORTED, exception.getErrorCode());
    }
}