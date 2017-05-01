package controller;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.BoardIO.GIFExporter;
import model.GameBoard;
import model.simulation.Simulator;
import view.BoardRenderer;
import view.ColorProfile;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * TODO: Add description
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 */
public class GIFExporterForm extends Stage implements Initializable
{
    private File outputFile;
    private Simulator simulator;
    private GameBoard startBoard;
    private GameBoard currentBoard;
    private BoardRenderer boardRenderer;

    private int generationCount;
    private int numberOfFrames;
    private int frameRate;
    private int cellSize;

    private int largestFrameWidth;
    private int largestFrameHeight;

    @FXML private Canvas canvas;
    @FXML private TextField framesField;
    @FXML private Label framesTextLabel;
    @FXML private Slider cellSizeSlider;
    @FXML private Slider frameRateSlider;
    @FXML private TextField cellSizeField;
    @FXML private TextField frameRateField;
    @FXML private CheckBox centerPatternCheckBox;
    @FXML private Button createGIFButton;
    @FXML private VBox leftBar;

    /**
     * Loads the FXML and sets up the stage.
     * @param startBoard The first generation in the animation.
     * @param simulator The simulator to be used when simulating the next generations.
     * @param numberOfFrames The suggested amount of frames the animation should have.
     * @param outputFile The file in which the data should be written to. If null, a FileChooser will be opened.
     */
    public GIFExporterForm(GameBoard startBoard, Simulator simulator, int numberOfFrames, File outputFile)
    {
        this.startBoard = startBoard;
        this.currentBoard = startBoard.deepCopy();
        this.numberOfFrames = numberOfFrames;
        this.outputFile = outputFile;
        this.simulator = simulator;

        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/GifExporterForm.fxml"));
            loader.setController(this);
            Scene scene = new Scene(loader.load());

            super.setTitle("Save your animation");
            super.getIcons().add(GameOfLife.APPLICATION_ICON);
            super.setScene(scene);

            addEventListeners();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Sets up the boardRenderer, AnimationTimer and the pattern repetition hint.
     * @param location A location.
     * @param resources A resource.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        boardRenderer = new BoardRenderer(canvas);
        ColorProfile cProfile = new ColorProfile(Color.WHITESMOKE,Color.BLACK, Color.GRAY);
        cProfile.setGridRendering(false);
        boardRenderer.setColorProfile(cProfile);

        cellSize = (int) cellSizeSlider.getValue();
        frameRate = (int) frameRateSlider.getValue();
        framesField.setText(String.valueOf(numberOfFrames));

        Platform.runLater(() -> createGIFButton.requestFocus());
        createAndStartAnimationTimer();
        updateRepeatingPatternHint(true);
    }

    /**
     * Adds event listeners to the JavaFX objects.
     */
    private void addEventListeners()
    {
        cellSizeSlider.valueProperty().addListener((a, b, newVal) ->
        {
            cellSizeField.setText(String.valueOf(newVal.intValue()));
            cellSize = newVal.intValue();
            boardRenderer.getCamera().setZoom(cellSize);
            drawBoard();
        });

        cellSizeField.textProperty().addListener((a, oldVal, newVal) ->
        {
            try
            {
                int val = Integer.parseInt(newVal);
                if(val > cellSizeSlider.getMax())
                    cellSizeSlider.setMax(val);
                cellSizeSlider.setValue(val);
            }
            catch(NumberFormatException e)
            {
                if(newVal.length() > 0)
                    cellSizeField.setText(oldVal);
            }
        });

        frameRateSlider.valueProperty().addListener((a, b, newVal) ->
        {
            frameRateField.setText(String.valueOf(newVal.intValue()));
            frameRate = newVal.intValue();
        });

        frameRateField.textProperty().addListener((a, oldVal, newVal) ->
        {
            try
            {
                int val = Integer.parseInt(newVal);
                if(val > frameRateSlider.getMax())
                    frameRateSlider.setMax(val);
                frameRateSlider.setValue(val);
            }
            catch(NumberFormatException e)
            {
                if(newVal.length() > 0)
                    frameRateField.setText(oldVal);
            }
        });

        framesField.textProperty().addListener((a, oldVal, newVal) ->
        {
            try
            {
                numberOfFrames = Math.max(1, Integer.parseInt(newVal));
                largestFrameWidth = largestFrameHeight = 0;
                updateRepeatingPatternHint(false);
            }
            catch(NumberFormatException e)
            {
                if(newVal.length() > 0)
                    framesField.setText(oldVal);
            }
        });

        super.getScene().widthProperty().addListener((a, b, newVal) ->
        {
            canvas.setWidth(newVal.intValue() - leftBar.getPrefWidth());
            drawBoard();
        });

        super.getScene().heightProperty().addListener((a, b, newVal) ->
        {
            canvas.setHeight(newVal.intValue());
            drawBoard();
        });
    }

