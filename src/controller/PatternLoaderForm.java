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

public class PatternLoaderForm extends Stage
{
    public PatternLoaderForm() throws IOException
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);

        VBox root = loader.load(getClass().getResource("../view/PatternLoaderForm.fxml"));
        Scene scene = new Scene(root);

        super.setTitle("Game of Life");
        super.setScene(scene);

      //  FileChooser fileChooser = new FileChooser();
       // File file = fileChooser.showOpenDialog(this);

       // if(file != null)
        //    System.out.println("God stemning!!!");

    }

    @FXML
    private void loadPressed()
    {
        super.close();
    }





}
