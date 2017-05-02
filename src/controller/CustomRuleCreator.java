package controller;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

/**
 * Created by julie on 18-Apr-17.
 */
public class CustomRuleCreator extends TextInputDialog {
    TextField birth;
    TextField survival;
    String newRule = "";

    public CustomRuleCreator()
    {
        this.birth = new TextField();
        this.survival = new TextField();
        designDialogLayout();
        setUpOKButtonEvent();
    }

    public void designDialogLayout()
    {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Birth:"), 0, 0);
        grid.add(birth, 1, 0);
        grid.add(new Label("Survival:"), 0, 1);
        grid.add(survival, 1, 1);

        super.setHeaderText("Please enter the amount of living neighbors\nthat will cause the following state of a cell:");
        super.getDialogPane().setContent(grid);
        super.setTitle("Create your own rule");
        ((Stage) super.getDialogPane().getScene().getWindow()).getIcons().add(GameOfLife.APPLICATION_ICON);

        DialogPane dialogPane = super.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/view/AlertStyleSheet").toExternalForm());
        dialogPane.getStyleClass().add("alert");

        Button cancelButton = (Button) super.getDialogPane().lookupButton( ButtonType.CANCEL );
        cancelButton.getStyleClass().add("cancelButton");
    }

    public void setUpOKButtonEvent()
    {
        super.getDialogPane().lookupButton(ButtonType.OK).addEventFilter(ActionEvent.ACTION, event ->
        {
            try
            {
                if(!birth.getText().isEmpty())
                    Integer.parseInt(birth.getText());

                if(!survival.getText().isEmpty())
                    Integer.parseInt(survival.getText());

                newRule += "B" + birth.getText() + "/S" + survival.getText();

                super.close();

            }
            catch(NumberFormatException e)
            {
                event.consume();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("");
                alert.setHeaderText("");
                alert.setContentText("The rule has to be integers only");
                ((Stage) alert.getDialogPane().getScene().getWindow()).getIcons().add(GameOfLife.APPLICATION_ICON);
                alert.showAndWait();

                birth.clear();
                survival.clear();
            }
        });
    }

    public String getRuleString()
    {
        return newRule;
    }
}

