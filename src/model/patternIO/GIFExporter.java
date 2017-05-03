package model.patternIO;

import lieng.GIFWriter;
import model.GameBoard;
import model.Point;
import model.simulation.Simulator;
import java.awt.Color;
import java.io.File;
import java.io.IOException;

/**
 * This class handles exporting of animated GIFs.
 * It takes in relevant animation parameters and recursively builds the animation frame by frame
 * with a {@link Simulator} and a {@link GIFWriter}.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 */
public class GIFExporter
{
    private Simulator simulator;
    private GIFWriter writer;
    private int width;
    private int height;
    private int cellSize;
    private boolean trimmedAndCentered;

    /**
     * Takes in the animation parameters and instantiates the GIFWriter.
     * @param file The file in which to save the animation.
     * @param simulator The simulator containing the rule to be used.
     * @param frameRate The frame rate of the animation.
     * @param cellSize The size of each cell.
     * @param width The maximum amounts of horizontal cells. Determines the GIFs width.
     * @param height The maximum amounts of vertical cells. Determines the GIFs height.
     * @param trimmedAndCentered Whether or not the pattern should be trimmed to size and centered for each frame.
     * @throws IOException If creating the GIFWriter fails.
     */
    public GIFExporter(File file, Simulator simulator, int frameRate, int cellSize, int width, int height, boolean trimmedAndCentered) throws IOException
    {
        this.simulator = simulator;
        this.cellSize = cellSize;
        this.width = width;
        this.height = height;
        this.trimmedAndCentered = trimmedAndCentered;
        this.writer = new GIFWriter(width * cellSize, height * cellSize, file.getCanonicalPath(), 1000 / frameRate);
    }

    /**
     * Creates the animated GIF recursively.
     * @param board The GameBoard the next frame will be made of.
     * @param numberOfFrames The amount of frames the animation should consist of.
     * @throws IOException If closing the GIFWriter fails.
     */
    public void export(GameBoard board, int numberOfFrames) throws IOException
    {
        // Return if all frames have been created.
        if(numberOfFrames <= 0)
        {
            writer.close();
            return;
        }

        // Create a frame of the current GameBoard.
        drawFrame(board);

        // Simulate the next generation
        simulator.simulateNextGenerationOn(board);

        // Call this method recursively to generate the next frame.
        export(trimmedAndCentered ? board.trimmedCopy(1) : board, numberOfFrames - 1);
    }

    /**
     * Draws the living cells of the GameBoard to a GIF frame
     * @param board The GameBoard to be used for the frame.
     * @throws IOException If writing to the GIF file fails.
     */
    private void drawFrame(GameBoard board) throws IOException
    {
        int startX = cellSize * ((width / 2) - (board.getWidth() / 2));
        int startY = cellSize * ((height / 2) - (board.getHeight() / 2));

        Point pos = new Point();
        for(pos.y = 0; pos.y < board.getHeight(); pos.y++)
            for(pos.x = 0; pos.x < board.getWidth(); pos.x++)
                if(board.isCellAliveInThisGeneration(pos))
                    writer.fillRect(
                            startX + cellSize * pos.x,
                            startX + cellSize * (pos.x + 1),
                            startY + cellSize * pos.y,
                            startY + cellSize * (pos.y + 1),
                            Color.BLACK);

        writer.insertAndProceed();
    }
}
