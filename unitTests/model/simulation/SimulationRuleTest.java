package model.simulation;

import org.junit.jupiter.api.Test;
import model.simulation.SimulationRule.Result;

import static org.junit.jupiter.api.Assertions.*;

class SimulationRuleTest
{
    @Test
    void testCustomRule()
    {
        SimulationRule rule = new CustomRule("B024/S57");

        Result[] expected =
        {
            Result.BIRTH,
            Result.DEATH,
            Result.BIRTH,
            Result.DEATH,
            Result.BIRTH,
            Result.SURVIVE,
            Result.DEATH,
            Result.SURVIVE,
            Result.DEATH
        };

        for(int i=0; i < expected.length; i++)
            assertEquals(expected[i], rule.execute(i));

        assertEquals("B024/S57", rule.getStringRule());
    }
}