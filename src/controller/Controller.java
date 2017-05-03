package controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;

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
import model.patternIO.Pattern;
import model.patternIO.PatternFormatException;
import model.patternIO.RuleStringFormatter;
import model.Point;
import model.simulation.CustomRule;
import model.simulation.DefaultRule;
import view.BoardRenderer;
import view.ColorProfile;

/**
 * The main controller of the application.
 * Handles all user interactions, simulation timing and board rendering in the main application window.
 *
 * All fields tagged with @FXML will be instantiated by
 * the JavaFx application thread when the associated FXML document is loaded.
 *
 * Business logic and board data is accessed through a private {@link GameModel} object. Board rendering
 * and editing is carried out through private {@link BoardRenderer} and {@link BoardEditor} objects.
 * An {@link UpdateTimer} is used for timed logic updates on the board data.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 */
public class Controller
{
    private GameModel gameModel;
    private BoardRenderer boardRenderer;
    private BoardEditor boardEditor;
    private UpdateTimer updateTimer;

    private Point lastMousePos;
    private boolean controlPressed;
    private long drawTimer;

    @FXML private AnchorPane anchorPane;
    @FXML private Canvas canvas;
    @FXML private Slider cellSizeSlider;
    @FXML private Slider speedSlider;
    @FXML private MenuBar menuBar;
    @FXML private MenuItem startStopMenuItem;
    @FXML private MenuItem nextMenuItem;
    @FXML private MenuItem reloadPatternMenuItem;
    @FXML private CheckMenuItem autoZoomMenuItem;
    @FXML private CheckMenuItem fullscreenMenuItem;
    @FXML private ColorPicker livingCellColor;
    @FXML private ColorPicker deadCellColor;
    @FXML private ToggleButton startStopButton;
    @FXML private ToolBar toolBar;
    @FXML private Label ruleInfo;
    @FXML private Label speed;

    /**
     * Called to initialize the controller after its root element has been completely processed.
     * Instantiate and sets up the local objects, adds event listeners and draws the default board.
     */
    @FXML
    public void initialize()
    {
        boardRenderer = new BoardRenderer(canvas);
        boardEditor   = new BoardEditor(boardRenderer.getCamera());
        lastMousePos  = new Point();
        gameModel     = new GameModel();
        updateTimer   = new UpdateTimer();

        boardRenderer.setColorProfile(new ColorProfile(Color.gray(0.949), Color.gray(0.0902), Color.GRAY));
        boardRenderer.scaleViewToFitBoard(gameModel.getGameBoard());
        updateTimer.setDelayBetweenUpdates((int)(speedSlider.getMax() - speedSlider.getValue()));
        deadCellColor.setValue((Color) boardRenderer.getColorProfile().getDeadColor());
        livingCellColor.setValue((Color) boardRenderer.getColorProfile().getAliveColor());

        scaleViewToFitBoard();
        Platform.runLater(this::addEventListeners);
    }

