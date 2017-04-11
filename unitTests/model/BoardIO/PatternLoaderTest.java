package model.BoardIO;

import model.TestUtils;
import model.simulation.DefaultRuleSet;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PatternLoaderTest
{
    @Test
    void testRLEFileLoading() throws IOException, PatternFormatException
    {
        PatternLoader loader = new PatternLoader();
        Pattern pattern  = loader.load(new File("patterns/testPattern.rle"));

        String cellData = TestUtils.gameBoardToString(pattern.getGameBoard());

        assertEquals("1100101101111110000010000",      cellData,            "Test cell data");
        assertEquals("TestPattern",                    pattern.getName(),   "Test name");
        assertEquals("FILE:patterns\\testPattern.rle", pattern.getOrigin(), "Test origin");
        assertEquals(DefaultRuleSet.class,                      pattern.getRule().getClass(),"Test rule");
    }

    @Test
    void testNonSupportedFileFormat()
    {
        assertThrows(PatternFormatException.class, () ->
        {
            PatternLoader loader = new PatternLoader();
            loader.load(new File("patterns/nonSupportedTestPattern.abc"));
        });
    }
}