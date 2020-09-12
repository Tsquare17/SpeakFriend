package com.tsquare.speakfriend.account;

import com.tsquare.speakfriend.account.preview.AccountPreview;
import com.tsquare.speakfriend.database.account.AccountList;
import com.tsquare.speakfriend.main.Controller;
import com.tsquare.speakfriend.main.Main;
import com.tsquare.speakfriend.state.State;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class AccountListController extends Controller {
    @FXML private VBox account_list_container;
    @FXML private AnchorPane account_anchor;
    @FXML private ScrollPane account_list_scrollpane;
    @FXML private TextField account_filter_field;
    @FXML private VBox account_list;

    @FXML
    public void initialize() {
        account_anchor.setPadding(new Insets(30, 0, 0, 0));
        account_list_scrollpane.setFitToWidth(true);
        account_list.setFillWidth(true);

        List<AccountPreview> decryptedList = AccountList.getPreviews();

        int count = 0;
        for(AccountPreview item: decryptedList) {
            HBox accountBox = new HBox();
            accountBox.getChildren().add(new Label(item.getName()));
            accountBox.setId(item.getName().replace(" ", "$:$").toLowerCase());
            accountBox.setPadding(new Insets(20, 30, 20, 30));
            accountBox.setCursor(Cursor.HAND);

            Color accountColor = Color.rgb(47, 52, 57);
            if (count % 2 != 0) {
                accountColor = Color.rgb(43, 46, 52);
            }

            accountBox.setBackground(
                    new Background(
                            new BackgroundFill(accountColor, CornerRadii.EMPTY, Insets.EMPTY)
                    )
            );
            accountBox.setOnMouseClicked(e -> {
                State.setSelectedAccountId(item.getId());
                try {
                    newContainerScene("account-details");
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            });
            account_list.getChildren().add(accountBox);
            count++;
        }

        account_list_scrollpane.setContent(account_list);

        Stage stage = Main.getStage();

        account_list_scrollpane.setPrefWidth(stage.getWidth());
        account_list_scrollpane.setPrefHeight(stage.getHeight());

        account_list_container.setPrefHeight(stage.getHeight());
        // Bind the scroll pane's size to the parent anchor pane's size.
        stage.heightProperty().addListener(e -> {
            account_list_container.setPrefHeight(stage.getHeight() - 30);
            account_anchor.setPrefHeight(stage.getHeight() - 60);
        });
        stage.widthProperty().addListener(e -> account_list_scrollpane.setPrefWidth(stage.getWidth()));
    }

    @FXML
    public void createAccountView() throws IOException {
        newContainerScene("create-account");
    }

    @FXML
    private void setFilteredList(KeyEvent event) {
        String filter = account_filter_field.getText().replace(" ", "").toLowerCase();

        if(event.getCode().equals(KeyCode.DELETE)) {
            if(filter.isEmpty()) {
                return;
            }
        }

        ObservableList<Node> listView = account_list.getChildren();
        int count = 0;
        for (Node item: listView) {
            if (item.getId().replace("$:$", " ").toLowerCase().startsWith(filter)) {
                item.setVisible(true);
                item.setManaged(true);

                Color accountColor = Color.rgb(47, 52, 57);
                if (count % 2 != 0) {
                    accountColor = Color.rgb(43, 46, 52);
                }

                ((HBox) item).setBackground(
                        new Background(
                                new BackgroundFill(accountColor, CornerRadii.EMPTY, Insets.EMPTY)
                        )
                );
                count++;
            } else {
                item.setVisible(false);
                item.setManaged(false);
            }

        }
    }
}
