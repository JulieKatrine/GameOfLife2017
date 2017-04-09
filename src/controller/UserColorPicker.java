package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.BoardIO.Pattern;
import view.BoardRenderer;
import view.BoardRendererImpl;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 */
public class UserColorPicker extends Stage implements Initializable {
    private Color color;
    private ColorPicker colorPicker;
    private BoardRendererImpl boardRenderer;

    public UserColorPicker(javafx.scene.canvas.Canvas canvas)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/ColorPicker.fxml"));
            loader.setController(this);
            AnchorPane root = loader.load();
            Scene scene = new Scene(root);
            super.setTitle("Color Picker");
            super.getIcons().add(GameOfLife.APPLICATION_ICON);
            super.setScene(scene);

            boardRenderer = new BoardRendererImpl(canvas);

            super.setOnCloseRequest(Event ->
                    super.close());
        } catch (IOException e)
        {
            e.printStackTrace();
            System.err.println("Failed to load FXML");
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    public void changeBackgroundColor()
    {
        boardRenderer.setDeadCellColor(colorPicker.getValue());
    }

    @FXML
    public void changeCellColor()
    {
        boardRenderer.setAliveCellColor(colorPicker.getValue());
    }
}
