package model;

import model.BoardIO.Pattern;
import model.BoardIO.PatternFormatException;
import model.BoardIO.PatternLoader;
import model.simulation.DefaultRuleSet;
import model.simulation.Simulator;
import model.simulation.SimulatorThreaded;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class GameBoardPerformanceTest
{

    @Test
    @Ignore
    void testPerformanceOnBoardWithStaticSize() throws IOException, PatternFormatException
    {
        System.out.println("No change in board size:");
        runTestOnThisBoard("/patterns/turingmachine.rle", 50);
    }


    @Test
    @Ignore
    void testPerformanceOnBoardWhichChangesSize() throws IOException, PatternFormatException
    {
        System.out.println("Board size increases throughout simulation:");
        runTestOnThisBoard("/patterns/spacefiller2.rle", 500);
    }

    private void runTestOnThisBoard(String patternPath, int generations) throws IOException, PatternFormatException
    {
        PatternLoader loader = new PatternLoader();
        Pattern pattern = loader.loadAsStream(patternPath);
        GameBoard board = pattern.getGameBoard();
        Simulator simulator = new SimulatorThreaded(new DefaultRuleSet());

        // ----------------------------- DYNAMIC ATOMIC LIST -----------------------------

        GameBoardDynamicList boardDynamicConcurrentList = new GameBoardDynamicList(board.getWidth(), board.getHeight(), true);
        TestUtils.addDataToGameBoard(boardDynamicConcurrentList, board);

        long simulationTimeDynamicConcurrentList = 0;
        for(int i = 0; i < generations; i++)
        {
            simulator.simulateNextGenerationOn(boardDynamicConcurrentList);
            simulationTimeDynamicConcurrentList += simulator.getSimulationTime();
        }

        // ----------------------------- DYNAMIC SYNCHRONIZED LIST -----------------------------

        GameBoardDynamicList boardDynamicList = new GameBoardDynamicList(board.getWidth(), board.getHeight(), false);
        TestUtils.addDataToGameBoard(boardDynamicList, board);

        long simulationTimeDynamicList = 0;
        for(int i = 0; i < generations; i++)
        {
            simulator.simulateNextGenerationOn(boardDynamicList);
            simulationTimeDynamicList += simulator.getSimulationTime();
        }

        // ----------------------------- DYNAMIC -----------------------------

        GameBoardDynamic boardDynamic = new GameBoardDynamic(board.getWidth(), board.getHeight());
        TestUtils.addDataToGameBoard(boardDynamic, board);

        long simulationTimeDynamic = 0;
        for(int i = 0; i < generations; i++)
        {
            simulator.simulateNextGenerationOn(boardDynamic);
            simulationTimeDynamic += simulator.getSimulationTime();
        }

        // ----------------------------- STATIC -----------------------------

        GameBoardStatic boardStatic = new GameBoardStatic(board.getWidth(), board.getHeight());
        TestUtils.addDataToGameBoard(boardDynamic, board);

        long simulationTimeStatic = 0;
        for(int i = 0; i < generations; i++)
        {
            simulator.simulateNextGenerationOn(boardStatic);
            simulationTimeStatic += simulator.getSimulationTime();
        }

        // ----------------------------- RESULT -----------------------------

        System.out.println(generations + " generations of " + patternPath + "\n" +
                " - GameBoardDynamicList (atomic):                        " + simulationTimeDynamicConcurrentList + " ms\n" +
                " - GameBoardDynamicList (synchronized):                  " + simulationTimeDynamicList + " ms\n" +
                " - GameBoardStatic (unoptimized and fixed board size):   " + simulationTimeStatic + " ms\n" +
                " - GameBoardDynamic (used in app):                       " + simulationTimeDynamic + " ms\n");
    }
}
