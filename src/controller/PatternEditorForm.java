package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.BoardEditor;
import model.BoardIO.Pattern;
import model.BoardIO.PatternExporter;
import model.BoardIO.PatternFormatException;
import model.GameBoard;
import model.Point;
import model.simulation.DefaultRuleSet;
import model.simulation.Simulator;
import model.simulation.ThreadedSimulator;
import view.BoardRenderer;
import view.BoardRendererImpl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * This is the stage and controller class for the pattern editor/saver.
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 */

public class PatternEditorForm extends Stage implements Initializable
{
    /**
     * TODO:
     *  [X] close on save? close button?
     *  [X] Open board button?
     *  [X] Add generation generationTiles
     *  [X] Only tile rendering or additional onPressed functionality?
     *  [X] Automatic generation simulation or button?
     *  [X] Add the saved pattern to the loaded list
     *  [X] CSS stylesheet
     *  [X] Scalable window
     *  [X] Alert dialog on empty board
     *  [ ] Get the colors from the main application (through a ColorProfile object)
     *  [ ] Get the rule from the main app?
     *  [ ] Simulate with the user defined rule
     *  [ ] Add GIF exporter? as filetype in fileChooser?
     *  [ ] Text when patterns repeat? good candidate for GIF?
     *  [ ] Unit tests for export
     *  [ ] JavaDoc
     */

    private final int NUMBER_OF_STRIP_TILES = 20;

    private GenerationTile selectedTile;
    private BoardRenderer boardRenderer;
    private BoardEditor boardEditor;
    private Simulator simulator;
    private Scene scene;

    @FXML private Canvas canvas;
    @FXML private TilePane tilePane;
    @FXML private ScrollPane scrollPane;
    @FXML private TextField ruleTextField;
    @FXML private TextField authorTextField;
    @FXML private TextField patternNameTextField;
    @FXML private TextArea descriptionTextArea;

    /**
     * Loads the FXML and sets up the new stage.
     * @param board The GameBoard to be used as the first generation.
     */
    public PatternEditorForm(GameBoard board)
    {
        selectedTile = new GenerationTile(board.trimmedCopy(1));

        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PatternEditorForm.fxml"));
            loader.setController(this);
            scene = new Scene(loader.load());

            super.setTitle("Edit and save your pattern");
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
     * Sets up the local objects, updates the genration strip and draw the board.
     * @param location Some location.
     * @param resources Some resource.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        boardRenderer = new BoardRendererImpl(canvas);
        boardEditor = new BoardEditor(boardRenderer.getCamera());
        simulator = new ThreadedSimulator(new DefaultRuleSet());

        boardRenderer.setLivingCellColor(Color.color(0.0275, 0.9882, 0));
        boardRenderer.setDeadCellColor(Color.BLACK);

        tilePane.setPrefColumns(NUMBER_OF_STRIP_TILES);

        updateGenerationStrip();
        drawBoard();
    }

    /**
     * Adds event listeners to the JavaFX elements.
     */
    private void addEventListeners()
    {
        super.setOnCloseRequest(event -> closeWindow());

        canvas.setOnMouseDragged(this::editBoard);
        canvas.setOnMousePressed(this::editBoard);
        canvas.setOnMouseReleased(event ->
        {
            updateGenerationStrip();
            drawBoard();
        });

        scene.widthProperty().addListener((a, b, newVal) ->
        {
            // 200 = width of left bar.
            canvas.setWidth(newVal.intValue() - 200);
            drawBoard();
        });

        scene.heightProperty().addListener((a, b, newVal) ->
        {
            // 140 = height of generation strip.
            canvas.setHeight(newVal.intValue() - 140);
            drawBoard();
        });
    }

    /**
     * This method is called when the Close button i pressed.
     * It sets the selected tile to null, making sure the main application
     * don't set it to its new board.
     */
    @FXML private void closeWindow()
    {
        selectedTile = null;
        super.close();
    }

