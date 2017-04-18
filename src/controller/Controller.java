package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.*;
import model.BoardIO.Pattern;
import model.Point;
import model.simulation.CustomRule;
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
    private ObservableList<String> listOfRuleSets;

    @FXML private AnchorPane anchorPane;
    @FXML private Canvas canvas;
    @FXML private Slider cellSizeSlider;
    @FXML private Slider speedSlider;
    @FXML private MenuItem startStopMenuItem;
    @FXML private MenuItem nextMenuItem;
    @FXML private Button startStopButton;
    @FXML private ColorPicker deadCellColor;
    @FXML private ColorPicker livingCellColor;
    @FXML private ChoiceBox<String> ruleChooser;

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
        boardRender.scaleViewToFitBoard(gameModel.getGameBoard());
        updateTimer.setDelayBetweenUpdates((int)(speedSlider.getMax() - speedSlider.getValue()));

        addEventListeners();
        drawBoard();
    }

    private void addEventListeners()
    {
        /*
        * TODO: Put the label in the box, not on the side. I think css is the way to go.
        */

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
                boardRender.getCamera().move(gameModel.getGameBoard(), event.getDeltaX(), event.getDeltaY());

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
            else if (event.getButton() == MouseButton.SECONDARY)
            {
                boardEditor.edit(gameModel.getGameBoard(),
                        new Point((int)event.getX(), (int)event.getY()),
                        false);
                drawBoard();
            }
        });

        canvas.setOnMouseDragged(event ->
        {
            // Updates the camera position when the user drags the mouse and
            if(event.getButton() == MouseButton.SECONDARY)
            {
                boardEditor.edit(gameModel.getGameBoard(),
                        new Point((int)event.getX(), (int)event.getY()),
                        false);
            }

            // Setts a cell alive when the mouse i dragged over it
            else if(event.getButton() == MouseButton.PRIMARY)
            {
                boardEditor.edit(gameModel.getGameBoard(),
                        new Point((int)event.getX(), (int)event.getY()),
                        true);
            }

            else if(event.getButton() == MouseButton.MIDDLE){
                double deltaX = (int)event.getX() - lastMousePos.x;
                double deltaY = (int)event.getY() - lastMousePos.y;
                boardRender.getCamera().move(gameModel.getGameBoard(), deltaX, deltaY);
                lastMousePos.x = (int)event.getX();
                lastMousePos.y = (int)event.getY();
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
            canvas.setHeight(newValue.doubleValue() - 40 - 20);
            drawBoard();
        });

        //Shows the user different choices of rules

        listOfRuleSets = FXCollections.observableArrayList
                ("Change rule", "Default Rule", "Replicator", "Fredkin", "Seeds", "Live Free or Die", "Life without death",
                        "Custom rule");
        ruleChooser.setValue("Change rule");
        ruleChooser.setItems(listOfRuleSets);
        ruleChooser.setOnMouseExited(event -> canvas.requestFocus());
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
            else if(code == KeyCode.CONTROL)
                controlPressed = true;
            else if(code == KeyCode.R)
            {
                Pattern p = PatternChooserForm.getSelectedPattern();
                if(p != null)
                {
                    gameModel.setGameBoard(PatternChooserForm.getSelectedPattern().getGameBoard());
                    drawBoard();
                }
            }
            else if(code == KeyCode.O)
                loadNewGameBoard();
        });

        scene.setOnKeyReleased(event ->
        {
            if(event.getCode() == KeyCode.CONTROL)
                controlPressed = false;
        });
    }

    @FXML private void simulateNextGeneration()
    {
        updateTimer.triggerUpdate();
    }

    @FXML protected void loadNewGameBoard()
    {
        PatternChooserForm loader = new PatternChooserForm();
        loader.showAndWait();
        Pattern pattern = loader.getSelectedPattern();
        if(pattern != null)
        {
            gameModel.setGameBoard(pattern.getGameBoard());
            gameModel.setRule(pattern.getRule());
            boardRender.scaleViewToFitBoard(gameModel.getGameBoard());
            cellSizeSlider.setValue(boardRender.getCamera().getZoom());
            updateTimer.setRunning(false);
            startStopMenuItem.setText("Start");
            startStopButton.setText("Start");
            nextMenuItem.setDisable(false);
            pattern.getRule();
            drawBoard();
        }
    }

    /**
     * Temporary test of pattern saving.
     * Saves the currently loaded pattern.
     */
    @FXML private void saveGameBoard()
    {
        PatternEditorForm editorForm = new PatternEditorForm(gameModel.getGameBoard());
        editorForm.setColorProfile(boardRender.getColorProfile());
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

    @FXML private void startStopSimulation()
    {
        updateTimer.setRunning(!updateTimer.isRunning());
        startStopMenuItem.setText(updateTimer.isRunning() ? "Stop" : "Start");
        startStopButton.setText(updateTimer.isRunning() ? "Stop" : "Start");
        nextMenuItem.setDisable(updateTimer.isRunning());
    }

    @FXML private void createEmptyBoard()
    {
        gameModel.setGameBoard(new GameBoardDynamic(GameBoard.DEFAULT_BOARD_WIDTH, GameBoard.DEFAULT_BOARD_HEIGHT));
        boardRender.scaleViewToFitBoard(gameModel.getGameBoard());
        cellSizeSlider.setValue(boardRender.getCamera().getZoom());
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

    @FXML private void getChosenRule()
    {       String rule;
            if(ruleChooser.getValue() != null) {
                rule = ruleChooser.getValue();
                ruleConverter(rule);
            }
    }

    /*TODO: Julie:
    * Make it possible to "re-click" on "custom rule"
    * Disable the first button (Change rule)
    * Default Ruleset could be done better
    * Add logo in window corners
    * Show the actual rule, not just the name
    * To add more rules: http://www.conwaylife.com/wiki/Rules#Rules*/
    private void ruleConverter(String rule)
    {
        if (rule.equals("Default Rule"))
            setNewRule("B3/S23");
        else if(rule.equals("Replicator"))
            setNewRule("B1357/S1357");
        else if(rule.equals("Fredkin"))
            setNewRule("B1357/S02468");
        else if(rule.equals("Seeds"))
            setNewRule("B2/S");
        else if(rule.equals("Live Free or Die"))
            setNewRule("B2/S0");
        else if(rule.equals("Life without death"))
            setNewRule("B3/S012345678");
        else if(rule.equals("Custom rule"))
            customChangeRule();
    }

    @FXML private void customChangeRule()
    {
        TextInputDialog inputDialog = new TextInputDialog();
        String newRule = "";

        inputDialog.setHeaderText("Please enter the amount of living neighbors\nthat will cause the following state of a cell:");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField birth = new TextField();
        birth.setPromptText("3");
        TextField survival = new TextField();
        survival.setPromptText("2, 3");

        grid.add(new Label("Birth:"), 0, 0);
        grid.add(birth, 1, 0);
        grid.add(new Label("Survival:"), 0, 1);
        grid.add(survival, 1, 1);

        inputDialog.getDialogPane().setContent(grid);
        inputDialog.showAndWait();

        newRule += "B"+birth.getText()+"/S"+survival.getText();
        newRule.trim().replaceAll(",", "");
        newRule.trim().replaceAll(" ", "");
        System.out.println(newRule);
        setNewRule(newRule);

        if(birth.getText().isEmpty() || survival.getText().isEmpty())
        {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("");
            alert.setHeaderText("");
            alert.setContentText("The rule was set to " + newRule);
            alert.show();
        }
    }

    private void setNewRule(String rule)
    {
        gameModel.setRule(new CustomRule(rule));
    }

    public void closeRequest() {

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