    private void addEventListeners()
    {
        // Allows the user to change the pattern-colors.
        deadCellColor.setOnMouseExited(event -> canvas.requestFocus());
        deadCellColor.setOnAction(event ->
        {
            boardRenderer.getColorProfile().setDeadColor(deadCellColor.getValue());
            drawBoard();
        });

        livingCellColor.setOnMouseExited(event -> canvas.requestFocus());
        livingCellColor.setOnAction(event ->
        {
            boardRenderer.getColorProfile().setAliveColor(livingCellColor.getValue());
            drawBoard();
        });

        // Sets the action to be performed when the updateTimer fires.
        // Limits the draw rate to 60 frames per second
        updateTimer.setOnUpdateAction(() ->
        {
            int FPS = 60;
            gameModel.simulateNextGeneration();
            if(System.currentTimeMillis() > drawTimer + (1000 / FPS))
            {
                Platform.runLater(this::drawBoard);
                Platform.runLater(this::setGenerationsPerSecondLabel);
                drawTimer = System.currentTimeMillis();
            }
        });

        // Moves the camera, If CTRL is pressed vertical scroll changes the camera zoom.
        canvas.setOnScroll((ScrollEvent event) ->
        {
            if(controlPressed)
                cellSizeSlider.adjustValue(cellSizeSlider.getValue() +
                        cellSizeSlider.getBlockIncrement() * Math.signum(event.getDeltaY()));
            else
            {
                boardRenderer.getCamera().move(gameModel.getGameBoard(), event.getDeltaX(), event.getDeltaY());
                disableAutoZoom();
            }
            drawBoard();
        });

        // Changes the camera-zoom when the cellSizeSlider is changed.
        cellSizeSlider.valueProperty().addListener((ov, old_val, new_val) ->
        {
            boardRenderer.getCamera().setZoom(new_val.doubleValue());
            drawBoard();
        });

        // Updates the timer delay when the speedSlider is changed.
        // Higher slider value gives a smaller delay between updates.
        speedSlider.valueProperty().addListener((ov, old_val, new_val) ->
                updateTimer.setDelayBetweenUpdates((int)speedSlider.getMax() - new_val.intValue()));

        // Sets the Stage to fullscreen when the fullscreen MenuItem is selected.
        fullscreenMenuItem.setOnAction((ActionEvent event) ->
                setFullscreen(fullscreenMenuItem.selectedProperty().getValue()));

        // Edits and redraws the board when the mouse is pressed over a cell.
        canvas.setOnMousePressed(event ->
        {
            // Resets the last mouse position to the current mouse position.
            // This is necessary to avoid "jumps" when moving the camera around.
            lastMousePos.x = (int)event.getX();
            lastMousePos.y = (int)event.getY();

            editBoard(event);
            drawBoard();
        });

        // Edits the board if the left or right mouse button is pressed while dragging.
        // Moves the camera if the scroll wheel is pressed while dragging.
        canvas.setOnMouseDragged(event ->
        {
            editBoard(event);

            if(event.getButton() == MouseButton.MIDDLE)
            {
                double deltaX = (int)event.getX() - lastMousePos.x;
                double deltaY = (int)event.getY() - lastMousePos.y;
                boardRenderer.getCamera().move(gameModel.getGameBoard(), deltaX, deltaY);
                lastMousePos.x = (int)event.getX();
                lastMousePos.y = (int)event.getY();
                disableAutoZoom();
            }

            drawBoard();
        });

        // Increases the board size if a cell has been set alive near the edge and the board is of type dynamic.
        canvas.setOnMouseReleased(event ->
        {
            GameBoard board = gameModel.getGameBoard();
            if(board instanceof GameBoardDynamic && !updateTimer.isRunning())
            {
                ((GameBoardDynamic)board).increaseBoardSizeIfNecessary();
                drawBoard();
            }
        });

        // Updates the canvas width when the window is resized.
        anchorPane.getScene().widthProperty().addListener((o, oldValue, newValue) ->
        {
            canvas.setWidth(newValue.doubleValue());
            drawBoard();
        });
        
        // Updates the canvas height when the window is resized.
        anchorPane.getScene().heightProperty().addListener((o, oldValue, newValue) ->
        {
            canvas.setHeight(newValue.doubleValue() - toolBar.getPrefHeight() - menuBar.getHeight());
            drawBoard();
        });

        // Handles key events and openShortcutDialog.
        anchorPane.getScene().setOnKeyPressed(event ->
        {
            KeyCode code = event.getCode();
            if(code == KeyCode.SPACE)
                startStopSimulation();
            else if(code == KeyCode.N)
                simulateNextGeneration();
            else if(code == KeyCode.C)
                clearBoard();
            else if(code == KeyCode.R)
                reloadGameBoard();
            else if(code == KeyCode.F)
                setFullscreen(!fullscreenMenuItem.isSelected());
            else if(code == KeyCode.O && controlPressed)
                loadNewGameBoard();
            else if(code == KeyCode.S && controlPressed)
                saveGameBoard();
            else if(code == KeyCode.CONTROL)
                controlPressed = true;
            else if(code == KeyCode.ESCAPE && fullscreenMenuItem.isSelected())
                setFullscreen(false);
        });

        // Updates the state of the CTRL button.
        anchorPane.getScene().setOnKeyReleased(event ->
        {
            if(event.getCode() == KeyCode.CONTROL)
                controlPressed = false;
        });
    }



