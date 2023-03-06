package com.tsquare.speakfriend.controller.tags;

import com.tsquare.speakfriend.controller.main.Controller;
import com.tsquare.speakfriend.database.model.TagsModel;
import com.tsquare.speakfriend.session.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.sql.ResultSet;
import java.sql.SQLException;

public class TagsController extends Controller {
    @FXML ListView<String> tag_list;
    @FXML TextField tag_input;

    @FXML
    public void initialize() throws SQLException {
        UserSession userSession = UserSession.getInstance();

        TagsModel tagsModel = new TagsModel();
        ResultSet resultSet = tagsModel.getUserTags(userSession.getId());

        while (resultSet.next()) {
            tag_list.getItems().add(resultSet.getString("tag_name"));
        }

        resultSet.close();
        tagsModel.close();
    }

    @FXML
    public void saveAction() throws SQLException {
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

            tagsModel.close();

            tag_list.getItems().add(tag_input.getText());

            tag_input.clear();
        }
    }

    @FXML
    public void deleteAction() throws SQLException {
        // delete user tag
        // remove from list
    }
}
