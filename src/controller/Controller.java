package controller;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import model.BoardLoader;
import model.GameModel;
import model.GameModelImpl;
import model.Point;
import view.BoardRenderer;
import view.BoardRendererImpl;

public class Controller implements Initializable, UpdatableObject
{
    private GameModel gameModel;
    private BoardLoader boardLoader;
    private BoardRenderer boardRender;
    private UpdateTimer updateTimer;
    private double lastX;
    private double lastY;


    @FXML private Canvas canvas;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        boardLoader = new BoardLoader();
        boardRender = new BoardRendererImpl(canvas);
        updateTimer = new UpdateTimer(this);
        gameModel   = new GameModelImpl();

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
        gameModel.setGameBoard(boardLoader.newRandomBoard(10,10));
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
        //FIX: MAKE THIS SHIT WORK!!

        canvas.setOnMouseDragged(new EventHandler<MouseEvent>()
        {
            public void handle(MouseEvent event)
            {
                double mouseX = event.getX();
                double mouseY = event.getY();

                double deltaX = lastX - mouseX ;
                double deltaY = lastY - mouseY;

                System.out.println(mouseX + " " + mouseY + " " + lastX + " " + lastY + " " + deltaX + " " + deltaY);
                boardRender.getCamera().moveX(deltaX);
                boardRender.getCamera().moveY(deltaY);
                drawBoard();

                lastX = mouseX;
                lastY = mouseY;
            }
        });

    }
}

