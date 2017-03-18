package model.simulation;

import model.GameBoard;
import model.GameBoardTest;
import model.Point;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimulatorTest
{
    @Test
    void executeOn()
    {
        GameBoard board = GameBoardTest.getGameBoardImplementation(5, 5);
        Simulator simulator = new SimulatorImpl(new DefaultRuleSet());

        byte[][] glider =
        {
            {0, 1, 0, 0},
            {0, 0, 1, 0},
            {1, 1, 1, 0},
            {0, 0, 0, 0}
        };

        byte[][] gliderAfterFourGenerations =
        {
            {0, 0, 0, 0},
            {0, 0, 1, 0},
            {0, 0, 0, 1},
            {0, 1, 1, 1}
        };

        // Add the glider pattern to the board
        for(int y = 0; y < glider.length; y++)
            for(int x = 0; x < glider[0].length; x++)
                board.editThisGeneration(glider[y][x] == 1, new Point(x, y));

        // Simulate four generations
        for(int i = 0; i < 4; i++)
            simulator.executeOn(board);

        // Test every cell in board to find simulation errors
        for(int y = 0; y < glider.length; y++)
            for(int x = 0; x < glider[0].length; x++)
                assertEquals(gliderAfterFourGenerations[y][x] == 1,
                        board.isCellAliveInThisGeneration(new Point(x, y)));

    }
}