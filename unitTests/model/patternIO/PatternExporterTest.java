package model.patternIO;

import model.TestUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PatternExporterTest
{
    @Test
    void export() throws IOException, PatternFormatException
    {
        Pattern testPattern = new Pattern();
        testPattern.setCellData(new boolean[][] {{false,true,false},{true,false,true},{false,true,false}});
        testPattern.setMetadata(Arrays.asList("N TestPattern","O Author", "C Comment"));
        testPattern.setRuleString("B3/S23");

        File outputFile = new File("test.rle");
        outputFile.createNewFile();

        PatternExporter exporter = new PatternExporter();
        exporter.export(testPattern, outputFile);

        RLEParser rleParser = new RLEParser();
        Pattern loadedPattern = rleParser.parse(new FileReader(outputFile));

        outputFile.delete();

        assertEquals("010101010", TestUtils.trimmedGameBoardToString(loadedPattern.getGameBoard()));
        assertEquals("TestPattern", loadedPattern.getName());
        assertEquals("Author", loadedPattern.getAuthor());
        assertEquals("Comment\n", loadedPattern.getComments());
        assertEquals("B3/S23", loadedPattern.getRuleString());
    }
}