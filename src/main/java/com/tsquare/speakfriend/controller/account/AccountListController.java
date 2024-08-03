package com.tsquare.speakfriend.controller.account;

import com.trevorthompson.Levenshtein;
import com.tsquare.speakfriend.database.entity.AccountPreviewEntity;
import com.tsquare.speakfriend.controller.main.Controller;
import com.tsquare.speakfriend.controller.main.Main;
import com.tsquare.speakfriend.session.AccountListSession;
import com.tsquare.speakfriend.session.ApplicationSession;
import com.tsquare.speakfriend.utils.AccountSearchComparator;
import com.tsquare.speakfriend.utils.Function;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.ArrayList;
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

        ApplicationSession applicationSession = ApplicationSession.getInstance();
        AccountListSession accountListSession = AccountListSession.getInstance();
        List<AccountPreviewEntity> decryptedList = accountListSession.getPreviews();

        int count = 0;
        for(AccountPreviewEntity item: decryptedList) {
            HBox accountBox = new HBox();
            accountBox.getChildren().add(new Label(item.getName()));
            accountBox.setId(item.getName().replace(" ", "$:$").toLowerCase());
            accountBox.setPadding(new Insets(20, 30, 20, 30));
            accountBox.setCursor(Cursor.HAND);
            accountBox.getStyleClass().add("account-preview-container");

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
                applicationSession.setSelectedAccountId(item.getId());
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

        if (!applicationSession.getAccountSearchString().equals("")) {
            account_filter_field.setText(applicationSession.getAccountSearchString());
            filterAccounts(applicationSession.getAccountSearchString());
        }

        account_filter_field.textProperty().addListener((obs, oldText, newText) -> {
            String filter = account_filter_field.getText().replace(" ", "").toLowerCase();
            applicationSession.setAccountSearchString(filter);

            if(filter.isEmpty()) {
                toAccounts();
                return;
            }

            filterAccounts(filter);
        });
    }

    @FXML
    public void createAccountView() throws IOException {
        newContainerScene("create-account");
    }

    private void filterAccounts(String filter) {
        Levenshtein levenshtein = new Levenshtein();
        ObservableList<Node> listView = account_list.getChildren();

        AccountSearchComparator comparator = new AccountSearchComparator();
        comparator.setCompareTo(filter);

        List<Node> list = new ArrayList<>(listView);
        list.sort(comparator);
        listView.clear();
        listView.addAll(list);

        int count = 0;
        for (Node item: listView) {
            String accountName = item.getId().replace("$:$", " ");
            float ratio = levenshtein.getRatio(accountName, filter);

            boolean match = accountName.toLowerCase().startsWith(filter);
            boolean contains = false;

            if (filter.length() > 5) {
                contains = accountName.contains(filter);
            }

            if (ratio > 0.4 || match || contains) {
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

    @FXML
    public void filterByTagsModal() throws IOException {
        Function clearTagFilters = () -> {
            Scene scene = Main.getScene();
            VBox accountList = (VBox) scene.lookup("#account_list");

            ObservableList<Node> listView = accountList.getChildren();

            int count = 0;
            for (Node item: listView) {
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
            }
        };

        createModalView("/filter-by-tags.fxml", "Tags", false, clearTagFilters);
    }
}
