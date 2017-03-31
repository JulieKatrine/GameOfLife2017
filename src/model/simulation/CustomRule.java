package model.simulation;

import model.BoardIO.Pattern;

/**
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 */
public class CustomRule implements SimRule
{
    private String rule;
    private byte[] birthValues;
    private byte[] survivalValues;

    public CustomRule(String rule)
    {
        this.rule = rule;
        System.out.println(rule);

        String[] ruleComponents = rule.split("/");

        int numberOfBirthValues = (ruleComponents[0].length() - 1);
        int numberOfSurvivalValues = (ruleComponents[1].length() - 1);

        if(numberOfBirthValues > 0) {
            birthValues = new byte[numberOfBirthValues];
            for (int i = 0; i < numberOfBirthValues; i++) {
                birthValues[i] = (byte)(ruleComponents[0].charAt(i+1) - '0');
            }
        }

        if(numberOfSurvivalValues > 0)
            survivalValues = new byte[numberOfSurvivalValues];
            for (int i = 0; i < numberOfSurvivalValues ; i++) {
                survivalValues[i] = (byte)(ruleComponents[1].charAt(i+1) - '0');
            }
        }

    @Override
    public Result execute(int numberOfLivingNeighbors)
    {
        if(birthValues != null)
            for(int i : birthValues)
                if(numberOfLivingNeighbors == i)
                    return Result.BIRTH;

        if(survivalValues != null)
            for (int i : survivalValues)
                if(numberOfLivingNeighbors == i)
                    return Result.UNCHANGED;

        return Result.DEATH;
    }
}
