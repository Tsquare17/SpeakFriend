import com.tsquare.speakfriend.database.model.*;
import com.tsquare.speakfriend.utils.Crypt;
import org.junit.jupiter.api.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Order(2)
public class DatabaseModelTest {
    @Test
    @Order(1)
    void canCreateUsers() throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        UsersModel usersModel = new UsersModel();
        Crypt crypt = new Crypt();
        String hashedPass = crypt.generatePassword("pass");
        usersModel.createUser("test-name", hashedPass);
        usersModel.close();
    }

    @Test
    @Order(2)
    void canGetUsers() throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        UsersModel usersModel = new UsersModel();

        ResultSet resultSet = usersModel.getUser(1);

        Assertions.assertEquals("test-name", resultSet.getString("name"));

        resultSet.close();

        usersModel.reset();

        Crypt crypt = new Crypt();
        String hashedPass = crypt.generatePassword("pass");
        usersModel.createUser("test", hashedPass);

        ResultSet resultSet1 = usersModel.getUsers();

        int count = 0;
        while(resultSet1.next()) {
            count++;
        }

        resultSet1.close();
        usersModel.close();

        Assertions.assertEquals(2, count);
    }

    @Test
    @Order(3)
    void canUpdateUsers() throws SQLException {
        UsersModel usersModel = new UsersModel();
        usersModel.updateUser(1, "new");

        ResultSet resultSet = usersModel.getUser(1);

        Assertions.assertEquals("new", resultSet.getString("name"));

        resultSet.close();

        usersModel.close();
    }

    @Test
    @Order(4)
    void canDeleteUsers() throws SQLException {
        UsersModel usersModel = new UsersModel();
        usersModel.deleteUser(2);

        ResultSet resultSet = usersModel.getUser(2);

        Assertions.assertFalse(resultSet.next());

        resultSet.close();
        usersModel.close();
    }

    @Test
    @Order(5)
    void canCreateAccounts() throws SQLException {
        AccountsModel accountsModel = new AccountsModel();
        accountsModel.createUserAccount(1, "test-name", "user", "pass", "url", "notes");
        accountsModel.close();
    }

    @Test
    @Order(6)
    void canGetAccounts() throws SQLException {
        AccountsModel accountsModel = new AccountsModel();

        ResultSet resultSet = accountsModel.getAccount(1);

        Assertions.assertEquals("test-name", resultSet.getString("name"));

        resultSet.close();
        accountsModel.reset();

        accountsModel.createUserAccount(1, "test", "user", "pass", "url", "notes");

        ResultSet resultSet1 = accountsModel.getAccounts();

        int count = 0;
        while(resultSet1.next()) {
            count++;
        }

        resultSet1.close();
        accountsModel.close();

        Assertions.assertEquals(2, count);
    }

    @Test
    @Order(7)
    void canGetAccountsByIds() throws SQLException {
        AccountsModel accountsModel = new AccountsModel();

        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);

        ResultSet resultSet = accountsModel.getAccountsByIds(list);

        int counter = 0;
        while(resultSet.next()) {
            counter++;
        }

        resultSet.close();
        accountsModel.close();

        Assertions.assertEquals(2, counter);
    }

    @Test
    @Order(7)
    void canUpdateAccounts() throws SQLException {
        AccountsModel accountsModel = new AccountsModel();
        accountsModel.updateAccount(1, "new", "user", "pass", "url", "notes");

        ResultSet resultSet = accountsModel.getAccount(1);

        Assertions.assertEquals("new", resultSet.getString("name"));

        resultSet.close();
        accountsModel.close();
    }

    @Test
    @Order(8)
    void canCreateUserAccountTags() throws SQLException {
        TagsModel tagsModel = new TagsModel();
        int tagId = tagsModel.createUserTag(1, "Test");
        tagsModel.reset();

        tagsModel.createAccountTag(1, tagId);
        tagsModel.reset();

        ResultSet resultSet = tagsModel.getUserTags(1);

        resultSet.close();
        tagsModel.reset();

        resultSet = tagsModel.getAccountTags(1);

        Assertions.assertEquals("Test", resultSet.getString("user_tag_name"));

        resultSet.close();

        tagsModel.close();
    }

    @Test
    @Order(9)
    void canDeleteAccounts() throws SQLException {
        AccountsModel accountsModel = new AccountsModel();
        accountsModel.deleteAccount(2);

        ResultSet resultSet = accountsModel.getAccount(2);

        Assertions.assertFalse(resultSet.next());

        resultSet.close();
        accountsModel.close();
    }

    @Test
    @Order(10)
    void canCreateUserSettings() throws SQLException {
        UserSettingsModel userSettingsModel = new UserSettingsModel();
        userSettingsModel.createUserSetting(1, "option", "test");
        userSettingsModel.close();
    }

    @Test
    @Order(11)
    void canGetUserSettings() throws SQLException {
        UserSettingsModel userSettingsModel = new UserSettingsModel();

        ResultSet resultSet = userSettingsModel.getUserSetting(1, "option");

        Assertions.assertEquals("test", resultSet.getString("value"));

        resultSet.close();
        userSettingsModel.close();
    }

    @Test
    @Order(12)
    void canUpdateUserSettings() throws SQLException {
        UserSettingsModel userSettingsModel = new UserSettingsModel();

        userSettingsModel.updateUserSetting(1, "option", "new");

        ResultSet resultSet = userSettingsModel.getUserSetting(1, "option");

        Assertions.assertEquals("new", resultSet.getString("value"));

        resultSet.close();
        userSettingsModel.close();
    }

    @Test
    @Order(13)
    void canDeleteUserSettings() throws SQLException {
        UserSettingsModel userSettingsModel = new UserSettingsModel();
        userSettingsModel.deleteUserSetting(1, "option");

        ResultSet resultSet = userSettingsModel.getUserSetting(1, "option");

        Assertions.assertFalse(resultSet.next());

        resultSet.close();
        userSettingsModel.close();
    }

    @Test
    @Order(14)
    void canCreateSystemSettings() throws SQLException {
        SystemSettingsModel systemSettingsModel = new SystemSettingsModel();
        systemSettingsModel.createSystemSetting("option", "test");
        systemSettingsModel.close();
    }

    @Test
    @Order(15)
    void canGetSystemSettings() throws SQLException {
        SystemSettingsModel systemSettingsModel = new SystemSettingsModel();

        ResultSet resultSet = systemSettingsModel.getSystemSetting("option");

        Assertions.assertEquals("test", resultSet.getString("value"));

        resultSet.close();
        systemSettingsModel.close();
    }

    @Test
    @Order(16)
    void canUpdateSystemSettings() throws SQLException {
        SystemSettingsModel systemSettingsModel = new SystemSettingsModel();

        systemSettingsModel.updateSystemSetting("option", "new");

        ResultSet resultSet = systemSettingsModel.getSystemSetting("option");

        Assertions.assertEquals("new", resultSet.getString("value"));

        resultSet.close();
        systemSettingsModel.close();
    }

    @Test
    @Order(17)
    void canDeleteSystemSettings() throws SQLException {
        SystemSettingsModel systemSettingsModel = new SystemSettingsModel();
        systemSettingsModel.deleteSystemSetting("option");

        ResultSet resultSet = systemSettingsModel.getSystemSetting("option");

        Assertions.assertFalse(resultSet.next());

        resultSet.close();
        systemSettingsModel.close();
    }
}
