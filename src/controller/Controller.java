package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CancellationException;

import javafx.scene.canvas.Canvas;
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

    @FXML
    private Canvas canvas;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        boardRender = new BoardRendererImpl(canvas);
        gameModel = new GameModelImpl();
        boardLoader = new BoardLoader();
        gameModel.setGameBoard(boardLoader.newRandomBoard());
        boardRender.render(gameModel.getGameBoard());
        updateTimer = new UpdateTimer(this);
    }

    @Override
    public void triggerUpdate()
    {
        gameModel.simulateNextGeneration();
        boardRender.render(gameModel.getGameBoard());
    }


    private void addEventListeners()
    {

    }

    @FXML private void loadNewGameBoard()
    {
        gameModel.setGameBoard(boardLoader.newRandomBoard());
        boardRender.render(gameModel.getGameBoard());
    }

    @FXML private void saveGameBoard()
    {

    }

    @FXML private void closeApplication()
    {

    }

    @FXML private void startSimulation()
    {
        updateTimer.start();
    }

    @FXML private void stopSimulation()
    {
        updateTimer.stop();
    }

    @FXML private void simulateNextGeneration()
    {
        gameModel.simulateNextGeneration();
        boardRender.render(gameModel.getGameBoard());
    }
}