    /**
     * Takes in a MouseEvent and edits the selected pattern accordingly.
     * @param event The mouse event.
     */
    private void editBoard(MouseEvent event)
    {
        if (event.getButton() == MouseButton.SECONDARY)
        {
            boardEditor.edit(selectedTile.getGameBoard(), new Point((int) event.getX(), (int) event.getY()), false);
            drawBoard();
        }
        else if (event.getButton() == MouseButton.PRIMARY)
        {
            boardEditor.edit(selectedTile.getGameBoard(), new Point((int) event.getX(), (int) event.getY()), true);
            drawBoard();
        }
    }

    /**
     * Scales the board to size and renders it to the canvas.
     */
    private void drawBoard()
    {
        boardRenderer.scaleViewToFitBoard(selectedTile.getGameBoard());
        boardRenderer.render(selectedTile.getGameBoard());
    }

    /**
     * Updates the generation strip with new simulated tiles.
     * This method is called when the selected pattern is edited.
     */
    private void updateGenerationStrip()
    {
        // Clear tiles and reset ScrollPane slider
        tilePane.getChildren().clear();
        scrollPane.setHvalue(0);

        // Add the selected til back in with an updated image.
        GameBoard currentBoard = selectedTile.getGameBoard().trimmedCopy(1);
        selectedTile = new GenerationTile(currentBoard);
        tilePane.getChildren().add(selectedTile);
        addSelectedEffectTo(selectedTile);

        // Add the rest of the tiles
        for(int i = 1; i < NUMBER_OF_STRIP_TILES; i++)
        {
            GameBoard board = currentBoard.trimmedCopy();
            simulator.executeOn(board);
            if(board.getPopulation() > 0)
            {
                tilePane.getChildren().add(new GenerationTile(board));
                currentBoard = board;
            }
            else break;
        }

       removeStaticGenerationTiles();
    }

    /**
     *  Removes all unnecessary static generations.
     *  It uses the boards hash codes to compare and remove similar generations
     *  from the back of the generation strip.
     */
    private void removeStaticGenerationTiles()
    {
        // Gather all hash codes
        int[] hashCodes = new int[tilePane.getChildren().size()];
        for(int i = 0; i < tilePane.getChildren().size(); i++)
            hashCodes[i] = ((GenerationTile)tilePane.getChildren().get(i)).getGameBoard().hashCode();

        // Remove generations with the same hash code
        for(int i = hashCodes.length - 1; i > 0; i--)
        {
            if (hashCodes[i] != hashCodes[i - 1] || i == 1)
            {
                if (i < hashCodes.length - 1 && i < tilePane.getChildren().size() - 1)
                    tilePane.getChildren().remove(i, hashCodes.length);

                break;
            }
        }
    }

    /**
     * Resets the effect of all tiles and adds the green selected effect
     * to the given tile.
     * @param tile The tile to get the selected effect.
     */
    private void addSelectedEffectTo(GenerationTile tile)
    {
        tilePane.getChildren().forEach(e -> e.setEffect(new DropShadow()));
        tile.setEffect(new DropShadow(10, Color.GREEN));
    }

    /**
     * This method is called when the Open button i pressed.
     * It closes the PatternEditor without setting the edited pattern to null,
     * making it available through the getSelectedPattern() method.
     */
    @FXML private void openBoard()
    {
        super.close();
    }

