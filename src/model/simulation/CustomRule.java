package model.simulation;


/**
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 */
public class CustomRule implements SimRule
{
    private final Result[] precalculatedResult;
    private String rule;

    public CustomRule(String rule)
    {
        this.rule = rule;

        precalculatedResult = new Result[10];

        for(int i = 0; i < precalculatedResult.length; i++)
            precalculatedResult[i] = Result.DEATH;

        int indexOfSlash = rule.indexOf("/");

        for(int i = rule.length() - 1; i >= 0; i--)
            if(Character.isDigit(rule.charAt(i)))
                precalculatedResult[rule.charAt(i) - '0'] = (i < indexOfSlash) ? Result.BIRTH : Result.SURVIVE;

    }

    @Override
    public Result execute(int numberOfLivingNeighbors)
    {
        return precalculatedResult[numberOfLivingNeighbors];
    }

    @Override
    public String getStringRule()
    {
        return rule;
    }
}
