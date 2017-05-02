package controller;

import com.sun.javafx.css.Stylesheet;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

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
import model.BoardIO.PatternFormatException;
import model.BoardIO.RuleStringFormatter;
import model.Point;
import model.simulation.CustomRule;
import model.simulation.DefaultRuleSet;
import view.BoardRenderer;
import view.ColorProfile;

/**
 * The main controller of the application.
 * Handles all user interaction, simulation timing and board rendering.
 *
 * It Implements Initializable. All fields tagged with @FXML will be instantiated by
 * the JavaFx application thread when the associated FXML document is loaded.
 *
 * Business logic and board data is accessed through a private {@link GameModel} object. Board rendering
 * and editing is carried out through private {@link BoardRenderer} and {@link BoardEditor} objects.
 * An {@link UpdateTimer} is used for timed logic updates on the board data.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 * @see Initializable
 */
public class Controller implements Initializable
{
    private GameModel gameModel;
    private BoardRenderer boardRender;
    private BoardEditor boardEditor;
    private UpdateTimer updateTimer;

    private Point lastMousePos;
    private boolean controlPressed;
    private long drawTimer;

    @FXML private AnchorPane anchorPane;
    @FXML private Canvas canvas;
    @FXML private Slider cellSizeSlider;
    @FXML private Slider speedSlider;
    @FXML private MenuItem startStopMenuItem;
    @FXML private MenuItem nextMenuItem;
    @FXML private MenuItem reloadPatternMenuItem;
    @FXML private CheckMenuItem autoZoomMenuItem;
    @FXML private CheckMenuItem fullscreenMenuItem;
    @FXML private ToggleButton startStopButton;
    @FXML private ColorPicker deadCellColor;
    @FXML private ColorPicker livingCellColor;
    @FXML private Label ruleInfo;
    @FXML private Label speed;
    @FXML private ToolBar toolBar;
    @FXML private MenuBar menuBar;

    /**
     * Called to initialize the controller after its root element has been completely processed.
     * Instantiate and sets up the local objects, adds event listeners and draws the default board.
     *
     * @param location Relative paths for the root object.
     * @param resources Resources used to localize the root object.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        boardRender  = new BoardRenderer(canvas);
        boardEditor  = new BoardEditor(boardRender.getCamera());
        lastMousePos = new Point();
        gameModel    = new GameModel();
        updateTimer  = new UpdateTimer();

        boardRender.setColorProfile(new ColorProfile(Color.gray(0.949), Color.gray(0.0902), Color.GRAY));
        boardRender.scaleViewToFitBoard(gameModel.getGameBoard());
        updateTimer.setDelayBetweenUpdates((int)(speedSlider.getMax() - speedSlider.getValue()));
        deadCellColor.setValue((Color)boardRender.getColorProfile().getDeadColor());
        livingCellColor.setValue((Color)boardRender.getColorProfile().getAliveColor());

        scaleViewToFitBoard();
        Platform.runLater(() -> addEventListeners());
    }

    private void addEventListeners()
    {
        // Allows the user to change the pattern-colors.
        deadCellColor.setOnMouseExited(event -> canvas.requestFocus());
        deadCellColor.setOnAction(event ->
        {
            boardRender.getColorProfile().setDeadColor(deadCellColor.getValue());
            drawBoard();
        });

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
            int FPS = 60;
            gameModel.simulateNextGeneration();
            if(System.currentTimeMillis() > drawTimer + (1000 / FPS))
            {
                Platform.runLater(this::drawBoard);
                Platform.runLater(this::generationsPerSecond);
                drawTimer = System.currentTimeMillis();
            }
        });

        // Moves the camera, If CTRL is pressed vertical scroll changes the camera zoom.
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

        // Changes the camera-zoom when the cellSizeSlider is changed.
        cellSizeSlider.valueProperty().addListener((ov, old_val, new_val) ->
        {
            boardRender.getCamera().setZoom(new_val.doubleValue());
            drawBoard();
        });

        /* Updates the timer delay when the speedSlider is changed.
        Higher slider value = smaller delay between updates */
        speedSlider.valueProperty().addListener((ov, old_val, new_val) ->
                updateTimer.setDelayBetweenUpdates((int)speedSlider.getMax() - new_val.intValue()));


        // Sets the stage to fullscreen when the fullscreen MenuItem is selected.
        fullscreenMenuItem.setOnAction((ActionEvent event) ->
                setFullscreen(fullscreenMenuItem.selectedProperty().getValue()));

        // Edits and redraws the board when the mouse is pressed over a cell.
        canvas.setOnMousePressed(event ->
        {
            /* Resets the last mouse position to the current mouse position.
            This is necessary to avoid "jumps" when moving the camera around */
            lastMousePos.x = (int)event.getX();
            lastMousePos.y = (int)event.getY();

            editBoard(event);
            drawBoard();
        });

        // Edits the board if left or right mouse button is pressed while dragging.
        // Moves the camera if the scroll wheel is pressed while dragging.
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

        // Increases the board size if a cell was set alive near the edge and the board is of type dynamic.
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
        anchorPane.getScene().widthProperty().addListener((o, oldValue, newValue) ->
        {
            canvas.setWidth(newValue.doubleValue());
            drawBoard();
        });
        
