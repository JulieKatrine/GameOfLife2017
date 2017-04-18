package view;


import javafx.scene.paint.Paint;

public class ColorProfile
{
    private Paint deadColor;
    private Paint aliveColor;
    private Paint gridColor;
    private boolean renderGrid = true;

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
        return  renderGrid;
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
