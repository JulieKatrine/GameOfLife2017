package model.BoardIO;

public class RuleStringFormatter
{
    private static final int ABSENT = -1;

    public static String format(String rule) throws PatternFormatException
    {
        rule = rule.trim().replaceAll(" ", "").toUpperCase();

        int indexOfB = rule.indexOf("B");
        int indexOfS = rule.indexOf("S");
        int indexOfSlash = rule.indexOf("/");

        String birthNumbers = "";
        String survivalNumbers = "";

        if(indexOfB == ABSENT && indexOfS == ABSENT)
        {
            if(indexOfSlash != ABSENT)
            {
                // If no B or S is specified survival values usually come before birth values.
                String[] values = rule.split("/");
                return "B" + values[1] + "/S" + values[0];
            }
            else
                throw new PatternFormatException(PatternFormatException.ErrorCode.UNKNOWN_RULE_FORMAT);
        }

        if(indexOfB != ABSENT)
            for(indexOfB +=1; (indexOfB < rule.length() && Character.isDigit(rule.charAt(indexOfB))); indexOfB++)
                birthNumbers += rule.charAt(indexOfB);

        if(indexOfS != ABSENT)
            for(indexOfS +=1; (indexOfS < rule.length() && Character.isDigit(rule.charAt(indexOfS))); indexOfS++)
                survivalNumbers += rule.charAt(indexOfS);

        return "B" + birthNumbers + "/S" + survivalNumbers;
    }
}
