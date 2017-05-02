package model.simulation;

import model.BoardIO.RuleStringFormatter;

/**
 * This SimRule implementation is used for custom made rules.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 * @see SimRule
 * @see Result
 * @see Simulator
 */
public class CustomRule implements SimRule
{
    private final Result[] calculatedResult;
    private String rule;

    /**
     * Takes in a {@link RuleStringFormatter standard formatted} rule string
     * and calculates the specified {@link Result results}.
     * @param rule A string on the standard rule format: B123/S123
     */
    public CustomRule(String rule)
    {
        this.rule = rule;

        calculatedResult = new Result[10];

        for(int i = 0; i < calculatedResult.length; i++)
            calculatedResult[i] = Result.DEATH;

        int indexOfSlash = rule.indexOf("/");

        for(int i = rule.length() - 1; i >= 0; i--)
            if(Character.isDigit(rule.charAt(i)))
                calculatedResult[rule.charAt(i) - '0'] = (i < indexOfSlash) ? Result.BIRTH : Result.SURVIVE;

    }

    @Override
    public Result execute(int numberOfLivingNeighbors)
    {
        return calculatedResult[numberOfLivingNeighbors];
    }

    @Override
    public String getStringRule()
    {
        return rule;
    }
}
