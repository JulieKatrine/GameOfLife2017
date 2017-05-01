package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * The programs main class.
 *
 * Extends Application as fxml is used.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 */
public class GameOfLife extends Application
{
    public static Image APPLICATION_ICON;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/UserInterface.fxml"));

        AnchorPane root = loader.load();
        Scene scene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());

        Controller controller = loader.getController();
        controller.handleKeyEvent(scene);

        primaryStage.setTitle("Game of Life");
        primaryStage.setMinWidth(root.getMinWidth());
        primaryStage.setMinHeight(root.getMinHeight());
        primaryStage.getIcons().add((APPLICATION_ICON = new Image(getClass().getResourceAsStream("/img/logo.png"))));
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(event ->
        {
            event.consume();
            controller.closeRequest();
        });

        primaryStage.show();
    }

    public static void main(String[] args)
    {
        Application.launch(GameOfLife.class, args);
    }
}
