package controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.util.Optional;

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
    private PatternChooserForm patternChooserForm;

    @Override
    public void start(Stage primaryStage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/UserInterface.fxml"));

        AnchorPane root = loader.load();
        Scene scene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());
        root.setStyle("-fx-background-color: #878787");

        Controller controller = loader.getController();
        controller.handleKeyEvent(scene);

        root.prefWidthProperty().bind(scene.widthProperty());
        root.prefHeightProperty().bind(scene.heightProperty());

        primaryStage.setTitle("Game of Life");
//      primaryStage.getIcons().add((APPLICATION_ICON = new Image(getClass().getResourceAsStream("/resources/Logo.png"))));'
        primaryStage.getIcons().add((APPLICATION_ICON = new Image("file:resources/Logo.png")));
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
