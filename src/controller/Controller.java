package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.*;
import model.BoardIO.Pattern;
import model.Point;
import model.simulation.CustomRule;
import model.simulation.DefaultRuleSet;
import view.BoardRenderer;
import view.ColorProfile;

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
    private boolean controlPressed;

    @FXML private AnchorPane anchorPane;
    @FXML private Canvas canvas;
    @FXML private Slider cellSizeSlider;
    @FXML private Slider speedSlider;
    @FXML private MenuItem startStopMenuItem;
    @FXML private MenuItem nextMenuItem;
    @FXML private MenuItem reloadPatternMenuItem;
    @FXML private CheckMenuItem autoZoomMenuItem;
    @FXML private ToggleButton startStopButton;
    @FXML private ColorPicker deadCellColor;
    @FXML private ColorPicker livingCellColor;
    @FXML private Label ruleInfo;
    @FXML private Label speed;

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
        boardRender  = new BoardRenderer(canvas);
        boardEditor  = new BoardEditor(boardRender.getCamera());
        lastMousePos = new Point();
        gameModel    = new GameModel();
        updateTimer  = new UpdateTimer();

        boardRender.setColorProfile(new ColorProfile(Color.BLACK, Color.color(0.0275, 0.9882, 0), Color.GRAY));
        boardRender.setColorProfile(new ColorProfile(Color.gray(0.949), Color.gray(0.0902), Color.GRAY));
        boardRender.scaleViewToFitBoard(gameModel.getGameBoard());
        updateTimer.setDelayBetweenUpdates((int)(speedSlider.getMax() - speedSlider.getValue()));

        scaleViewToFitBoard();
        addEventListeners();
    }

    private void addEventListeners()
    {
        //TODO: Put the label in the box, not on the side. I think css is the way to go.

        // Allows the user to change the pattern-colors.
        deadCellColor.setValue((Color)boardRender.getColorProfile().getDeadColor());
        deadCellColor.setOnMouseExited(event -> canvas.requestFocus());
        deadCellColor.setOnAction(event ->
        {
            boardRender.getColorProfile().setDeadColor(deadCellColor.getValue());
            drawBoard();
        });

        livingCellColor.setValue((Color)boardRender.getColorProfile().getAliveColor());
        livingCellColor.setOnMouseExited(event -> canvas.requestFocus());
        livingCellColor.setOnAction(event ->
        {
            boardRender.getColorProfile().setAliveColor(livingCellColor.getValue());
            drawBoard();
        });

        // Sets the action to be performed when the updateTimer fires.
        // Limits the draw rate to 60 fps
        updateTimer.setOnUpdateAction(() ->
        {
            gameModel.simulateNextGeneration();
            if(System.currentTimeMillis() > drawTimer + 16)
            {
                Platform.runLater(this::drawBoard);
                Platform.runLater(this::generationsPerSecond);
                drawTimer = System.currentTimeMillis();
            }
        });

        // Moves the camera with horizontal and vertical scroll,
        // If CTRL is pressed vertical scroll changes the camera zoom
        canvas.setOnScroll((ScrollEvent event) ->
        {
            if(controlPressed)
                cellSizeSlider.adjustValue(cellSizeSlider.getValue() + cellSizeSlider.getBlockIncrement() * Math.signum(event.getDeltaY()));
            else
            {
                boardRender.getCamera().move(gameModel.getGameBoard(), event.getDeltaX(), event.getDeltaY());
                disableAutoZoom();
            }

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

            editBoard(event);
            drawBoard();
        });

        canvas.setOnMouseDragged(event ->
        {
            editBoard(event);

            if(event.getButton() == MouseButton.MIDDLE)
            {
                double deltaX = (int)event.getX() - lastMousePos.x;
                double deltaY = (int)event.getY() - lastMousePos.y;
                boardRender.getCamera().move(gameModel.getGameBoard(), deltaX, deltaY);
                lastMousePos.x = (int)event.getX();
                lastMousePos.y = (int)event.getY();
                disableAutoZoom();
            }

            drawBoard();
        });

        canvas.setOnMouseReleased(event ->
        {
            GameBoard board = gameModel.getGameBoard();
            if(board instanceof GameBoardDynamic && !updateTimer.isRunning())
            {
                ((GameBoardDynamic)board).increaseBoardSizeIfNecessary();
                drawBoard();
            }
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
            canvas.setHeight(newValue.doubleValue() - 45 - 30);
            drawBoard();
        });
    }

    private void generationsPerSecond()
    {
        int speed = gameModel.getSimulator().getGenerationsPerSecond();
        this.speed.setText(updateTimer.isRunning() ? speed + " g/s" : "0 g/s");
    }

    private void editBoard(MouseEvent event)
    {
        MouseButton button = event.getButton();
        if(button == MouseButton.PRIMARY || button == MouseButton.SECONDARY)
        {
            double deltaX = event.getX() - lastMousePos.x;
            double deltaY = event.getY() - lastMousePos.y;
            lastMousePos.x = (int)event.getX();
            lastMousePos.y = (int)event.getY();

            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            double stepDist = distance / boardRender.getCamera().getZoom();

            for(double i = 0.0;  i < 1.0; i += 1.0 / stepDist)
            {
                int x = (int)(event.getX() - (i * deltaX));
                int y = (int)(event.getY() - (i * deltaY));
                boardEditor.edit(gameModel.getGameBoard(), new Point(x,y), button == MouseButton.PRIMARY);
            }
        }
    }

    private void scaleViewToFitBoard()
    {
        boardRender.scaleViewToFitBoard(gameModel.getGameBoard());
        cellSizeSlider.setValue(boardRender.getCamera().getZoom());
        drawBoard();
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
            KeyCode code = event.getCode();
            if(code == KeyCode.SPACE)
                startStopSimulation();
            else if(code == KeyCode.N)
                simulateNextGeneration();
            else if(code == KeyCode.O)
                loadNewGameBoard();
            else if(code == KeyCode.C)
                clearBoard();
            else if(code == KeyCode.S && controlPressed)
                saveGameBoard();
            else if(code == KeyCode.CONTROL)
                controlPressed = true;
            else if(code == KeyCode.R)
                reloadPattern();
        });

        scene.setOnKeyReleased(event ->
        {
            if(event.getCode() == KeyCode.CONTROL)
                controlPressed = false;
        });
    }

    @FXML private void enableGridRendering(ActionEvent event)
    {
        CheckMenuItem item = (CheckMenuItem) event.getSource();
        boardRender.getColorProfile().setGridRendering(item.isSelected());
        drawBoard();
    }

    @FXML private void autoZoomAction(ActionEvent event)
    {
        CheckMenuItem item = (CheckMenuItem) event.getSource();
        boardRender.setScaleViewOnRender(item.isSelected());
        if(item.isSelected())
            scaleViewToFitBoard();
    }

    private void disableAutoZoom()
    {
        boardRender.setScaleViewOnRender(false);
        autoZoomMenuItem.setSelected(false);
    }

    @FXML private void trimBoardToSize()
    {
        GameBoard trimmedBoard = gameModel.getGameBoard().trimmedCopy(1);
        gameModel.setGameBoard(trimmedBoard);
        scaleViewToFitBoard();
    }

    @FXML private void simulateNextGeneration()
    {
        updateTimer.triggerUpdate();
    }

    @FXML protected void loadNewGameBoard()
    {
        PatternChooserForm loader = new PatternChooserForm();
        loader.initModality(Modality.WINDOW_MODAL);
        loader.initOwner(anchorPane.getScene().getWindow());
        loader.showAndWait();
        Pattern pattern = loader.getSelectedPattern();
        if(pattern != null)
        {
            gameModel.setGameBoard(pattern.getGameBoard());
            gameModel.setRule(pattern.getRule());
            updateTimer.setRunning(false);
            startStopMenuItem.setText("Start");
            nextMenuItem.setDisable(false);
            reloadPatternMenuItem.setDisable(false);
            ruleInfo.setText("Rule: " + pattern.getRuleString());
            scaleViewToFitBoard();
            drawBoard();
        }
    }

    @FXML private void saveGameBoard()
    {
        if(updateTimer.isRunning())
            startStopSimulation();

        PatternEditorForm editorForm = new PatternEditorForm(gameModel.getGameBoard(), gameModel.getSimulator());
        editorForm.setColorProfile(boardRender.getColorProfile());
        editorForm.initModality(Modality.WINDOW_MODAL);
        editorForm.initOwner(anchorPane.getScene().getWindow());
        editorForm.showAndWait();
        GameBoard selectedGameBoard = editorForm.getSelectedGameBoard();
        if(selectedGameBoard != null)
        {
            gameModel.setGameBoard(selectedGameBoard);
            boardRender.getCamera().reset();
            boardRender.scaleViewToFitBoard(selectedGameBoard);
            drawBoard();
        }
    }

    @FXML private void reloadPattern()
    {
        Pattern pattern = PatternChooserForm.getSelectedPattern();
        if(pattern != null)
        {
            gameModel.setGameBoard(PatternChooserForm.getSelectedPattern().getGameBoard());
            gameModel.setRule(pattern.getRule());
            scaleViewToFitBoard();
            drawBoard();
        }
    }

    /**
     * The method is called when the user hovers a file over the application window.
     * It accepts the action and a "move" GUI-element will show that this application
     * accepts this type of file loading. The transfer will not be accepted if a
     * PatternChooserForm is already opened.
     * @param event The DragEvent
     */
    @FXML private void fileOver(DragEvent event)
    {
        Dragboard board = event.getDragboard();
        if (board.hasFiles() && !PatternChooserForm.isOpened())
            event.acceptTransferModes(TransferMode.ANY);
    }

    /**
     * The method is called when the user drops one or more files over the application window.
     * It reads in the files and adds them to the PatternChooserForms loading queue.
     * The PatternChooserForms will the be opened.
     * @param event The DragEvent
     */
    @FXML private void fileDropped(DragEvent event)
    {
        event.getDragboard().getFiles().forEach(file -> PatternChooserForm.addFileToLoadingQueue(file));
        Platform.runLater(() -> loadNewGameBoard());
    }

    @FXML private void startStopSimulation()
    {
        updateTimer.setRunning(!updateTimer.isRunning());
        startStopMenuItem.setText(updateTimer.isRunning() ? "Stop" : "Start");
        startStopButton.setSelected(updateTimer.isRunning());
        nextMenuItem.setDisable(updateTimer.isRunning());
        speed.setText("0 g/s");
    }

    @FXML private void clearBoard()
    {
        gameModel.setGameBoard(new GameBoardDynamic(GameBoard.DEFAULT_BOARD_WIDTH, GameBoard.DEFAULT_BOARD_HEIGHT));
        if(updateTimer.isRunning())
            startStopSimulation();
        scaleViewToFitBoard();
        drawBoard();
    }

    @FXML private void shortCuts()
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        ((Stage)alert.getDialogPane().getScene().getWindow()).getIcons().add(GameOfLife.APPLICATION_ICON);
        alert.setTitle("About");
        alert.setHeaderText("How to create your own pattern");
        alert.setContentText("Use the left mouse button to draw, the right mouse button to erase.\n\n" +
                "If you are using a mouse:\n" +
                "Click the wheel to move around and scroll to zoom.\n\n" +
                "If you are using a touch pad:\n" +
                "Scroll to move around, and ctrl+scroll to zoom.");

        alert.showAndWait();
    }

    @FXML private void setNewRuleFromFXML(Event event)
    {
        String ruleFromFXML = ((MenuItem)event.getSource()).getId().trim().replaceAll("S", "/S");
        ruleInfo.setText("Rule: " + ((MenuItem) event.getSource()).getText() + " - " + ruleFromFXML);
        setNewRule(ruleFromFXML);
    }

    @FXML private void customChangeRule()
    {
        CustomRuleCreator customRuleCreator = new CustomRuleCreator();
        customRuleCreator.showAndWait();
        String rule = customRuleCreator.getRuleString();
        if(rule.length() > 0)
        {
            setNewRule(rule);
            ruleInfo.setText("Rule: " + rule);
        }
    }

    private void setNewRule(String rule)
    {
        if (rule.equals("B3/S23"))
            gameModel.setRule(new DefaultRuleSet());
        else
            gameModel.setRule(new CustomRule(rule));
    }

    //TODO: Activate me!!
    public void closeRequest()
    {
        closeApplication();
/*
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(GameOfLife.APPLICATION_ICON);
        alert.setTitle("Exit");
        alert.setHeaderText("Are you sure you want to exit?");
        ButtonType ExitButton = new ButtonType("Exit");
        ButtonType Cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType fileChooser = new ButtonType("Open file chooser");
        alert.getButtonTypes().setAll(ExitButton, fileChooser, Cancel);

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ExitButton)
            closeApplication();
        else if (result.get() == fileChooser)
            loadNewGameBoard();
*/
    }

    @FXML private void closeApplication()
    {
        Platform.exit();
        System.exit(0);
    }
}