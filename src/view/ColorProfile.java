package view;

import javafx.scene.paint.Paint;

/**
 * Color profile for the game board.
 *
 * Allows you to set and get the colors of the cells and grid of the pattern. You can also decide weather or not the grid should be drawn.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 */
public class ColorProfile
{
    private Paint deadColor;
    private Paint aliveColor;
    private Paint gridColor;
    private boolean renderGrid = true;

    /**
     * Constructs the color profile of the pattern.
     * @param deadColor The color of dead cells.
     * @param aliveColor The color of living cells.
     * @param gridColor The color of the grid.
     */
    public ColorProfile(Paint deadColor, Paint aliveColor, Paint gridColor)
    {
        this.deadColor = deadColor;
        this.aliveColor = aliveColor;
        this.gridColor = gridColor;
    }

    public void setDeadColor(Paint color)
    {
        deadColor = color;
    }

    public void setAliveColor(Paint color)
    {
        aliveColor = color;
    }

    public void setGridColor(Paint color)
    {
        gridColor = color;
    }

    public void setGridRendering(boolean state)
    {
        renderGrid = state;
    }

    public boolean renderGrid()
    {
        return renderGrid;
    }

    public Paint getDeadColor()
    {
        return deadColor;
    }

    public Paint getAliveColor()
    {
        return aliveColor;
    }

    public Paint getGridColor()
    {
        return gridColor;
    }
}
