package model;

public interface GameModel
{
    void simulateNextGeneration();
    void setGameBoard(GameBoard board);
    GameBoard getGameBoard();
}
