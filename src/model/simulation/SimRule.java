package model.simulation;

public interface SimRule
{
    Result execute(int numberOfLivingNeighbors);
}
