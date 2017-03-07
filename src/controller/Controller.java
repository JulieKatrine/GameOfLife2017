package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.Slider;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import model.GameModel;
import model.Point;
import view.BoardRenderer;
import view.BoardRendererImpl;

/**
 * This class controls the first screen.
 * Description.... ex.
 */

public class Controller implements Initializable, UpdatableObject
{
    private GameModel gameModel;
    private BoardRenderer boardRender;
    private UpdateTimer updateTimer;
    private Point lastMousePos;

    @FXML private AnchorPane anchorPane;
    @FXML private Canvas canvas;
    @FXML private Slider cellSizeSlider;
    @FXML private Slider speedSlider;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        boardRender  = new BoardRendererImpl(canvas);
        updateTimer  = new UpdateTimer(this);
        gameModel    = new GameModel();
        lastMousePos = new Point();

        addEventListeners();
        loadNewGameBoard();
    }

    @Override
    public void triggerControllerUpdate()
    {
        gameModel.simulateNextGeneration();
        drawBoard(); //kaller på boardRender i hjelpemetode under
    }

    private void drawBoard()
    {
        boardRender.render(gameModel.getGameBoard());
    }

    @FXML private void simulateNextGeneration()
    {
        triggerControllerUpdate();
    }

    @FXML private void loadNewGameBoard()
    {
        gameModel.loadNewRandomBoard(50,50);
        drawBoard();
    }

    @FXML private void saveGameBoard()
    {
        //Future feature
    }

    @FXML private void startSimulation()
    {
        updateTimer.start();
    }

    @FXML private void stopSimulation()
    {
        updateTimer.stop();
    }

    @FXML private void closeApplication()
    {
        Platform.exit();
    }

    private void addEventListeners()
    {
        // Moves the cellSizeSlider when the scroll-wheel is used
        canvas.setOnScroll((ScrollEvent event) ->
        {
            cellSizeSlider.adjustValue(cellSizeSlider.getValue() +  cellSizeSlider.getBlockIncrement() * Math.signum(event.getDeltaY()));
            drawBoard();
        });


        // Changes the camera-zoom when the cellSizeSlider is changed
        cellSizeSlider.valueProperty().addListener((ov, old_val, new_val) ->
        {
            boardRender.getCamera().setZoom(new_val.intValue());
            drawBoard();
        });


        // Updates the timer delay when the speedSlider is changed
        // Higher slider value = smaller delay between updates
        speedSlider.valueProperty().addListener((ov, old_val, new_val) ->
                updateTimer.setDelayBetweenUpdates((int)speedSlider.getMax() - new_val.intValue()));


        // Resets the last mouse position to the current mouse position
        // This is necessary to avoid "jumps" when moving the camera around
        canvas.setOnMousePressed(event ->
        {
            lastMousePos.x = (int)event.getX();
            lastMousePos.y = (int)event.getY();
        });


        // Updates the camera position when the user drags the mouse
        canvas.setOnMouseDragged(event ->
        {
            double deltaX = (int)event.getX() - lastMousePos.x;
            double deltaY = (int)event.getY() - lastMousePos.y;
            boardRender.getCamera().move(deltaX, deltaY);
            lastMousePos.x = (int)event.getX();
            lastMousePos.y = (int)event.getY();
            drawBoard();
        });


        // Updates the canvas width when the window is resized
        anchorPane.prefWidthProperty().addListener((o, oldValue, newValue) ->
        {
            canvas.setWidth(newValue.doubleValue());
            drawBoard();
        });


        // Updates the canvas height when the window is resized
        anchorPane.prefHeightProperty().addListener((o, oldValue, newValue) ->
        {
            canvas.setHeight(newValue.doubleValue());
            drawBoard();
        });
    }
}

