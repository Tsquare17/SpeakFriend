package com.tsquare.speakfriend.fxml;

import com.tsquare.speakfriend.database.entity.Tag;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

public class UserTagCellFactory implements Callback<ListView<Tag>, ListCell<Tag>> {
    @Override
    public ListCell<Tag> call(ListView<Tag> param) {
        return new UserTagCell();
    }
}
