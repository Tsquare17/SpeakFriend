package com.tsquare.speakfriend.nodes;

import com.tsquare.speakfriend.account.AccountController;
import com.tsquare.speakfriend.account.preview.AccountPreview;

import javafx.scene.Cursor;
import javafx.scene.control.ListCell;

import java.io.IOException;

public class accountListCell extends ListCell<AccountPreview> {

    @Override
    protected void updateItem(AccountPreview account, boolean b) {
        super.updateItem(account, b);
        if(b || account == null) {
            setText(null);
            setGraphic(null);
        } else {
            this.setCursor(Cursor.HAND);
            setText(account.getName());

            this.setOnMouseClicked(e -> {
                AccountController accountController = new AccountController();
                try {
                    accountController.showAccountDetails(account.getId());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }
}
