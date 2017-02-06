package model.simulation;

public class DefaultRuleSet implements SimRule
{
    public Result execute(int numberOfLivingNeighbors)
    {
        if(numberOfLivingNeighbors != 2)
            return Result.DEATH;
        else if(numberOfLivingNeighbors == 3)
            return Result.BIRTH;
        else
            return Result.UNCHANGED;
    }
}
