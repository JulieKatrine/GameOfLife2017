package model.simulation;

import model.BoardIO.PatternFormatException;
import model.BoardIO.PatternLoader;
import model.GameBoard;
import model.TestUtilities;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by Niklas on 09.04.2017.
 */
class ThreadedSimulatorTest
{
    @Test
    void executeOn() throws IOException, PatternFormatException
    {
        PatternLoader pLoader = new PatternLoader();
        GameBoard board = pLoader.load(new File("patterns/period6oscillators.rle")).getGameBoard();

        String expected = TestUtilities.gameBoardToString(board);

        Simulator simulator = new ThreadedSimulator(new DefaultRuleSet());
        for(int i=0; i < 6 * 100; i++)
            simulator.executeOn(board);

        String actual = TestUtilities.gameBoardToString(board);

        assertEquals(actual, expected);
    }
}