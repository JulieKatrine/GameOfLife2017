package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import java.util.List;
import java.util.ResourceBundle;

public class PatternChooserForm extends Stage implements Initializable
{
    private final int TILE_SIZE = 128;
    private File lastDirectoryOpened;
    private Pattern selectedPattern;
    private DropShadow dropShadow;
    private DropShadow selected;

    @FXML private TilePane tilePane;
    @FXML private TextArea textArea;
    @FXML private TextField urlTextField;

    public PatternChooserForm() throws IOException
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
            super.close();
        });
    }

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

        addDefaultPatterns();
    }

    private void addDefaultPatterns()
    {
        String[] defaultPatterns =
        {
            "patterns/test01.rle"
        };

        for(String s : defaultPatterns)
        {
            try
            {
                PatternLoader loader = new PatternLoader();
                Pattern pattern = loader.loadFromDisk(new File(s));
                addPattern(pattern);
            }
            catch (IOException | PatternFormatException e)
            {
                e.printStackTrace();
            }
        }

    }

    @FXML
    private void selectPattern()
    {
        if(selectedPattern != null)
            super.close();
    }

    @FXML
    private void loadURL()
    {
        String url = urlTextField.getText();
        if(url != null && url.length() > 0)
        {
            try
            {
                PatternLoader loader = new PatternLoader();
                Pattern pattern = loader.loadFromURL(url);
                addPattern(pattern);
            }
            catch (IOException | PatternFormatException e)
            {
                e.printStackTrace();
                urlTextField.setText("");
                //TODO: Add GUI to the exception-message.
            }
        }
    }

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
            {
                try
                {
                    PatternLoader loader = new PatternLoader();
                    Pattern pattern = loader.loadFromDisk(file);
                    addPattern(pattern);
                }
                catch (IOException | PatternFormatException e)
                {
                    e.printStackTrace();
                    //TODO: add a dialog to inform the user about what went wrong
                }
            }
        }
    }

    private void addPattern(Pattern pattern)
    {
        ImageView img = createTileImageFromPattern(pattern);
        img.setOnMouseClicked(event ->
        {
            selectedPattern = pattern;
            textArea.setText(pattern.getAllMetadata());

            // Clear the "selected" effect on all tiles
            for(Node n : tilePane.getChildren())
               n.setEffect(dropShadow);

            // Add the "selected" effect to the clicked tile
            img.setEffect(selected);
        });

        tilePane.getChildren().add(img);
    }

    private ImageView createTileImageFromPattern(Pattern pattern)
    {
        boolean[][] cellData = pattern.getCellData();

        WritableImage wimg = new WritableImage(cellData[0].length, cellData.length);
        PixelWriter pw =  wimg.getPixelWriter();

        for(int y=0; y <cellData.length; y++)
            for(int x=0; x <cellData[0].length; x++)
                pw.setColor(x, y, cellData[y][x] ? Color.BLACK : Color.rgb(244, 244, 244));

        ImageView imageView = new ImageView(wimg);
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(TILE_SIZE);
        imageView.setFitWidth(TILE_SIZE);
        imageView.setEffect(dropShadow);
        return imageView;
    }

    public Pattern getPattern()
    {
        return selectedPattern;
    }
}
