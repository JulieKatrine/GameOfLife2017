package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.BoardEditor;
import model.BoardIO.FileType;
import model.BoardIO.Pattern;
import model.BoardIO.PatternExporter;
import model.BoardIO.PatternFormatException;
import model.GameBoard;
import model.Point;
import view.BoardRenderer;
import view.BoardRendererImpl;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PatternEditorForm extends Stage implements Initializable
{
    /**
     * TODO:
     *  - Get the colors from the main application
     *  - Get the rule from the main app?
     *  - Add the saved pattern to the loaded list
     *  - Add some way of adjusting the board size
     *  - close on save? close button?
     *  - Add generation tiles
     *  - Only tile rendering or additional onPressed functionality?
     *  - Automatic generation simulation or button?
     *  - Add GIF exporter? as filetype in fileChooser?
     *  - Text when patterns repeat? good candidate for GIF?
     *  - Set application board on exit
     *  - CSS stylesheet
     *  - Alert dialog on empty board
     *  - Unit tests for export
     *  - JavaDoc
     *  - + + +
     */

    private BoardRenderer boardRenderer;
    private BoardEditor boardEditor;
    private GameBoard originalBoard;
    private GameBoard selectedBoard;

    @FXML private Canvas canvas;
    @FXML private TextField ruleTextField;
    @FXML private TextField authorTextField;
    @FXML private TextField patternNameTextField;
    @FXML private TextArea descriptionTextArea;

    public PatternEditorForm(GameBoard board)
    {
        this.originalBoard = board;
        this.selectedBoard = board.getDeepCopy();

        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../view/PatternEditorForm.fxml"));
            loader.setController(this);
            Scene scene = new Scene(loader.load());

            super.setTitle("Choose your pattern");
            super.getIcons().add(GameOfLife.APPLICATION_ICON);
            super.setScene(scene);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        boardRenderer = new BoardRendererImpl(canvas);
        boardEditor = new BoardEditor(boardRenderer.getCamera());

        boardRenderer.setLivingCellColor(Color.color(0.0275, 0.9882, 0));
        boardRenderer.setDeadCellColor(Color.BLACK);
        boardRenderer.scaleViewToFitBoard(selectedBoard);
        boardRenderer.render(selectedBoard);
        addEventListeners();
    }

    private void addEventListeners()
    {
        canvas.setOnMouseDragged(event -> editBoard(event));
        canvas.setOnMousePressed(event -> editBoard(event));
    }

    private void editBoard(MouseEvent event)
    {
        if (event.getButton() == MouseButton.SECONDARY)
        {
            boardEditor.edit(selectedBoard, new Point((int) event.getX(), (int) event.getY()), false);
            drawBoard();
        }
        else if (event.getButton() == MouseButton.PRIMARY)
        {
            boardEditor.edit(selectedBoard, new Point((int) event.getX(), (int) event.getY()), true);
            drawBoard();
        }
    }

    private void drawBoard()
    {
        boardRenderer.render(selectedBoard);
    }

    @FXML private void save()
    {
        try
        {
            Pattern pattern = createPattern();
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("GOL Pattern", "*.rle"));
            fileChooser.setInitialFileName(pattern.getName());
            File file = fileChooser.showSaveDialog(this);

            if(file != null)
            {
                PatternExporter exporter = new PatternExporter();
                exporter.export(pattern, file);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (PatternFormatException e)
        {
            e.printStackTrace();
        }
    }

    private Pattern createPattern() throws PatternFormatException
    {
        // Gets the bounding box and calculates the new width and height.
        Point[] bBox = selectedBoard.getBoundingBox();
        int width  = bBox[1].x - bBox[0].x;
        int height = bBox[1].y - bBox[0].y;

        if(width > 0 && height > 0)
        {
            // Creates a new data array and copies the data over.
            boolean[][] cellData = new boolean[height][width];
            Point cellPos = new Point();
            for (cellPos.y = bBox[0].y; cellPos.y < bBox[1].y; cellPos.y++)
                for (cellPos.x = bBox[0].x; cellPos.x < bBox[1].x; cellPos.x++)
                    cellData[cellPos.y - bBox[0].y][cellPos.x - bBox[0].x] = selectedBoard.isCellAliveInThisGeneration(cellPos);

            // Gets all the text from the TextFields.
            String rule = ruleTextField.getText();
            String author = authorTextField.getText();
            String name = patternNameTextField.getText();
            String description = descriptionTextArea.getText();

            // Adds the text to an ArrayList.
            ArrayList<String> metaData = new ArrayList<>();
            metaData.add(name == null ? "" : "#N " + name);
            metaData.add(author == null ? "" : "#O " + author);
            for(String c : description.split("\n"))
                metaData.add("#C " + c);

            // Creates a new pattern and adds the data to it.
            Pattern pattern = new Pattern();
            pattern.setMetadata(metaData);
            pattern.setCellData(cellData);
            pattern.setRule(rule);
            return pattern;
        }
        else
            throw new PatternFormatException("The board contains no living cells.");
    }
}
