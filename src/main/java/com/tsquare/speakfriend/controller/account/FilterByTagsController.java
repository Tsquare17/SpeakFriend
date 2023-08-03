package com.tsquare.speakfriend.controller.account;

import com.tsquare.speakfriend.controller.main.Controller;
import com.tsquare.speakfriend.controller.main.Main;
import com.tsquare.speakfriend.database.entity.AccountPreviewEntity;
import com.tsquare.speakfriend.database.model.TagsModel;
import com.tsquare.speakfriend.session.AccountListSession;
import com.tsquare.speakfriend.session.UserSession;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class FilterByTagsController extends Controller {
    @FXML
    private VBox filter_tags_container;

    @FXML
    public void initialize() throws SQLException {
        filter_tags_container.setPadding(new Insets(30, 0, 0, 0));

        UserSession userSession = UserSession.getInstance();

        // get user tags
        TagsModel tagsModel = new TagsModel();
        ResultSet resultSet = tagsModel.getUserTags(userSession.getId());

        ScrollPane scrollPane = new ScrollPane();
        VBox tagBox = new VBox();

        while(resultSet.next()) {

            CheckBox checkBox = new CheckBox(resultSet.getString("user_tag_name"));

            checkBox.setPadding(new Insets(8));
            checkBox.setCursor(Cursor.HAND);
            checkBox.setStyle("-fx-text-fill: #fff;");

            EventHandler<ActionEvent> event = actionEvent -> {
                // get the account list container
                Scene scene = Main.getScene();
                VBox accountList = (VBox) scene.lookup("#account_list");

                ObservableList<Node> listView = accountList.getChildren();

                // get the filter tags checkboxes.
                Set<Node> tagListParent = filter_tags_container.lookupAll("CheckBox");

                // loop through the checkboxes and put together list of checked tags.
                List<String> tags = new ArrayList<>();
                for (Node check: tagListParent) {
                    if (check instanceof CheckBox) {
                        if (((CheckBox) check).isSelected()) {
                            tags.add(((CheckBox) check).getText());
                        }
                    }
                }

                // loop through the accountpreviewlist, and put together a map of accountname to tags,
                AccountListSession accountListSession = AccountListSession.getInstance();

                List<AccountPreviewEntity> accounts = accountListSession.getPreviews();
                HashMap<String, String[]> tagMap = new HashMap<>();
                for (AccountPreviewEntity account: accounts) {
                    if (account.getTags() != null) {
                        tagMap.put(account.getName(), account.getTags());
                    }
                }

                // loop through the account list, and filter out items that don't have a tag in the checked tags.
                int count = 0;
                for (Node item: listView) {
                    if (tags.isEmpty()) {
                        item.setVisible(true);
                        item.setManaged(true);
                        continue;
                    }

                    String accountName = item.getId().replace("$:$", " ");

                    // check the account list tags map
                    String[] accountTags = tagMap.get(accountName);

                    // hide account
                    item.setVisible(false);
                    item.setManaged(false);

                    if (accountTags == null) {
                        continue;
                    }

                    for (String accountTag: accountTags) {
                        if (tags.contains(accountTag)) {
                            // show account
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
                    }
                }
            };

            checkBox.setOnAction(event);

            tagBox.getChildren().add(checkBox);
        }

        scrollPane.setContent(tagBox);

        filter_tags_container.getChildren().add(scrollPane);
    }
}
