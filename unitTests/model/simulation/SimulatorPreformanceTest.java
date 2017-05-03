package model.simulation;


import model.patternIO.Pattern;
import model.patternIO.PatternFormatException;
import model.patternIO.PatternLoader;
import model.GameBoard;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class SimulatorPreformanceTest
{
    @Test
    void testExecutionSpeed() throws IOException, PatternFormatException
    {
        int generations = 500;
        String patternPath = "/patterns/turingmachine.rle";
        PatternLoader loader = new PatternLoader();
        Pattern pattern = loader.loadAsStream(patternPath);

        // ----------------------------- SimulatorImpl -----------------------------

        GameBoard board = pattern.getGameBoard();
        Simulator simulator = new SimulatorImpl(new DefaultRule());
        long simulatorTime = 0;
        for(int i = 0; i < generations; i++)
        {
            simulator.simulateNextGenerationOn(board);
            simulatorTime +=  simulator.getSimulationTime();
        }

        // ----------------------------- ThreadedSimulatorImpl -----------------------------

        board = pattern.getGameBoard();
        simulator = new ThreadedSimulatorImpl(new DefaultRule());
        long simulatorThreadedTime = 0;
        for(int i = 0; i < generations; i++)
        {
            simulator.simulateNextGenerationOn(board);
            simulatorThreadedTime +=  simulator.getSimulationTime();
        }

        // ----------------------------- Results -----------------------------

        System.out.println(generations + " generations of " + patternPath + "\n" +
                " - SimulatorImpl:      " + simulatorTime + " ms\n" +
                " - ThreadedSimulatorImpl:  " + simulatorThreadedTime + " ms\n");
    }
}