    /**
     * Creates a new AnimationTimer and starts it.
     * The timer draws and simulates a new frame of the animation at
     * the given frame rate.
     */
    private void createAndStartAnimationTimer()
    {
        new AnimationTimer()
        {
            private long timer;

            @Override
            public void handle(long now)
            {
                if(System.currentTimeMillis() > timer + (1000 / frameRate))
                {
                    drawBoard();
                    simulateNextFrame();
                    timer = System.currentTimeMillis();
                }
            }
        }.start();
    }

    /**
     * Simulates the next generation of the current GameBoard.
     * It counts the generations and starts over when it reaches numberOfFrames.
     * If the centerPatternCheckBox is selected, the board gets trimmed with
     * a padding of one.
     */
    private void simulateNextFrame()
    {
        if(generationCount < numberOfFrames - 1)
        {
            simulator.simulateNextGenerationOn(currentBoard);
            generationCount++;

            if(centerPatternCheckBox.isSelected())
                currentBoard = currentBoard.trimmedCopy(1);

            largestFrameWidth = Math.max(largestFrameWidth, currentBoard.getWidth());
            largestFrameHeight = Math.max(largestFrameHeight, currentBoard.getHeight());
        }
        else
        {
            currentBoard = startBoard.deepCopy();
            generationCount = 0;
        }
    }

    /**
     * Draws the current GameBoard to the canvas.
     */
    private void drawBoard()
    {
        boardRenderer.render(currentBoard);
    }

    /**
     * Searches for repetition and notifies the user if the generations
     * repeat after a certain amount of frames. If setFrameFiled is true,
     * the Frames input field will be set to the amount of frames in the repeating pattern.
     * @param setFramesField Whether or not FramesField should be updated.
     */
    private void updateRepeatingPatternHint(boolean setFramesField)
    {
        int result = lastIndexOfRepeatingPattern(getHashCodes(numberOfFrames * 2));
        if(result != -1)
        {
            framesTextLabel.setText("Detected a repeating pattern after " + result + " frames");
            if(setFramesField)
                framesField.setText(String.valueOf(numberOfFrames = result));
        }
        else framesTextLabel.setText("No repeating pattern");
    }

    /**
     * Searches for repetition in a list of hash codes.
     * Only generations repeating from the start will be found.
     * @param hashCodes A list hash codes.
     * @return The last index in the first occurrence of the repeating pattern.
     */
    public static int lastIndexOfRepeatingPattern(List<Integer> hashCodes)
    {
        if(hashCodes == null || hashCodes.size() == 0)
            return -1;

        int firstCode = hashCodes.get(0);
        int lengthOfFirstSequence = 0;

        // Searches for a generation with the same hash code as the first one.
        for(int i = 1; i < hashCodes.size() && lengthOfFirstSequence == 0; i++)
            if(hashCodes.get(i) == firstCode)
                lengthOfFirstSequence = i;

        // If this generation exists, check to see if the generations in the first sequence repeats in the next.
        if(lengthOfFirstSequence >= 2 && hashCodes.size() >= lengthOfFirstSequence * 2)
        {
            List<Integer> firstSequence = hashCodes.subList(0, lengthOfFirstSequence);
            List<Integer> nextSequence = hashCodes.subList(lengthOfFirstSequence, lengthOfFirstSequence*2);

            if(firstSequence.equals(nextSequence))
                return lengthOfFirstSequence;
        }

        return -1;
    }

    /**
     * Simulates a given amount of generations and returns a list of their hash codes.
     * @param numberOfGenerations The amount of generations to get the codes from.
     * @return A list of hash codes.
     */
    private List<Integer> getHashCodes(int numberOfGenerations)
    {
        List<Integer> hashCodes = new ArrayList<>(numberOfGenerations);

        GameBoard board = startBoard;
        for(int i = 0; i < numberOfGenerations; i++)
        {
            board = board.trimmedCopy(1);
            hashCodes.add(board.hashCode());
            simulator.simulateNextGenerationOn(board);
        }
        return hashCodes;
    }

    /**
     * This method is called when the user presses the Export button.
     * It creates a GIFExporter and sends in the user specified data.
     * If this form was opened form the generation strip in the PatternEditorForm,
     * no output file has been selected and a FileChooser is opened.
     * @see GIFExporter
     */
    @FXML
    private void createGif()
    {
        if (outputFile == null)
        {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Animation", " *.gif"));
            outputFile = fileChooser.showSaveDialog(this);
        }

        if (outputFile != null)
        {
            try
            {
                GIFExporter exporter = new GIFExporter(
                        outputFile,
                        simulator,
                        frameRate,
                        cellSize,
                        largestFrameWidth,
                        largestFrameHeight,
                        centerPatternCheckBox.isSelected());

                exporter.export(startBoard.deepCopy(), numberOfFrames);

                super.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(GameOfLife.APPLICATION_ICON);
                alert.setTitle("Error");
                alert.setContentText("Exporting the GIF failed horribly and it's probably not your fault!");
                alert.showAndWait();
            }
        }
    }
}
