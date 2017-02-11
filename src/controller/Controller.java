package controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.canvas.Canvas;
import model.BoardLoader;
import model.GameModel;
import model.GameModelImpl;
import view.BoardRenderer;
import view.BoardRendererImpl;

public class Controller implements Initializable, UpdatableObject
{
    private GameModel gameModel;
    private BoardLoader boardLoader;
    private BoardRenderer boardRender;
    private UpdateTimer updateTimer;

    @FXML private Canvas canvas;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        boardLoader = new BoardLoader();
        boardRender = new BoardRendererImpl(canvas);
        updateTimer = new UpdateTimer(this);
        gameModel   = new GameModelImpl();
        loadNewGameBoard();
    }

    @Override
    public void triggerUpdate()
    {
        gameModel.simulateNextGeneration();
        drawBoard();
    }

    private void drawBoard()
    {
        boardRender.render(gameModel.getGameBoard());
    }

    @FXML private void simulateNextGeneration()
    {
        triggerUpdate();
    }

    @FXML private void loadNewGameBoard()
    {
        gameModel.setGameBoard(boardLoader.newRandomBoard(40,28));
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
        //TODO: Add event listeners for sliders, canvas etc.
    }
}

