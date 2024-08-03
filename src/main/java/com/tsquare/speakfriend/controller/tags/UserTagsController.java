package com.tsquare.speakfriend.controller.tags;

import com.tsquare.speakfriend.controller.main.Controller;
import com.tsquare.speakfriend.database.entity.Tag;
import com.tsquare.speakfriend.database.model.TagsModel;
import com.tsquare.speakfriend.fxml.UserTagCellFactory;
import com.tsquare.speakfriend.session.UserSession;
import com.tsquare.speakfriend.utils.Function;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserTagsController extends Controller {
    @FXML ListView<Tag> tag_list;
    @FXML TextField tag_input;

    @FXML
    public void initialize() throws SQLException {
        UserSession userSession = UserSession.getInstance();

        TagsModel tagsModel = new TagsModel();

        ResultSet resultSet = tagsModel.getUserTags(userSession.getId());

        while (resultSet.next()) {
            Tag tag = new Tag(resultSet);

            tag_list.getItems().add(tag);
        }

        tag_list.setCellFactory(new UserTagCellFactory());

        resultSet.close();
        tagsModel.close();
    }

    @FXML
    public void saveAction() throws SQLException {
        if (tag_input.getText().isEmpty()) {
            return;
        }

        UserSession userSession = UserSession.getInstance();

        TagsModel tagsModel = new TagsModel();
        ResultSet resultSet = tagsModel.getTagByName(userSession.getId(), tag_input.getText());

        if (resultSet.next()) {
            resultSet.close();
            tagsModel.close();

            tag_input.clear();
        } else {
            resultSet.close();
            tagsModel.reset();

            tagsModel.createUserTag(userSession.getId(), tag_input.getText());

            tagsModel.reset();

            resultSet = tagsModel.getTagByName(userSession.getId(), tag_input.getText());

            Tag tag = new Tag(resultSet);

            resultSet.close();

            tagsModel.close();

            tag_list.getItems().add(tag);

            tag_input.clear();
        }
    }

    @FXML
    public void delete() throws IOException {
        Function onConfirm = () -> {
            ObservableList<Tag> list = tag_list.getItems();
            TagsModel tagsModel = new TagsModel();

            ArrayList<Tag> removeTags = new ArrayList<>();

            for(Tag item: list) {
                if (item.isSelected()) {
                    tagsModel.deleteUserTag(item.getUserTagId());

                    removeTags.add(item);
                }
            }

            for (Tag item: removeTags) {
                tag_list.getItems().remove(item);
            }
        };

        createModalConfirmation("Are you sure you want to delete the selected tags?", onConfirm);
    }
}