    /* -------------------- RUNS, UPDATES AND CLOSES -------------------- */

    @FXML private void simulateNextGeneration()
    {
        updateTimer.triggerUpdate();
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

    //TODO: Activate me!! And add correct style class to cancel button!
    public void closeRequest()
    {
        closeApplication();
        /*
        stopSimulation();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(GameOfLife.APPLICATION_ICON);
        alert.setTitle("Exit");
        alert.setHeaderText("Are you sure you want to exit?");
        ButtonType exitButton = new ButtonType("Exit");
        ButtonType cancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType fileChooser = new ButtonType("Open file chooser");
        alert.getButtonTypes().setAll(exitButton, fileChooser, cancel);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/view/layout/AlertStyleSheet.css").toExternalForm());
        dialogPane.getStyleClass().add("alert");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == exitButton)
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



    /* -------------------- SCALE, VIEW AND RENDER -------------------- */

    private void drawBoard()
    {
        boardRenderer.render(gameModel.getGameBoard());
    }

    @FXML private void enableGridRendering(ActionEvent event)
    {
        CheckMenuItem item = (CheckMenuItem) event.getSource();
        boardRenderer.getColorProfile().setGridRendering(item.isSelected());
        drawBoard();
    }

    @FXML private void autoZoomState(ActionEvent event)
    {
        CheckMenuItem item = (CheckMenuItem) event.getSource();
        boardRenderer.setScaleViewOnRender(item.isSelected());
        if(item.isSelected())
            scaleViewToFitBoard();
    }

    private void disableAutoZoom()
    {
        boardRenderer.setScaleViewOnRender(false);
        autoZoomMenuItem.setSelected(false);
    }

    private void scaleViewToFitBoard()
    {
        boardRenderer.scaleViewToFitBoard(gameModel.getGameBoard());
        cellSizeSlider.setValue(boardRenderer.getCamera().getZoom());
        drawBoard();
    }

    private void setFullscreen(boolean state)
    {
        fullscreenMenuItem.selectedProperty().setValue(state);
        ((Stage) anchorPane.getScene().getWindow()).setFullScreen(state);
    }

    private void setGenerationsPerSecondLabel()
    {
        int speed = gameModel.getSimulator().getGenerationsPerSecond();
        this.speed.setText(updateTimer.isRunning() ? speed + " g/s" : "0 g/s");
    }



    /* -------------------- BOARD OPERATIONS -------------------- */

    @FXML private void saveGameBoard()
    {
        stopSimulation();
        PatternEditorForm editorForm =
                new PatternEditorForm(gameModel.getGameBoard(), gameModel.getSimulator());
        editorForm.setColorProfile(boardRenderer.getColorProfile());
        editorForm.initModality(Modality.WINDOW_MODAL);
        editorForm.initOwner(anchorPane.getScene().getWindow());
        editorForm.showAndWait();

        GameBoard selectedGameBoard = editorForm.getSelectedGameBoard();
        if(selectedGameBoard != null)
        {
            gameModel.setGameBoard(selectedGameBoard);
            boardRenderer.getCamera().reset();
            boardRenderer.scaleViewToFitBoard(selectedGameBoard);
            drawBoard();
        }
    }

    @FXML protected void loadNewGameBoard()
    {
        stopSimulation();
        PatternChooserForm loader = new PatternChooserForm();
        loader.initModality(Modality.WINDOW_MODAL);
        loader.initOwner(anchorPane.getScene().getWindow());
        loader.showAndWait();

        Pattern pattern = PatternChooserForm.getSelectedPattern();
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

    @FXML private void reloadGameBoard()
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
            double stepDist = distance / boardRenderer.getCamera().getZoom();

            Point cellPos = new Point();
            for(double i = 0.0;  i < 1.0; i += 1.0 / stepDist)
            {
                cellPos.x = (int)(event.getX() - (i * deltaX));
                cellPos.y = (int)(event.getY() - (i * deltaY));
                boardEditor.edit(gameModel.getGameBoard(), cellPos, button == MouseButton.PRIMARY);
            }
        }
    }

    @FXML private void trimBoardToSize()
    {
        GameBoard trimmedBoard = gameModel.getGameBoard().trimmedCopy(1);
        gameModel.setGameBoard(trimmedBoard);
        scaleViewToFitBoard();
    }

    /**
     * The method is called when the user hovers a file over the application window.
     * The file is only allowed to move into the current focused window.
     * @param event The DragEvent
     */
    @FXML private void fileIsHoveringOverApplication(DragEvent event)
    {
        Dragboard board = event.getDragboard();
        if (board.hasFiles() && !PatternChooserForm.isWindowOpened())
            event.acceptTransferModes(TransferMode.ANY);
    }

    /**
     * The method is called when the user drops one or more files into the application window.
     * It reads in the files and adds them to the PatternChooserForms loading queue.
     * The PatternChooserForms will then be opened.
     * @param event The DragEvent
     */
    @FXML private void fileIsDroppedOnApplication(DragEvent event)
    {
        event.getDragboard().getFiles().forEach(PatternChooserForm::addFileToLoadingQueue);
        Platform.runLater(this::loadNewGameBoard);
    }

    @FXML private void clearBoard()
    {
        gameModel.setGameBoard(new GameBoardDynamic(GameBoard.DEFAULT_BOARD_WIDTH, GameBoard.DEFAULT_BOARD_HEIGHT));
        stopSimulation();
        scaleViewToFitBoard();
        drawBoard();
    }



    /* -------------------- RULE -------------------- */

    @FXML private void setRuleFromMenuBar(Event event) throws PatternFormatException
    {
        MenuItem item = (MenuItem) event.getSource();
        String ruleFromFXML = RuleStringFormatter.format(item.getId());
        ruleInfo.setText("Rule: " + item.getText() + " - " + ruleFromFXML);
        setNewRule(ruleFromFXML);
    }

    @FXML private void openCustomRuleCreator()
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
            gameModel.setRule(new DefaultRule());
        else
            gameModel.setRule(new CustomRule(rule));
    }



