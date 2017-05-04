package controller;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * A dialog that allows the user to specify their own simulation rule.
 *
 * @author Niklas Johansen
 * @author Julie Katrine Høvik
 */
public class CustomRuleCreator extends TextInputDialog
{
    private TextField birth;
    private TextField survival;
    private String newRule = "";

    /**
     * Sets up the dialog window with GUI elements and stylesheet.
     */
    public CustomRuleCreator()
    {
        this.birth = new TextField();
        this.survival = new TextField();
        designDialogLayout();
        setUpOKButtonEvent();
    }

    private void designDialogLayout()
    {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        super.setTitle("Create your own rule");
        super.setHeaderText("Please enter the amount of living neighbors\n" +
                "that will cause the following state of a cell:");

        grid.add(new Label("Birth:"), 0, 0);
        grid.add(birth, 1, 0);
        grid.add(new Label("Survival:"), 0, 1);
        grid.add(survival, 1, 1);

        DialogPane dialogPane = super.getDialogPane();
        dialogPane.setContent(grid);
        dialogPane.getStylesheets().add(getClass().getResource
                ("/view/layout/AlertStyleSheet.css").toExternalForm());
        dialogPane.getStyleClass().add("alert");
        ((Stage) dialogPane.getScene().getWindow()).getIcons().add(Main.APPLICATION_ICON);

        Button cancelButton = (Button) super.getDialogPane().lookupButton( ButtonType.CANCEL );
        cancelButton.getStyleClass().add("cancelButton");
    }

    private void setUpOKButtonEvent()
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

                DialogPane dialogPane = alert.getDialogPane();
                dialogPane.getStylesheets().add(getClass().getResource
                        ("/view/layout/AlertStyleSheet.css").toExternalForm());
                dialogPane.getStyleClass().add("alert");
                alert.setTitle("");
                alert.setHeaderText("");
                alert.setContentText("The rule has to be integers only");
                ((Stage) dialogPane.getScene().getWindow()).getIcons().add(Main.APPLICATION_ICON);
                alert.showAndWait();

                birth.clear();
                survival.clear();
            }
        });
    }

    /**
     * Returns the user specified rule string.
     * The string will be empty if the user pressed cancel.
     * @return A string.
     */
    public String getRuleString()
    {
        return newRule;
    }
}

