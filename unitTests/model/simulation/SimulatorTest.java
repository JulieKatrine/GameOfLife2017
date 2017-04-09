package model.simulation;

import model.GameBoard;
import model.GameBoardTest;
import model.Point;
import model.TestUtilities;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimulatorTest
{
    @Test
    void executeOn()
    {
        GameBoard board = TestUtilities.getGameBoardImplementation(5, 5);
        Simulator simulator = new SimulatorImpl(new DefaultRuleSet());
        String glider = "010001111";

        // Add the glider pattern to the board
        for(int y = 0; y < 3; y++)
            for(int x = 0; x < 3; x++)
                board.editThisGeneration(glider.charAt(y * 3 + x) == '1', new Point(x, y));

        // Simulate four generations
        for(int i = 0; i < 4; i++)
            simulator.executeOn(board);

        // The glider oscillates and should stay the same after 4 generations.
        assertEquals(glider, TestUtilities.gameBoardToString(board));
    }
}