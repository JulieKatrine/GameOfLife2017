package model.BoardIO;

import lieng.GIFWriter;
import model.GameBoard;
import model.Point;
import model.simulation.Simulator;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

public class GIFExporter
{
    private File file;
    private Simulator simulator;
    private GIFWriter writer;

    private int width;
    private int height;
    private int cellSize;
    private int frameRate;
    private boolean trimmedAndCentered;

    public GIFExporter(File file, Simulator simulator, int frameRate, int cellSize, boolean trimmedAndCentered)
    {
        this.file = file;
        this.simulator = simulator;
        this.frameRate = frameRate;
        this.cellSize = cellSize;
        this.trimmedAndCentered = trimmedAndCentered;
    }

    public void export(GameBoard board, int numberOfFrames) throws IOException
    {
        if(writer == null)
        {
            board = board.trimmedCopy(1);
            this.width = board.getWidth() * cellSize;
            this.height = board.getHeight() * cellSize;
            this.writer = new GIFWriter(width, height, file.getCanonicalPath(), 1000 / frameRate);
        }

        if(numberOfFrames <= 0)
        {
            writer.close();
            return;
        }

        drawFrame(board);
        simulator.executeOn(board);

        export(trimmedAndCentered ? board.trimmedCopy(1) : board, numberOfFrames - 1);
    }

    private void drawFrame(GameBoard board) throws IOException
    {
        double scaleX = (double) width / board.getWidth();
        double scaleY = (double) height / board.getHeight();

        Point pos = new Point();
        for(pos.y = 0; pos.y < board.getHeight(); pos.y++)
            for(pos.x = 0; pos.x < board.getWidth(); pos.x++)
                if(board.isCellAliveInThisGeneration(pos))
                    writer.fillRect(
                            (int)(scaleX * pos.x),
                            (int)(scaleX * (pos.x + 1)),
                            (int)(scaleY * pos.y),
                            (int)(scaleY * (pos.y + 1)),
                            Color.BLACK);

        writer.insertAndProceed();
    }
}