    /**
     * This method is called when the Save button is pressed.
     * It opens a FileChooser and saves the pattern in the selected file.
     * The new pattern gets added to the PatternChooser for later access.
     */
    @FXML private void save()
    {
        try
        {
            Pattern pattern = createPattern();
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("GOL Pattern", "*.rle"));
            fileChooser.setInitialFileName(pattern.getName());
            File file = fileChooser.showSaveDialog(this);

            if(file != null)
            {
                PatternExporter exporter = new PatternExporter();
                exporter.export(pattern, file);

                // Add the pattern to the pattern chooser
                pattern.setOrigin("FILE:" + file.getCanonicalPath());
                PatternChooserForm.addPattern(pattern);
            }
        }
        catch (PatternFormatException e)
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(GameOfLife.APPLICATION_ICON);
            alert.getDialogPane().setPrefWidth(450);
            alert.setTitle("Empty board");
            alert.setContentText("The pattern you are trying to save contains no living cells");
            alert.showAndWait();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * The method creates a Pattern object from the select GenerationTile and the text filds.
     * @return A Pattern object.
     * @throws PatternFormatException When there is no living cells on the board.
     */
    private Pattern createPattern() throws PatternFormatException
    {
        GameBoard board = selectedTile.getGameBoard().trimmedCopy();

        if(board.getPopulation() == 0)
            throw new PatternFormatException("The board contains no living cells.");

        // Creates a new data array and copies the data over.
        boolean[][] cellData = new boolean[board.getHeight()][board.getWidth()];
        Point cellPos = new Point();
        for (cellPos.y = 0; cellPos.y < board.getHeight(); cellPos.y++)
            for (cellPos.x = 0; cellPos.x < board.getWidth(); cellPos.x++)
                cellData[cellPos.y][cellPos.x] = board.isCellAliveInThisGeneration(cellPos);

        // Gets all the text from the TextFields.
        String rule = ruleTextField.getText();
        String author = authorTextField.getText();
        String name = patternNameTextField.getText();
        String description = descriptionTextArea.getText();

        // Adds the text to an ArrayList.
        ArrayList<String> metaData = new ArrayList<>();
        metaData.add(name == null ? "" : "#N " + name);
        metaData.add(author == null ? "" : "#O " + author);
        for(String c : description.split("\n"))
            metaData.add("#C " + c);

        // Creates a new pattern and adds the data to it.
        Pattern pattern = new Pattern();
        pattern.setMetadata(metaData);
        pattern.setCellData(cellData);
        pattern.setRule(rule);
        return pattern;
    }

    /**
     * Used by the main application to get the edited GameBoard after the editor is closed.
     * @return The selected GameBoard.
     */
    public GameBoard getSelectedGameBoard()
    {
        if(selectedTile != null)
            return selectedTile.getGameBoard();
        else
            return null;
    }

    /**
     * The class extends the functionality of ImageView.
     * It creates and stores a preview image of an associated GameBoard object.
     */
    private class GenerationTile extends ImageView
    {
        private final int TILE_SIZE = 100;
        private GameBoard board;

        public GenerationTile(GameBoard board)
        {
            this.board = board;
            super.setImage(createTileImage());
            super.setEffect(new DropShadow());
            super.setCursor(Cursor.HAND);
            super.setOnMouseClicked(event ->
            {
                selectedTile = this;
                addSelectedEffectTo(this);
                drawBoard();
            });
        }

        private Image createTileImage()
        {
            double scale = Math.min((double)TILE_SIZE / board.getWidth(), (double)TILE_SIZE / board.getHeight());
            int width = (int) Math.ceil(board.getWidth() * scale);
            int height = (int) Math.ceil(board.getHeight() * scale);

            WritableImage writableImage = new WritableImage(width, height);
            PixelWriter pw = writableImage.getPixelWriter();

            Point pos = new Point();
            for(pos.y = 0; pos.y < board.getHeight(); pos.y++)
            {
                for (pos.x = 0; pos.x < board.getWidth(); pos.x++)
                {
                    Color color = board.isCellAliveInThisGeneration(pos) ? Color.BLACK : Color.rgb(244, 244, 244);

                    for (int dy = 0; dy < scale; dy++)
                        for (int dx = 0; dx < scale; dx++)
                            pw.setColor((int)(pos.x * scale + dx),(int)(pos.y * scale + dy), color);
                }
            }

            return writableImage;
        }

        private GameBoard getGameBoard()
        {
            return board;
        }
    }
}
