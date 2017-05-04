package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.BoardEditor;
import model.patternIO.Pattern;
import model.patternIO.PatternExporter;
import model.patternIO.PatternFormatException;
import model.patternIO.RuleStringFormatter;
import model.GameBoard;
import model.Point;
import model.simulation.*;
import view.BoardRenderer;
import view.ColorProfile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This is the stage and controller class for the editor/saver window.
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 */

public class PatternEditorController extends Stage
{
    private final int MAX_BOARD_SIZE = 4000000;

    private GenerationTile selectedTile;
    private BoardRenderer boardRenderer;
    private BoardEditor boardEditor;
    private Image createGIFImage;
    private Image loopImage;
    private Simulator simulator;
    private GameBoard selectedGameBoard;

    @FXML private Canvas canvas;
    @FXML private TilePane tilePane;
    @FXML private ScrollPane scrollPane;
    @FXML private TextField ruleTextField;
    @FXML private TextField authorTextField;
    @FXML private TextField patternNameTextField;
    @FXML private TextArea descriptionTextArea;
    @FXML private Button applyRuleButton;
    @FXML private Button saveButton;
    @FXML private VBox leftBar;

    /**
     * Loads the FXML and sets up the new stage.
     * @param board The GameBoard to be used as the first generation.
     * @param simulator The simulator to update the generation strip.
     */
    public PatternEditorController(GameBoard board, Simulator simulator)
    {
        if(showSizeWarning(board, simulator))
        {
            selectedTile = new GenerationTile(board.trimmedCopy(1));
            this.simulator = simulator;

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/layout/PatternEditor.fxml"));
                loader.setController(this);
                Scene scene = new Scene(loader.load());

                super.setTitle("Edit and save your pattern");
                super.getIcons().add(Main.APPLICATION_ICON);
                super.setScene(scene);

                addEventListeners();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else
            closeWindow();
    }

    /**
     * Sets up the local objects, updates the generation strip and draw the board.
     */
    @FXML
    public void initialize()
    {
        boardRenderer = new BoardRenderer(canvas);
        boardEditor = new BoardEditor(boardRenderer.getCamera());

        createGIFImage = new Image(getClass().getResourceAsStream("/img/newCreateGIF.png"));
        loopImage = new Image(getClass().getResourceAsStream("/img/loop.png"));
        ruleTextField.setText(simulator.getSimulationRule().getStringRule());
        boardRenderer.setColorProfile(new ColorProfile(Color.BLACK, Color.color(0.0275, 0.9882, 0), Color.GRAY ));

        Platform.runLater(() -> scrollPane.requestFocus());
        updateGenerationStrip();
        drawBoard();
    }

    //TODO: finish me
    private boolean showSizeWarning(GameBoard gameBoard, Simulator simulator)
    {
       if(gameBoard.getHeight()*gameBoard.getWidth() >= MAX_BOARD_SIZE)
       {
           Alert alert = new Alert(Alert.AlertType.WARNING);
           DialogPane dialogPane = alert.getDialogPane();
           ButtonType tryAnywayButton = new ButtonType("Try anyway");
           ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
           ButtonType save = new ButtonType("Save");

           //dialogPane.lookupButton( ButtonType.CANCEL ).getStyleClass().add("cancelButton");

           ((Stage)dialogPane.getScene().getWindow()).getIcons().add(Main.APPLICATION_ICON);
           dialogPane.setPrefWidth(450);
           alert.setTitle("Warning");
           alert.setHeaderText("");
           alert.setContentText("The board might be too large for editing. Would you like to " +
                   "save it directly or try editing anyway?\n\n");
           alert.getButtonTypes().setAll(save, tryAnywayButton, cancel);

           dialogPane.getStylesheets().add(getClass().getResource("/view/layout/AlertStyleSheet.css").toExternalForm());
           dialogPane.getStyleClass().add("alert");

           Optional<ButtonType> result = alert.showAndWait();
           if (result.get() == cancel)
               return false;
           else if (result.get() == tryAnywayButton)
               return true;
           else if (result.get() == save)
           {
               FileChooser fileChooser = new FileChooser();
               fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("GOL Pattern", "*.rle"));
               File file = fileChooser.showSaveDialog(this);
               if(file != null)
               {
                   selectedGameBoard = gameBoard;
                   Pattern pattern = new Pattern();
                   pattern.setMetadata(new ArrayList<>());
                   pattern.setRuleString(simulator.getSimulationRule().toString());
                   addCellData(pattern);
                   PatternExporter exporter = new PatternExporter();
                   try {
                       exporter.export(pattern, file);
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }


               return false;
           }
       }
        return true;
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

        super.getScene().widthProperty().addListener((a, b, newVal) ->
        {
            canvas.setWidth(newVal.intValue() - leftBar.getPrefWidth());
            drawBoard();
        });

        super.getScene().heightProperty().addListener((a, b, newVal) ->
        {
            canvas.setHeight(newVal.intValue() - tilePane.getPrefHeight());
            drawBoard();
        });

        super.addEventHandler(KeyEvent.ANY, event ->
        {
            KeyCode code = event.getCode();
            if(scrollPane.isFocused() && (code == KeyCode.RIGHT || code == KeyCode.LEFT))
            {
                int direction = (code == KeyCode.RIGHT) ? 1 : -1;
                int index = tilePane.getChildren().indexOf(selectedTile);
                if(index + direction >= 0 && index + direction < tilePane.getChildren().size())
                {
                    GenerationTile tile = (GenerationTile) tilePane.getChildren().get(index + direction);
                    if(tile.board != null)
                    {
                        addSelectedEffectTo(selectedTile = tile);
                        scrollPane.setHvalue(scrollPane.getHvalue() + (0.8 / tilePane.getPrefColumns()) * direction);
                        drawBoard();
                    }
                }
            }
        });
    }

    /**
     * This method is called when the Apply button i pressed.
     * It reads the rule text field and formats its to the standard form.
     * Unknown format will be replaced with the default rule.
     */
    @FXML private void applyRule()
    {
        String rule = ruleTextField.getText();
        try
        {
            rule = RuleStringFormatter.format(rule);
            ruleTextField.setText(rule);
        }
        catch (PatternFormatException e)
        {
            rule = "B3/S23";
            ruleTextField.setText(rule);
        }
        simulator.setRule(new CustomRule(rule));
        updateGenerationStrip();
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

        List<Integer> generationHashCodes = new ArrayList<>();

        // Simulate and generate new tiles
        GameBoard currentBoard = selectedTile.getGameBoard();
        for(int i = 0; i < tilePane.getPrefColumns(); i++)
        {
            GameBoard trimmedBoard = currentBoard.trimmedCopy(1);
            tilePane.getChildren().add(new GenerationTile(trimmedBoard));
            generationHashCodes.add(trimmedBoard.hashCode());

            simulator.simulateNextGenerationOn(currentBoard);

            // Stop adding tiles if the generation has died out.
            if(currentBoard.getPopulation() == 0)
                break;
        }

        // Set the selected tile to the first one in the strip.
        selectedTile = (GenerationTile) tilePane.getChildren().get(0);
        selectedGameBoard = selectedTile.getGameBoard().trimmedCopy();

        addSelectedEffectTo(selectedTile);

        // Clean up the strip by removing repetition
        removeStaticGenerationTiles(generationHashCodes);
        replaceRepeatingPatternsWithGIFExport(generationHashCodes);
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
     *  Removes all unnecessary static generations.
     *  It uses the boards hash codes to compare and remove similar generations,
     *  starting from the back of the strip. Leaves the first tile if all tiles are the same.
     *  @param hashCodeList A list of hash codes matching the generations in the tilePane.
     */
    private void removeStaticGenerationTiles(List<Integer> hashCodeList)
    {
        // Remove generations with the same hash code
        for(int i = hashCodeList.size() - 1; i > 0; i--)
        {
            if (!hashCodeList.get(i).equals(hashCodeList.get(i - 1)) || i == 1)
            {
                i += 3; // Show some of the repeated patterns
                if (i < hashCodeList.size() - 1 && i < tilePane.getChildren().size() - 1)
                {
                    tilePane.getChildren().remove(i, hashCodeList.size());

                    // Add the loop image if the static pattern repeats
                    GenerationTile tile = new GenerationTile(null);
                    tile.setImage(loopImage);
                    tile.setOnMouseClicked(event -> {});
                    Tooltip.install(tile, new Tooltip("This pattern repeats indefinitely"));
                    tilePane.getChildren().add(tile);
                }

                break;
            }
        }
    }

    /**
     * Replaces repeating patterns with an option to export the remaining tiles to a GIF.
     * This method uses a search function provided by the AnimationExportController.
     * @param hashCodeList A list of hash codes matching the generations in the tilePane.
     * @see AnimationExportController
     */
    private void replaceRepeatingPatternsWithGIFExport(List<Integer> hashCodeList)
    {
        int result = AnimationExportController.locateLastIndexOfRepeatingPattern(hashCodeList);
        if(result != -1)
        {
            // Remove all tiles after the first sequence.
            tilePane.getChildren().remove(result, tilePane.getChildren().size());

            // Add a special GIF-button tile to the strip.
            GenerationTile tile = new GenerationTile(null);

            tile.setImage(createGIFImage);
            tile.setOnMouseClicked(event -> openGIFExporterDialog(null));
            Tooltip.install(tile, new Tooltip("Create a GIF of this repeating pattern"));
            tilePane.getChildren().add(tile);
        }
    }

    /**
     * Opens a new GIFExporter dialog window.
     * @param file The output file. If null, a FileChooser will be opened later.
     */
    private void openGIFExporterDialog(File file)
    {
        AnimationExportController exporterForm = new AnimationExportController(
                selectedTile.getGameBoard(),
                simulator,
                tilePane.getChildren().size(),
                file
        );
        exporterForm.initModality(Modality.WINDOW_MODAL);
        exporterForm.initOwner(this);
        exporterForm.showAndWait();
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
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("GOL Pattern", "*.rle"),
                    new FileChooser.ExtensionFilter("Animation", " *.gif"));
            fileChooser.setInitialFileName(pattern.getName());
            File file = fileChooser.showSaveDialog(this);

            if(file != null)
            {
                if(file.getName().endsWith(".rle"))
                {
                    PatternExporter exporter = new PatternExporter();
                    exporter.export(pattern, file);

                    // Add the file to the PatternChooserController
                    PatternChooserController.addFileToLoadingQueue(file);
                }
                else if(file.getName().endsWith(".gif"))
                    openGIFExporterDialog(file);
            }
        }
        catch (PatternFormatException e)
        {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(Main.APPLICATION_ICON);
            alert.getDialogPane().setPrefWidth(450);
            alert.setTitle("Empty board");
            alert.setContentText(e.getErrorMessage());
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
        if(selectedGameBoard.getPopulation() == 0)
            throw new PatternFormatException(PatternFormatException.ErrorCode.NO_LIVING_CELLS);

        // Creates a new pattern and adds the data to it.
        Pattern pattern = new Pattern();
        addCellData(pattern);
        addMetaData(pattern);
        return pattern;
    }

    private void addCellData(Pattern pattern)
    {
        // Creates a new data array and copies the data over.
        boolean[][] cellData = new boolean[selectedGameBoard.getHeight()][selectedGameBoard.getWidth()];
        Point cellPos = new Point();
        for (cellPos.y = 0; cellPos.y < selectedGameBoard.getHeight(); cellPos.y++)
            for (cellPos.x = 0; cellPos.x < selectedGameBoard.getWidth(); cellPos.x++)
                cellData[cellPos.y][cellPos.x] = selectedGameBoard.isCellAliveInThisGeneration(cellPos);

        pattern.setCellData(cellData);
    }

    private void addMetaData(Pattern pattern)
    {
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

        pattern.setMetadata(metaData);
        pattern.setRuleString(rule);
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

    public void setColorProfile(ColorProfile profile)
    {
        if(boardRenderer != null)
            boardRenderer.setColorProfile(profile);
    }

    /**
     * The class extends the functionality of ImageView.
     * It creates and stores a preview image of an associated GameBoard object.
     */
    private class GenerationTile extends ImageView
    {
        public static final int TILE_SIZE = 100;
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
            if(board == null)
                return null;

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