    /* -------------------- HELP MENU ITEM -------------------- */

    @FXML private void informationBoxTemplate(String title, String headerText, String contentText)
    {
        stopSimulation();
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(GameOfLife.APPLICATION_ICON);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/view/AlertStyleSheet.css").toExternalForm());
        dialogPane.getStyleClass().add("alert");

        dialogPane.setPrefWidth(450);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    @FXML private void openGettingStartedDialog()
    {
        stopSimulation();
        informationBoxTemplate(
                "Getting Started",
                "Getting Started",
                "Use the left " +
                        "and right mouse buttons to edit the board. Click the mouse-wheel to move around and " +
                        "ctrl+scroll to zoom.\n(If you are using a touch pad: scroll to move around).\n\n" +

                        "Open the \"File Chooser\" (ctrl+o) to select a pattern from your disk, " +
                        "a URL or a pre-loaded pattern. Start the simulation with the \"Play\"-button " +
                        "on the toolbar (or with space). You can change the board-colors and change or create " +
                        "your own rules.\n If you want to save your pattern choose: " +
                        "\"File\">\"Edit and save\" (ctrl+s).\n\n");
    }

    @FXML private void openShortcutDialog()
    {
        stopSimulation();
        informationBoxTemplate(
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

    @FXML private void openAboutDialog()
    {
        stopSimulation();
        informationBoxTemplate(
                "About",
                "Information",
                "This game was created by Niklas Johansen (s306603) " +
                        "and Julie Katrine Høvik (s236518) in the spring of 2017.");
    }
}