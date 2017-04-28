package model.simulation;

import model.GameBoard;
import model.Point;
import model.TestUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimulatorTest
{
    @Test
    void executeOn()
    {
        GameBoard board = TestUtils.getGameBoardImplementation(5, 5);
        Simulator simulator = new SimulatorImpl(new DefaultRuleSet());
        String glider = "010001111";

        // Add the glider pattern to the board
        for(int y = 0; y < 3; y++)
            for(int x = 0; x < 3; x++)
                board.editThisGeneration(glider.charAt(y * 3 + x) == '1', new Point(x, y));

        // Simulate four generations
        for(int i = 0; i < 4; i++)
            simulator.simulateNextGenerationOn(board);

        // The glider oscillates and should stay the same after 4 generations.
        assertEquals(glider, TestUtils.trimmedGameBoardToString(board));
    }

    @Test
    void testSimulationTime() throws InterruptedException
    {
        Simulator simulator = new Simulator(new DefaultRuleSet())
        {
            @Override
            public void executeOn(GameBoard board)
            {
                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException ignored) {}
            }
        };

        simulator.simulateNextGenerationOn(TestUtils.getGameBoardImplementation(10,10));

        assertEquals(100, (int)simulator.getSimulationTime(), 20);
    }

    @Test
    void testGenerationCountPerSecond() throws InterruptedException
    {
        GameBoard board = TestUtils.getGameBoardImplementation(10, 10);
        Simulator simulator = new Simulator(new DefaultRuleSet())
        {
            @Override
            protected void executeOn(GameBoard board)
            {
                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException ignored) {}
            }
        };

        for(int i = 0; i < 15; i++)
            simulator.simulateNextGenerationOn(board);

        assertEquals(10, simulator.getGenerationsPerSecond(), 2);
    }

    @Test
    void testGetterAndSetters()
    {
        SimRule defaultRule = new DefaultRuleSet();
        Simulator simulator = new SimulatorImpl(defaultRule);
        assertEquals(defaultRule, simulator.getSimulationRule());

        SimRule customRule = new CustomRule("B3/S23");
        simulator.setRule(customRule);
        assertEquals(customRule, simulator.getSimulationRule());
    }
}