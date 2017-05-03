package model.simulation;

import model.patternIO.PatternFormatException;
import model.patternIO.PatternLoader;
import model.GameBoard;
import model.TestUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ThreadedSimulatorImplTest
{
    /**
     * This test simulates 6000 generations of a 6-period pattern.
     * The test will fail if a single synchronization problem occurs while simulating.
     *
     * @throws IOException
     * @throws PatternFormatException
     */
    @Test
    void executeOn() throws IOException, PatternFormatException
    {
        PatternLoader pLoader = new PatternLoader();
        GameBoard board = pLoader.loadAsStream("/patterns/period6oscillators.rle").getGameBoard();

        String expected = TestUtils.trimmedGameBoardToString(board);

        Simulator simulator = new ThreadedSimulatorImpl(new DefaultRule());
        for(int i = 0; i < 6 * 1000; i++)
            simulator.simulateNextGenerationOn(board);

        String actual = TestUtils.trimmedGameBoardToString(board);

        assertEquals(actual, expected);
    }
}