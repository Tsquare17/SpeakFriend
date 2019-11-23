package list;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class List {
    @FXML private Text errorMessage;

    @FXML protected void handleSubmitButtonAction(ActionEvent event) {
        errorMessage.setText("Button pressed");
    }
}
