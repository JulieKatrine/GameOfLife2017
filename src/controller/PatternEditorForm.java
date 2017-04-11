package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;
import model.BoardEditor;
import model.GameBoard;
import model.GameBoardDynamic;
import view.BoardRenderer;
import view.BoardRendererImpl;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PatternEditorForm extends Stage implements Initializable
{
    private BoardRenderer boardRenderer;
    private BoardEditor boardEditor;
    private GameBoard selectedBoard;

    @FXML private Canvas canvas;

    public PatternEditorForm()
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/PatternEditorForm.fxml"));
            loader.setController(this);
            Parent root = loader.load();
            Scene scene = new Scene(root);
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

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        selectedBoard = new GameBoardDynamic(50,50);
        boardRenderer = new BoardRendererImpl(canvas);
        boardEditor = new BoardEditor(boardRenderer.getCamera());

        boardRenderer.scaleViewToFitBoard(selectedBoard);
        boardRenderer.render(selectedBoard);
    }

    private void addEventListeners()
    {
//        canvas.setOn
    }



}