        // Updates the canvas height when the window is resized
        anchorPane.getScene().heightProperty().addListener((o, oldValue, newValue) ->
        {
            canvas.setHeight(newValue.doubleValue() - toolBar.getPrefHeight() - menuBar.getHeight());
            drawBoard();
        });

        // Handles all key events and shortcuts.
        anchorPane.getScene().setOnKeyPressed(event ->
        {
            KeyCode code = event.getCode();
            if(code == KeyCode.SPACE)
                startStopSimulation();
            else if(code == KeyCode.N)
                simulateNextGeneration();
            else if(code == KeyCode.C)
                clearBoard();
            else if(code == KeyCode.O && controlPressed)
                loadNewGameBoard();
            else if(code == KeyCode.S && controlPressed)
                saveGameBoard();
            else if(code == KeyCode.CONTROL)
                controlPressed = true;
            else if(code == KeyCode.R)
                reloadPattern();
            else if(code == KeyCode.F)
                setFullscreen(!fullscreenMenuItem.isSelected());
            else if(code == KeyCode.ESCAPE && fullscreenMenuItem.isSelected())
                setFullscreen(false);
        });

        anchorPane.getScene().setOnKeyReleased(event ->
        {
            if(event.getCode() == KeyCode.CONTROL)
                controlPressed = false;
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

    private void setFullscreen(boolean state)
    {
        fullscreenMenuItem.selectedProperty().setValue(state);
        ((Stage) anchorPane.getScene().getWindow()).setFullScreen(state);
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
        stopSimulation();
        PatternChooserForm loader = new PatternChooserForm();
        loader.initModality(Modality.WINDOW_MODAL);
        loader.initOwner(anchorPane.getScene().getWindow());
        loader.showAndWait();
        Pattern pattern = loader.getSelectedPattern();
        if(pattern != null)
        {
            gameModel.setGameBoard(pattern.getGameBoard());
            gameModel.setRule(pattern.getRule());
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
        event.getDragboard().getFiles().forEach(PatternChooserForm::addFileToLoadingQueue);
        Platform.runLater(this::loadNewGameBoard);
    }

    @FXML private void startStopSimulation()
    {
        updateTimer.setRunning(!updateTimer.isRunning());
        startStopMenuItem.setText(updateTimer.isRunning() ? "Stop" : "Start");
        startStopButton.setSelected(updateTimer.isRunning());
        nextMenuItem.setDisable(updateTimer.isRunning());
        speed.setText("0 g/s");
    }

    private void stopSimulation()
    {
        if(updateTimer.isRunning())
            startStopSimulation();
    }

    @FXML private void clearBoard()
    {
        gameModel.setGameBoard(new GameBoardDynamic(GameBoard.DEFAULT_BOARD_WIDTH, GameBoard.DEFAULT_BOARD_HEIGHT));
        stopSimulation();
        scaleViewToFitBoard();
        drawBoard();
    }

    @FXML private void informationBox(String title, String headerText, String contentText)
    {
        stopSimulation();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(GameOfLife.APPLICATION_ICON);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/view/AlertStyleSheet").toExternalForm());
        dialogPane.getStyleClass().add("alert");

        dialogPane.setPrefWidth(450);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);

        alert.showAndWait();
    }

    @FXML private void gettingStarted()
    {
        stopSimulation();
        informationBox(
                "Getting Started",
                "Getting Started",
                "Use the left " +
                "and right mouse buttons to edit the board. Click the mouse-wheel to move around and " +
                "ctrl+scroll to zoom.\n(If you are using a touch pad: scroll to move around).\n\n" +

                "Open the \"File Chooser\" (ctrl+o) to select a pattern from your disk, " +
                "a URL or a pre-loaded pattern. Start the simulation with the \"Play\"-button on the toolbar " +
                "(or with space). You can change the board-colors and change or create your own rules.\n" +
                "If you want to save your pattern choose: \"File\">\"Edit and save\" (ctrl+s).\n\n");
    }

    @FXML private void shortcuts()
    {
        stopSimulation();
        informationBox(
                "Shortcuts",
                "Shortcuts:",
                "Play/pause: SPACE\n" +
                "Next: N\n" +
                "Reload: R\n" +
                "Clear board: C\n" +
                "Full screen: F\n" +
                "Save: CTRL+S\n" +
                "Open pattern: CTRL+O\n" +
                "Zoom: CTRL+SCROLL\n");
    }

    @FXML private void about()
    {
        stopSimulation();
        informationBox(
                "About",
                "Information",
                "This game was created by Niklas Johansen (s306603) " +
                "and Julie Katrine Høvik (s236518) in the spring of 2017.");
    }

    @FXML private void setNewRuleFromFXML(Event event) throws PatternFormatException
    {
        MenuItem item = (MenuItem) event.getSource();
        String ruleFromFXML = RuleStringFormatter.format(item.getId());
        ruleInfo.setText("Rule: " + item.getText() + " - " + ruleFromFXML);
        setNewRule(ruleFromFXML);
    }

    @FXML private void customChangeRule()
    {
        stopSimulation();
        CustomRuleCreator customRuleCreator = new CustomRuleCreator();
        customRuleCreator.showAndWait();
        String rule = customRuleCreator.getRuleString();
        if(!rule.isEmpty())
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
        stopSimulation();
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