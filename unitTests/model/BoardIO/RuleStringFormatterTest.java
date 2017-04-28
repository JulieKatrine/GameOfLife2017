package model.BoardIO;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class RuleStringFormatterTest
{
    @Test
    void testFormatting() throws PatternFormatException
    {
        assertEquals("B3/S23", RuleStringFormatter.format("B3/S23"));
        assertEquals("B3/S23", RuleStringFormatter.format("S23/B3"));
        assertEquals("B3/S23", RuleStringFormatter.format("B3S23"));
        assertEquals("B3/S23", RuleStringFormatter.format("S23B3"));
        assertEquals("B3/S23", RuleStringFormatter.format("b3/s23"));
        assertEquals("B3/S23", RuleStringFormatter.format("s23/b3"));
        assertEquals("B3/S23", RuleStringFormatter.format("B3S23asd"));
        assertEquals("B3/S23", RuleStringFormatter.format("23/3"));
        assertEquals("B3/S",   RuleStringFormatter.format("B3"));
        assertEquals("B/S23",  RuleStringFormatter.format("S23"));
    }

    @Test
    void testUnknownFormatException()
    {
        PatternFormatException exception = assertThrows(PatternFormatException.class, () ->
        {
            RuleStringFormatter.format("rule");
        });

        assertEquals(PatternFormatException.ErrorCode.UNKNOWN_RULE_FORMAT, exception.getErrorCode());
    }
}