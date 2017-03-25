package model;


import view.Camera;

public class BoardEditor
{
    private Camera camera;

    public BoardEditor(Camera camera)
    {
        this.camera = camera;
    }

    public void edit(GameBoard board, Point mousePosition, boolean drawLivingCells)
    {
        Point positionOnBoard = new Point();
        Point cameraPos = camera.getCenterOffsetRenderingPosition(board);

        positionOnBoard.x = (int)((mousePosition.x - cameraPos.x) / camera.getZoom());
        positionOnBoard.y = (int)((mousePosition.y - cameraPos.y) / camera.getZoom());

        if(positionOnBoard.x >= 0 &&
                positionOnBoard.x < board.getWidth() &&
                positionOnBoard.y >= 0 &&
                positionOnBoard.y < board.getHeight() &&
                !board.isCellAliveInThisGeneration(positionOnBoard))
        {
            board.editThisGeneration(drawLivingCells, positionOnBoard);
        }
    }
}