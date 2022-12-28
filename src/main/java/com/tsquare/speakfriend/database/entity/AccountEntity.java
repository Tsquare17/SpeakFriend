package com.tsquare.speakfriend.database.entity;

import com.tsquare.speakfriend.crypt.Crypt;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class AccountEntity extends Entity {
    public AccountEntity(ResultSet resultSet) throws SQLException {
        super(resultSet);
    }

    public AccountEntity(int parseInt, String accountName, String accountUser, String accountPass, String accountUrl, String accountNotes) {
        this.props.put("id", parseInt);
        this.props.put("name", accountName);
        this.props.put("user", accountUser);
        this.props.put("pass", accountPass);
        this.props.put("url", accountUrl);
        this.props.put("notes", accountNotes);
    }

    public void decrypt(String key) throws InvalidAlgorithmParameterException, NoSuchPaddingException,
        IllegalBlockSizeException, NoSuchAlgorithmException, InvalidKeySpecException, BadPaddingException,
        InvalidKeyException {
        for (Map.Entry<String, Object> column : props.entrySet()) {
            if (column.getValue() instanceof String) {
                props.replace(
                    column.getKey(),
                    Crypt.decrypt(key, (String) column.getValue())
                );
            }
        }
    }

    public int getId() {
        return (int) props.get("id");
    }

    public String getName() {
        return (String) props.get("name");
    }

    public String getUser() {
        return (String) props.get("user");
    }

    public String getPass() {
        return (String) props.get("pass");
    }

    public String getUrl() {
        return (String) props.get("url");
    }

    public String getNotes() {
        return (String) props.get("notes");
    }
}
