package model.simulation;


import model.BoardIO.Pattern;
import model.BoardIO.PatternFormatException;
import model.BoardIO.PatternLoader;
import model.GameBoard;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class SimulatorPreformanceTest
{
    @Test
    @Ignore
    void testExecutionSpeed() throws IOException, PatternFormatException
    {
        int generations = 500;
        String patternPath = "/patterns/turingmachine.rle";
        PatternLoader loader = new PatternLoader();
        Pattern pattern = loader.loadAsStream(patternPath);

        // ----------------------------- SimulatorImpl -----------------------------

        GameBoard board = pattern.getGameBoard();
        Simulator simulator = new SimulatorImpl(new DefaultRuleSet());
        long simulatorTime = 0;
        for(int i = 0; i < generations; i++)
        {
            simulator.simulateNextGenerationOn(board);
            simulatorTime +=  simulator.getSimulationTime();
        }

        // ----------------------------- SimulatorThreaded -----------------------------

        board = pattern.getGameBoard();
        simulator = new SimulatorThreaded(new DefaultRuleSet());
        long simulatorThreadedTime = 0;
        for(int i = 0; i < generations; i++)
        {
            simulator.simulateNextGenerationOn(board);
            simulatorThreadedTime +=  simulator.getSimulationTime();
        }

        // ----------------------------- Results -----------------------------

        System.out.println(generations + " generations of " + patternPath + "\n" +
                " - SimulatorImpl:      " + simulatorTime + " ms\n" +
                " - SimulatorThreaded:  " + simulatorThreadedTime + " ms\n");
    }
}
