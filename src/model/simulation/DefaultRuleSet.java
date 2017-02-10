package model.simulation;

public class DefaultRuleSet implements SimRule
{
    public Result execute(int numberOfLivingNeighbors)
    {
        if(numberOfLivingNeighbors == 3)
            return Result.BIRTH;
        else if(numberOfLivingNeighbors != 2)
            return Result.DEATH;
        else
            return Result.UNCHANGED;
    }
}
