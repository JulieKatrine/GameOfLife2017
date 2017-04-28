package model.BoardIO;

import model.GameBoard;
import model.TestUtils;
import model.simulation.DefaultRuleSet;
import model.simulation.SimulatorImpl;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;


class GIFExporterTest
{
    @Test
    void export() throws IOException
    {
        File outputFile = new File("testGIF.gif");
        outputFile.createNewFile();

        GIFExporter exporter = new GIFExporter(
                outputFile,
                new SimulatorImpl(new DefaultRuleSet()),
                10,
                5,
                5,
                5,
                false);

        GameBoard board = TestUtils.getGameBoardImplementation(5,5);
        TestUtils.addDataToGameBoard(board, new byte[][]
        {
            {0,0,0,0,0},
            {0,0,1,0,0},
            {0,0,1,0,0},
            {0,0,1,0,0},
            {0,0,0,0,0}
        });

        exporter.export(board, 10);
        outputFile.delete();
    }
}