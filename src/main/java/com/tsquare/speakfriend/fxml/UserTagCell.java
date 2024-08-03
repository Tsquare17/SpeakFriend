package com.tsquare.speakfriend.fxml;

import com.tsquare.speakfriend.database.entity.Tag;
import javafx.scene.control.ListCell;

public class UserTagCell extends ListCell<Tag> {
    @Override
    protected void updateItem(Tag tag, boolean empty) {
        super.updateItem(tag, empty);

        if (empty || tag == null) {
            setText(null);
        } else {
            setText(tag.getTagName());

            this.setOnMouseClicked(mouseEvent -> {
                if (tag.isSelected()) {
                    setStyle("-fx-background-color: -dark");
                } else {
                    setStyle("-fx-background-color: -medium");
                }

                tag.setIsSelected(!tag.isSelected());
            });
        }
    }
}
