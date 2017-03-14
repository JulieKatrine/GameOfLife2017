package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class GameOfLife extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../view/UserInterface.fxml"));
        AnchorPane root = loader.load();
        Controller controller = loader.getController();


        Scene scene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());

        controller.handleKeyEvent(scene);

        root.prefWidthProperty().bind(scene.widthProperty());
        root.prefHeightProperty().bind(scene.heightProperty());

        primaryStage.setTitle("Game of Life");
        primaryStage.setScene(scene);
        primaryStage.show();


    }

    public static void main(String[] args)
    {
        launch(args);
    }

}
