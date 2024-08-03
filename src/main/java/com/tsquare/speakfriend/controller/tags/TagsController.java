package com.tsquare.speakfriend.controller.tags;

import com.tsquare.speakfriend.controller.main.Controller;
import com.tsquare.speakfriend.database.entity.Tag;
import com.tsquare.speakfriend.database.model.TagsModel;
import com.tsquare.speakfriend.fxml.TagCellFactory;
import com.tsquare.speakfriend.session.ApplicationSession;
import com.tsquare.speakfriend.session.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TagsController extends Controller {
    @FXML ListView<Tag> tag_list;
    @FXML TextField tag_input;

    @FXML
    public void initialize() throws SQLException {
        UserSession userSession = UserSession.getInstance();
        ApplicationSession applicationSession = ApplicationSession.getInstance();

        TagsModel tagsModel = new TagsModel();

        ResultSet resultSet = tagsModel.getAccountTags(applicationSession.getSelectedAccountId());

        List<Integer> accountTags = new ArrayList<>();
        while (resultSet.next()) {
            accountTags.add(resultSet.getInt("user_tag_id"));
        }

        resultSet.close();
        tagsModel.reset();

        resultSet = tagsModel.getUserTags(userSession.getId());

        while (resultSet.next()) {
            Tag tag = new Tag(resultSet);

            Integer accountTagId = resultSet.getInt("user_tag_id");

            for (int selectedAccountTags: accountTags) {
                if (selectedAccountTags == accountTagId) {
                    tag.setIsSelected(true);
                }
            }

            tag_list.getItems().add(tag);
        }

        tag_list.setCellFactory(new TagCellFactory());

        resultSet.close();
        tagsModel.close();
    }

    @FXML
    public void saveAction() throws SQLException {
        if (tag_input.getText().isEmpty()) {
            return;
        }

        UserSession userSession = UserSession.getInstance();
        ApplicationSession applicationSession = ApplicationSession.getInstance();

        int accountId = applicationSession.getSelectedAccountId();

        TagsModel tagsModel = new TagsModel();
        ResultSet resultSet = tagsModel.getTagByName(userSession.getId(), tag_input.getText());

        if (resultSet.next()) {
            resultSet.close();
            tagsModel.close();

            tag_input.clear();
        } else {
            resultSet.close();
            tagsModel.reset();

            int userTagId = tagsModel.createUserTag(userSession.getId(), tag_input.getText());

            tagsModel.reset();

            tagsModel.createAccountTag(accountId, userTagId);

            tagsModel.reset();

            resultSet = tagsModel.getTagByName(userSession.getId(), tag_input.getText());

            Tag tag = new Tag(resultSet);

            resultSet.close();

            tag.setIsSelected(true);

            tagsModel.close();

            tag_list.getItems().add(tag);

            tag_input.clear();
        }

        ApplicationSession.getInstance().setDirtyAccounts(true);
    }
}
