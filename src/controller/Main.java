/**
 * dfghjhgfdfghjhgfd
 */
package controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * The main class in this application.
 * Loads the FXML document and sets up the new scene and stage.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 * @see MainController
 */
public class Main extends Application
{
    public static Image APPLICATION_ICON;

    /**
     * Is called from the JavaFX application thread.
     * @param primaryStage Main stage for the application.
     * */
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/view/layout/MainUserInterface.fxml"));

        AnchorPane root = loader.load();
        Scene scene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());

        primaryStage.setTitle("Game of Life");
        primaryStage.setMinWidth(root.getMinWidth());
        primaryStage.setMinHeight(root.getMinHeight());
        primaryStage.getIcons().add((APPLICATION_ICON =
                new Image(getClass().getResourceAsStream("/img/logo.png"))));
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event ->
        {
            event.consume();
            ((MainController)loader.getController()).closeRequest();
        });
    }

    /**
     * The main method of the application.
     * The first method called from JVM.
     * @param args Arguments for the applications
     */
    public static void main(String[] args)
    {
        Application.launch(Main.class, args);
    }
}
