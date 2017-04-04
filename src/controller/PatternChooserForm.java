package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.BoardIO.FileType;
import model.BoardIO.Pattern;
import model.BoardIO.PatternFormatException;
import model.BoardIO.PatternLoader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PatternChooserForm extends Stage implements Initializable
{
    private final int TILE_SIZE = 128;

    private static File lastDirectoryOpened;
    private static ArrayList<Tile> loadedTiles;
    private static Pattern selectedPattern;

    private DropShadow dropShadow;
    private DropShadow selected;

    @FXML private TilePane tilePane;
    @FXML private TextArea textArea;
    @FXML private TextField urlTextField;


    /**
     * The constructor loads the FXML document and sets up the new scene and stage,
     */
    public PatternChooserForm()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/PatternChooserForm.fxml"));
            loader.setController(this);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            super.setTitle("Choose your pattern");
            super.setScene(scene);
            super.setOnCloseRequest(Event ->
            {
                selectedPattern = null;
                closeWindow();
            });
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
        tilePane.setAlignment(Pos.TOP_LEFT);
        tilePane.setVgap(10);
        tilePane.setHgap(10);

        dropShadow = new DropShadow();
        dropShadow.setRadius(15);

        selected = new DropShadow();
        selected.setRadius(15);
        selected.setColor(Color.rgb(100,143,1));

        addLoadedTiles();
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
        else
        {
            for(Tile t : loadedTiles)
                addTileEventListener(t);

            tilePane.getChildren().addAll(loadedTiles);

            if(selectedPattern != null)
                textArea.setText(selectedPattern.getAllMetadata());
        }
    }

    /**
     * This method holds a list of default patterns and adds them to the form.
     */
    private void addDefaultPatterns()
    {
        String[] defaultPatterns = { "test01.rle", "blockstacker.rle"};

        for(String s : defaultPatterns)
            addTileToForm(loadPattern("FILE:patterns/" + s));
    }

    /**
     * This method loads a pattern from a given path.
     * The path needs to have one of two prefixes:
     * "FILE:" - the path to a local file.
     * "URL:" - the web address to the file.
     * The method handles all exceptions and will inform the user if errors occur while loading.
     * @param path
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
        catch (IOException e)
        {
            e.printStackTrace();
            //TODO: add a dialog to inform the user about what went wrong
        }
        catch (PatternFormatException e)
        {
            e.printStackTrace();
            //TODO: add a dialog to inform the user about what went wrong
        }
        catch (OutOfMemoryError e)
        {
            e.printStackTrace();
            //TODO add a dialog to tell the user that the pattern required more memory than JVM had access to
        }

        return null;
    }

    /**
     * This method takes in a pattern, creates a Tile object and adds it to the form.
     * @param pattern
     */
    private void addTileToForm(Pattern pattern)
    {
        if(pattern == null)
            return;

        // Returns if the pattern already is loaded.
        for(Tile t : loadedTiles)
            if(t.getOrigin().equals(pattern.getOrigin()))
                return;

        Tile tile = createTileFromPattern(pattern);
        addTileEventListener(tile);

        tilePane.getChildren().add(tile);
        loadedTiles.add(tile);
    }

    /**
     * This method creates a Tile object from a given pattern.
     * It samples the patterns cell data and creates a scaled image to be used as preview.
     * @param pattern A pattern object
     * @return A tile object
     */
    private Tile createTileFromPattern(Pattern pattern)
    {
        boolean[][] cellData = pattern.getCellData();

        WritableImage writableImage = new WritableImage(TILE_SIZE, TILE_SIZE);
        PixelWriter pw =  writableImage.getPixelWriter();

        double scale = Math.min((double)TILE_SIZE / cellData[0].length, (double)TILE_SIZE / cellData.length);

        for(int y = 0; y < cellData.length; y++)
        {
            for (int x = 0; x < cellData[0].length; x++)
            {
                Color color = cellData[y][x] ? Color.BLACK : Color.rgb(244, 244, 244);

                for (double dy = 0; dy < scale; dy++)
                {
                    for (double dx = 0; dx < scale; dx++)
                    {
                        pw.setColor((int) (x * scale + dx), (int) (y * scale + dy), color);
                    }
                }
            }
        }

        Tile tile = new Tile(writableImage, pattern);
        tile.setEffect(dropShadow);
        return tile;
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

            for(Tile t : loadedTiles)
                t.setEffect(dropShadow);

            loadedTile.setEffect(selected);
        });
    }

    /**
     * This method sets all loaded patterns to null, except selectPattern, and closes the stage.
     */
    private void closeWindow()
    {
        for(Tile t : loadedTiles)
            t.releasePattern();

        System.gc();
        super.close();
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
            addTileToForm(loadPattern("URL:" + url));

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
                addTileToForm(loadPattern("FILE:" + file));
        }
    }

    /**
     * This method is used to access the PatternChooserForms selected pattern.
     * @return A Pattern object.
     */
    public Pattern getPattern()
    {
        return selectedPattern;
    }

    /**
     * This private Tile class adds some functionality to JavaFX ImageView.
     * It stores the associated pattern and a path to where this pattern where loaded from.
     * This is done so big pattern objects can be freed from memory when the PatternChooserForm
     * is closed. When the form gets reopened this path is used to reload the Pattern back in.
     */
    private class Tile extends ImageView
    {
        private String origin;
        private Pattern pattern;

        public Tile(WritableImage img, Pattern pattern)
        {
            super(img);
            this.pattern = pattern;
            this.origin = pattern.getOrigin();
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
