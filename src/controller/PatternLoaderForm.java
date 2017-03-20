package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * Controls the screen for the PatternLoaderForm.fxml.
 *
 * This is a new window that opens when a new pattern is loaded from a file or URL,
 * where the user can choose a name for the pattern and save it.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 */
public class PatternLoaderForm extends Stage
{
    /**
     *
     * @throws IOException as it takes input from the FileChooser.
     */
    public PatternLoaderForm() throws IOException
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);

        VBox root = loader.load(getClass().getResource("../view/PatternLoaderForm.fxml"));
        Scene scene = new Scene(root);

        super.setTitle("Game of Life");
        super.setScene(scene);

        //TODO: Consider a board-chooser prompt
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(this);

    }

    @FXML
    private void loadPressed()
    {
        super.close();
    }





}
