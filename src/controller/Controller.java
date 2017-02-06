package controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;
import model.GameModel;
import view.BoardRenderer;

public class Controller implements Initializable, UpdatableObject
{
    private GameModel gameModel;
    private BoardLoader boardLoader;
    private BoardRenderer boardRender;
    private UpdateTimer updateTimer;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

    }

    @Override
    public void triggerUpdate()
    {

    }

    private void drawBoard()
    {

    }

    private void addEventListeners()
    {

    }

    @FXML private void loadNewGameBoard()
    {

    }

    @FXML private void saveGameBoard()
    {

    }

    @FXML private void closeApplication()
    {

    }

    @FXML private void startSimulation()
    {

    }

    @FXML private void stopSimulation()
    {

    }

    @FXML private void simulateNextGeneration()
    {

    }
}

