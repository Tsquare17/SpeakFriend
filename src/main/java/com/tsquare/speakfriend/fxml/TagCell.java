package com.tsquare.speakfriend.fxml;

import com.tsquare.speakfriend.database.entity.Tag;
import com.tsquare.speakfriend.database.model.TagsModel;
import com.tsquare.speakfriend.session.ApplicationSession;
import javafx.scene.control.ListCell;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TagCell extends ListCell<Tag> {
    @Override
    protected void updateItem(Tag tag, boolean empty) {
        super.updateItem(tag, empty);

        if (empty || tag == null) {
            setText(null);
        } else {
            setText(tag.getTagName());

            this.setOnMouseClicked(mouseEvent -> {
                ApplicationSession applicationSession = ApplicationSession.getInstance();

                if (tag.isSelected()) {
                    setStyle("-fx-background-color: -dark");

                    // remove tag from account_tags
                    try {
                        TagsModel tagsModel = new TagsModel();
                        ResultSet resultSet = tagsModel.getAccountTagByName(applicationSession.getSelectedAccountId(), tag.getTagName());

                        int accountTagId = resultSet.getInt("account_tag_id");

                        resultSet.close();
                        tagsModel.reset();

                        tagsModel.deleteAccountTag(accountTagId);

                        tagsModel.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                } else {
                    setStyle("-fx-background-color: -medium");

                    // add tag to account_tags
                    try {
                        TagsModel tagsModel = new TagsModel();
                        tagsModel.createAccountTag(applicationSession.getSelectedAccountId(), tag.getUserTagId());

                        tagsModel.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                tag.setIsSelected(!tag.isSelected());

                applicationSession.setDirtyAccounts(true);
            });

            if (tag.isSelected()) {
                setStyle("-fx-background-color: -medium");
            } else {
                setStyle("-fx-background-color: -dark");
            }
        }
    }
}
