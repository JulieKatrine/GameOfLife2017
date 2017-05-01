package model;

import view.Camera;

/**
 * This class enables the user to edit the cells of a given GameBoard.
 * It uses the positional information in the Camera class to determine
 * which cell to edit at a given mouse position.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 * @see GameBoard
 * @see Camera
 */

public class BoardEditor
{
    private Camera camera;

    public BoardEditor(Camera camera)
    {
        this.camera = camera;
    }

    /**
     * Edits a cell at a given position inside the window.
     *
     * @param board The GameBoard to edit.
     * @param mousePosition The mouse position inside the window.
     * @param drawLivingCells The state to set - true/false = alive/dead.
     */
    public void edit(GameBoard board, Point mousePosition, boolean drawLivingCells)
    {
        Point cameraPos = camera.getCenterOffsetRenderingPosition(board);
        Point positionOnBoard = Point.sub(mousePosition, cameraPos).div(camera.getZoom());

        if(positionOnBoard.x >= 0 &&
                positionOnBoard.x < board.getWidth() &&
                positionOnBoard.y >= 0 &&
                positionOnBoard.y < board.getHeight() &&
                board.isCellAliveInThisGeneration(positionOnBoard) != drawLivingCells)
        {
            board.editThisGeneration(drawLivingCells, positionOnBoard);
        }
    }
}