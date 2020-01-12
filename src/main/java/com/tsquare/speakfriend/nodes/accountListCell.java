package com.tsquare.speakfriend.nodes;

import com.tsquare.speakfriend.account.AccountController;
import com.tsquare.speakfriend.account.preview.AccountPreview;

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

            setText(account.getAccountName());

            this.setOnMouseClicked(e -> {
                AccountController accountController = new AccountController();
                try {
                    accountController.showAccountDetails(account.getAccountId());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }
}
