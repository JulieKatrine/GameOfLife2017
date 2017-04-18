package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.BoardIO.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PatternChooserForm extends Stage implements Initializable
{
    private static Stack<Pattern> patternsToLoad;
    private static List<Tile> loadedTiles;
    private static File lastDirectoryOpened;
    private static Pattern selectedPattern;
    private static double scrollBarPos;
    private static int height;
    private static int width;

    private DropShadow selected;
    private DropShadow dropShadow;
    private ExecutorService executorService;

    @FXML private TextField urlTextField;
    @FXML private ScrollPane scrollPane;
    @FXML private TilePane tilePane;
    @FXML private TextArea textArea;

    /**
     * The constructor loads the FXML document and sets up the new scene and stage,
     */
    public PatternChooserForm()
    {
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/PatternChooserForm.fxml"));
            loader.setController(this);
            HBox root = loader.load();

            if(width == 0 || height == 0)
            {
                width = (int) root.getPrefWidth();
                height = (int) root.getPrefHeight();
            }

            Scene scene = new Scene(root, width, height);
            super.setTitle("Choose your pattern");
            super.getIcons().add(GameOfLife.APPLICATION_ICON);
            super.setScene(scene);
        }
        catch (IOException e)
        {
            System.err.println("Failed to load FXML");
            e.printStackTrace();
        }
    }

    /**
     * The method sets up the TilePane and creates new dropshadow effects.
     * @param location Some location.
     * @param resources Some resources.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        dropShadow = new DropShadow();
        dropShadow.setRadius(15);
        dropShadow.setColor(Color.rgb(124, 120, 118));

        selected = new DropShadow();
        selected.setRadius(15);

        addEventListener();
        addLoadedTiles();

        Platform.runLater(() -> tilePane.requestFocus());
    }

    /**
     * Adds event listeners stage and JavaFX elements.
     */
    private void addEventListener()
    {
        // Get the window height when the user resizes it.
        super.heightProperty().addListener((a, b, newVal) -> height = newVal.intValue());

        // Get the window width when the user resizes it and calculate the new preferred column count.
        super.widthProperty().addListener((a, b, newVal) -> {
            tilePane.setPrefColumns((newVal.intValue() - 220) / (Tile.TILE_SIZE + 20));
            width = newVal.intValue();
        });

        // Set selected pattern to null and close the window when the user presses the exit button.
        super.setOnCloseRequest(Event ->
        {
            selectedPattern = null;
            closeWindow();
        });

        // Get the position of the vertical scroll bar.
        scrollPane.vvalueProperty().addListener((a, b, newVal) -> scrollBarPos = newVal.doubleValue());

        // Sets the position of the scrollbar
        Platform.runLater(() ->
        {
            scrollPane.layout();
            scrollPane.setVvalue(scrollBarPos);
        });

        super.addEventHandler(KeyEvent.ANY, event ->
        {
            if(event.getCode() == KeyCode.SPACE)
            {
                if (selectedPattern != null)
                    closeWindow();
            }
        });

    }

    /**
     * This method sets all loaded patterns to null, except selectPattern, and closes the stage.
     */
    private void closeWindow()
    {
        loadedTiles.forEach(Tile::releasePattern);
        executorService.shutdown();
        System.gc();
        super.close();
    }

    /**
     * This method adds previously loaded tiles to the form.
     * The first time a PatternChooserForm is created the loadedTiles-list is
     * instantiated and the default patterns added to it.
     */
    private void addLoadedTiles()
    {
        if(loadedTiles == null)
        {
            loadedTiles = new ArrayList<>();
            addDefaultPatterns();
        }

        // Adds the saved patterns to the chooser.
        while(patternsToLoad != null && patternsToLoad.size() > 0)
            loadAndAddPatternToForm(patternsToLoad.pop().getOrigin());

        loadedTiles.forEach(t -> addTileEventListener(t));

        tilePane.getChildren().addAll(loadedTiles);

        if(selectedPattern != null)
            textArea.setText(selectedPattern.getAllMetadata());
    }

    /**
     * This method holds a list of default patterns and adds them to the form.
     */
    private void addDefaultPatterns()
    {
        String[] defaultPatterns = { "test01.rle", "blockstacker.rle"};

        for(String s : defaultPatterns)
            loadAndAddPatternToForm("FILE:patterns/" + s);
    }

    /**
     * Loads the pattern from the path if it is not already loaded.
     * The method passes the task to a ExecutorService to not block
     * the JavaFX thread while loading large patterns.
     * @param path The path prefixed with either FILE: or URL:
     */
    private void loadAndAddPatternToForm(String path)
    {
        // Returns if the pattern already is loaded.
        for(Tile t : loadedTiles)
            if(t.getOrigin().equals(path))
                return;

        // Adds the temporary hourglass image while the pattern is loading
        //Tile hourGlass = new Tile(new Image(getClass().getResourceAsStream("/resources/hourglass.png")));
        Tile hourGlass = new Tile(new Image("file:resources/hourglass.png"));

        hourGlass.setEffect(dropShadow);
        tilePane.getChildren().add(0, hourGlass);

        executorService.execute(() ->
        {
            Pattern p = loadPattern(path);
            Tile tile = new Tile(p);

            // Interaction with JavaFX elements cannot be done from a separate thread,
            // and is therefor passed on to the Platform.
            Platform.runLater(() ->
            {
                if(tile != null)
                {
                    addTileEventListener(tile);
                    addSelectedEffect(tile);
                    tilePane.getChildren().add(0, tile);
                    loadedTiles.add(0, tile);
                    selectedPattern = tile.pattern;
                }
                tilePane.getChildren().remove(hourGlass);
            });
        });
    }

    private void addSelectedEffect(Tile tile)
    {
        loadedTiles.forEach(t -> t.setEffect(dropShadow));
        tile.setEffect(selected);
    }

    /**
     * This method loads a pattern from a given path.
     * The path needs to have one of two prefixes:
     * "FILE:" - the path to a local file.
     * "URL:" - the web address to the file.
     * The method handles all exceptions and will inform the user if errors occur while loading.
     * @param path The path prefixed with either FILE: or URL:
     * @return A pattern object.
     */
    private Pattern loadPattern(String path)
    {
        try
        {
            PatternLoader loader = new PatternLoader();

            if(path.startsWith("FILE:"))
                return loader.load(new File(path.substring(5)));

            else if (path.startsWith("URL:"))
                return loader.load(path.substring(4));
        }
        catch (PatternFormatException e)
        {
            e.printStackTrace();
            showAlertDialog(Alert.AlertType.ERROR,
                    "Error message",
                     "The pattern you are trying to load is in the wrong format." +
                    "\nMake sure this is an rle file.");
        }
        catch (OutOfMemoryError e)
        {
            e.printStackTrace();
            showAlertDialog(Alert.AlertType.WARNING,
                    "Warning message",
                    "The pattern you are trying to load is too large!");
        }
        catch (Exception e)
        {
            e.printStackTrace();
            showAlertDialog(Alert.AlertType.ERROR,
                    "Error message",
                    "Something went wrong while loading this pattern!");
        }

        return null;
    }

    /**
     * Opens and shows a alert dialog with the given text.
     * @param alertType The type of alert dialog.
     * @param title The title of the dialog.
     * @param content The alert content text.
     */
    private void showAlertDialog(Alert.AlertType alertType, String title, String content)
    {
        Platform.runLater(() ->
        {
            Alert alert = new Alert(alertType);
            ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(GameOfLife.APPLICATION_ICON);
            alert.getDialogPane().setPrefWidth(450);
            alert.setTitle(title);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    /**
     * This method adds the onMouseClicked event to a Tile.
     * When a tile is clicked the event will check if the tile has a loaded pattern
     * and set this as the selected one. If the Tile was loaded in a previous PatternChooseForm instance,
     * its pattern will no longer exist in memory and has to be reloaded. The "dropshadow" effect is applied
     * to all Tiles and the clicked tile gets the "selected" effect. The textArea is also updated with the
     * patterns metadata.
     * @param tile A Tile object.
     */
    private void addTileEventListener(Tile tile)
    {
        tile.setOnMouseClicked(event ->
        {
            Tile loadedTile = (Tile) event.getSource();

            if(loadedTile.pattern == null)
            {
                System.out.println("Reloading pattern...");
                loadedTile.pattern = loadPattern(tile.getOrigin());
            }

            selectedPattern = loadedTile.pattern;
            textArea.setText(loadedTile.pattern.getAllMetadata());

            addSelectedEffect(loadedTile);

            if(event.getClickCount() == 2)
                closeWindow();
        });
    }

    /**
     * This method is called when the "Select pattern" button i pressed.
     * The window is only closed if a pattern is selected.
     */
    @FXML
    private void selectPattern()
    {
        if(selectedPattern != null)
            closeWindow();
    }

    /**
     * This method is called when the "Go" button i pressed.
     * It tries to load the pattern if there is any text in the textField.
     */
    @FXML
    private void loadURL()
    {
        String url = urlTextField.getText();

        if(url != null && url.length() > 0)
            loadAndAddPatternToForm("URL:" + url);

        urlTextField.setText("");
    }

    /**
     * This method is called when the "Open..." button is pressed.
     * It launches a FileChooser and adds the chosen patterns to the form.
     */
    @FXML
    private void openFileChooser()
    {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("GOL Patterns",
                FileType.getFileTypes()));

        if(lastDirectoryOpened != null)
            fileChooser.setInitialDirectory(lastDirectoryOpened);

        List<File> files = fileChooser.showOpenMultipleDialog(this);

        if(files != null && files.size() > 0)
        {
            lastDirectoryOpened = files.get(0).getParentFile();

            for(File file : files)
                loadAndAddPatternToForm("FILE:" + file);
        }
    }

    /**
     * This method is used to access the PatternChooserForms selected pattern.
     * @return A Pattern object.
     */
    public static Pattern getSelectedPattern()
    {
        return selectedPattern;
    }

    /**
     * Adds the pattern to a Stack for future loading.
     * The pattern gets loaded when a new PatternChooser instance is created.
     * @param pattern The Pattern to be loaded.
     */
    public static void addPattern(Pattern pattern)
    {
        if(patternsToLoad == null)
            patternsToLoad = new Stack<>();

        patternsToLoad.add(pattern);
    }

    /**
     * This private Tile class adds some functionality to JavaFX ImageView.
     * It stores the associated pattern and a path to where this pattern where loaded from.
     * This is done so big pattern objects can be freed from memory when the PatternChooserForm
     * is closed. When the form gets reopened this path is used to reload the Pattern back in.
     */
    private class Tile extends ImageView
    {
        public static final int TILE_SIZE = 128;

        private String origin;
        private Pattern pattern;

        public Tile(Image img)
        {
            super(img);
        }

        public Tile(Pattern pattern)
        {
            this.pattern = pattern;
            this.origin = pattern.getOrigin();
            super.setImage(createTileImage());
            super.setEffect(dropShadow);
            super.setCursor(Cursor.HAND);
        }

        /**
         * This method samples the patterns cell data and creates a scaled image to be used as preview.
         * @return A image.
         */
        private Image createTileImage()
        {
            boolean[][] cellData = pattern.getCellData();

            double scale = Math.min((double)TILE_SIZE / cellData[0].length, (double)TILE_SIZE / cellData.length);
            int width = (int) Math.ceil(cellData[0].length * scale);
            int height = (int) Math.ceil(cellData.length * scale);

            WritableImage writableImage = new WritableImage(width, height);
            PixelWriter pw = writableImage.getPixelWriter();

            for(int y = 0; y < cellData.length; y++)
            {
                for (int x = 0; x < cellData[0].length; x++)
                {
                    Color color = cellData[y][x] ? Color.BLACK : Color.rgb(244, 244, 244);

                    for (int dy = 0; dy < scale; dy++)
                        for (int dx = 0; dx < scale; dx++)
                            pw.setColor((int) (x * scale + dx), (int) (y * scale + dy), color);
                }
            }

            return writableImage;
        }

        public String getOrigin()
        {
            return origin;
        }

        public void releasePattern()
        {
            pattern = null;
        }
        public Pattern getPattern()
        {
            return pattern;
        }
    }
}
