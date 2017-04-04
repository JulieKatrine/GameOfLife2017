package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import model.*;
import model.BoardIO.Pattern;
import view.BoardRenderer;
import view.BoardRendererImpl;

/**
 * Controls the screen in co-operation with UserInterface.fxml.
 *
 * Implements Initializable.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 * @see Initializable
 */
public class Controller implements Initializable
{
    private GameModel gameModel;
    private BoardRenderer boardRender;
    private Point lastMousePos;
    private BoardEditor boardEditor;
    private UpdateTimer updateTimer;
    private long drawTimer;

    @FXML private AnchorPane anchorPane;
    @FXML private Canvas canvas;
    @FXML private Slider cellSizeSlider;
    @FXML private Slider speedSlider;
    @FXML private MenuItem startStopMenuItem;
    @FXML private MenuItem nextMenuItem;

    /**
     * Called to initialize the controller after it's root element has been completely processed.
     *
     * @param location fill me in
     * @param resources fill me in
     * TODO: Finish me!
     */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        boardRender  = new BoardRendererImpl(canvas);
        boardEditor  = new BoardEditor(boardRender.getCamera());
        lastMousePos = new Point();
        gameModel    = new GameModel();
        updateTimer  = new UpdateTimer();

        boardRender.scaleViewToFitBoard(gameModel.getGameBoard());
        updateTimer.setDelayBetweenUpdates((int)(speedSlider.getMax() - speedSlider.getValue()));

        addEventListeners();
        drawBoard();
    }

    private void addEventListeners()
    {
        // Sets the action to be performed when the updateTimer fires.
        // Limits the draw rate to 60 fps
        updateTimer.setOnUpdateAction(() ->
        {
            gameModel.simulateNextGeneration();
            if(System.currentTimeMillis() > drawTimer + 16)
            {
                Platform.runLater(this::drawBoard);
                drawTimer = System.currentTimeMillis();
            }
        });

        // Moves the cellSizeSlider when the scroll-wheel is used
        canvas.setOnScroll((ScrollEvent event) ->
        {
            cellSizeSlider.adjustValue(cellSizeSlider.getValue() +  cellSizeSlider.getBlockIncrement() * Math.signum(event.getDeltaY()));
            drawBoard();
        });

        // Changes the camera-zoom when the cellSizeSlider is changed
        cellSizeSlider.valueProperty().addListener((ov, old_val, new_val) ->
        {
            boardRender.getCamera().setZoom(new_val.doubleValue());
            drawBoard();
        });

        /* Updates the timer delay when the speedSlider is changed.
        Higher slider value = smaller delay between updates */
        speedSlider.valueProperty().addListener((ov, old_val, new_val) ->
                updateTimer.setDelayBetweenUpdates((int)speedSlider.getMax() - new_val.intValue()));

        canvas.setOnMousePressed(event ->
        {
            /* Resets the last mouse position to the current mouse position.
            This is necessary to avoid "jumps" when moving the camera around */
            lastMousePos.x = (int)event.getX();
            lastMousePos.y = (int)event.getY();

            // Setts a cell alive when the mouse i pressed
            if(event.getButton() == MouseButton.PRIMARY)
            {
                boardEditor.edit(gameModel.getGameBoard(),
                        new Point((int)event.getX(), (int)event.getY()),
                        true);
                drawBoard();
            }
        });

        canvas.setOnMouseDragged(event ->
        {
            // Updates the camera position when the user drags the mouse and
            if(event.getButton() == MouseButton.SECONDARY)
            {
                double deltaX = (int)event.getX() - lastMousePos.x;
                double deltaY = (int)event.getY() - lastMousePos.y;
                boardRender.getCamera().move(gameModel.getGameBoard(), deltaX, deltaY);
                lastMousePos.x = (int)event.getX();
                lastMousePos.y = (int)event.getY();
            }

            // Setts a cell alive when the mouse i dragged over it
            else if(event.getButton() == MouseButton.PRIMARY)
            {
                boardEditor.edit(gameModel.getGameBoard(),
                        new Point((int)event.getX(), (int)event.getY()),
                        true);
            }

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
            canvas.setHeight(newValue.doubleValue() - 25);
            drawBoard();
        });
    }

    private void drawBoard()
    {
        boardRender.render(gameModel.getGameBoard());
    }

    /**
     * Handles the events of a user using the key-board.
     *
     * If the user presses "space", the program will call updateTimer.setRunning(),
     * and start/stop running depending on it's state.
     *
     * If the user presses "N", the method will call simulateNextGeneration().
     *
     * @param scene Takes in the programs current scene.
     */
    public void handleKeyEvent(Scene scene)
    {
        scene.setOnKeyPressed(event ->
        {
            if(event.getCode() == KeyCode.SPACE)
                startStopSimulation();
            else if(event.getCode() == KeyCode.N)
                simulateNextGeneration();
        });
    }

    @FXML private void simulateNextGeneration()
    {
        updateTimer.triggerUpdate();
    }

    @FXML private void loadNewGameBoard()
    {
        PatternChooserForm loader = new PatternChooserForm();
        loader.showAndWait();
        Pattern pattern = loader.getPattern();
        if(pattern != null)
        {
            gameModel.setGameBoard(pattern.getGameBoard());
            gameModel.setRule(pattern.getRule());
            boardRender.scaleViewToFitBoard(gameModel.getGameBoard());
            cellSizeSlider.setValue(boardRender.getCamera().getZoom());
            updateTimer.setRunning(false);
            pattern.getRule();
            drawBoard();
        }
    }

    @FXML private void saveGameBoard()
    {
        //Future feature
    }

    @FXML private void startStopSimulation()
    {
        updateTimer.setRunning(!updateTimer.isRunning());
        startStopMenuItem.setText(updateTimer.isRunning() ? "Stop" : "Start");
        nextMenuItem.setDisable(updateTimer.isRunning());
    }

    @FXML private void closeApplication()
    {
        Platform.exit();
    }

    @FXML private void createEmptyBoard()
    {
        gameModel.setGameBoard(new GameBoardDynamic(GameBoard.DEFAULT_BOARD_WIDTH, GameBoard.DEFAULT_BOARD_HEIGHT));
        boardRender.scaleViewToFitBoard(gameModel.getGameBoard());
        cellSizeSlider.setValue(boardRender.getCamera().getZoom());
        drawBoard();
    }
}