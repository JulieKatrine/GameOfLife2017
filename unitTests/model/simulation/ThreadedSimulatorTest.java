package model.simulation;

import model.BoardIO.PatternFormatException;
import model.BoardIO.PatternLoader;
import model.GameBoard;
import model.TestUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ThreadedSimulatorTest
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
        GameBoard board = pLoader.load(new File("patterns/period6oscillators.rle")).getGameBoard();

        String expected = TestUtils.gameBoardToString(board);

        Simulator simulator = new ThreadedSimulator(new DefaultRuleSet());
        for(int i = 0; i < 6 * 1000; i++)
            simulator.executeOn(board);

        String actual = TestUtils.gameBoardToString(board);

        assertEquals(actual, expected);
    }
}