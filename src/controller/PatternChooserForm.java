package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PatternChooserForm extends Stage implements Initializable
{
    @FXML
    private ImageView imageView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        imageView.setImage(new Image("/resources/Picture.jpg"));

    }

    public PatternChooserForm() throws IOException
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);

        Parent root = loader.load(getClass().getResource("../view/PatternChooserForm.fxml"));
        Scene scene = new Scene(root);



        super.setTitle("Choose your pattern");
        super.setScene(scene);
    }

    @FXML
    private void openFileChooser()
    {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(this);
    }

}
