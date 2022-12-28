import com.tsquare.speakfriend.crypt.Crypt;
import com.tsquare.speakfriend.database.model.AccountsModel;
import com.tsquare.speakfriend.database.model.SystemSettingsModel;
import com.tsquare.speakfriend.database.model.UserSettingsModel;
import com.tsquare.speakfriend.database.model.UsersModel;
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
        String hashedPass = Crypt.generatePassword("pass");
        usersModel.createUser("test-name", hashedPass);
    }

    @Test
    @Order(2)
    void canGetUsers() throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        UsersModel usersModel = new UsersModel();

        ResultSet resultSet = usersModel.getUser(1);

        Assertions.assertEquals("test-name", resultSet.getString("name"));

        resultSet.close();

        String hashedPass = Crypt.generatePassword("pass");
        usersModel.createUser("test", hashedPass);

        ResultSet resultSet1 = usersModel.getUsers();

        int count = 0;
        while(resultSet1.next()) {
            count++;
        }

        Assertions.assertEquals(2, count);
    }

    @Test
    @Order(3)
    void canUpdateUsers() throws SQLException {
        UsersModel usersModel = new UsersModel();
        usersModel.updateUser(1, "new");

        ResultSet resultSet = usersModel.getUser(1);

        Assertions.assertEquals("new", resultSet.getString("name"));
    }

    @Test
    @Order(4)
    void canDeleteUsers() throws SQLException {
        UsersModel usersModel = new UsersModel();
        usersModel.deleteUser(2);

        ResultSet resultSet = usersModel.getUser(2);

        Assertions.assertFalse(resultSet.next());
    }

    @Test
    @Order(5)
    void canCreateAccounts() throws SQLException {
        AccountsModel accountsModel = new AccountsModel();
        accountsModel.createUserAccount(1, "test-name", "user", "pass", "url", "notes");
    }

    @Test
    @Order(6)
    void canGetAccounts() throws SQLException {
        AccountsModel accountsModel = new AccountsModel();

        ResultSet resultSet = accountsModel.getAccount(1);

        Assertions.assertEquals("test-name", resultSet.getString("name"));

        resultSet.close();

        accountsModel.createUserAccount(1, "test", "user", "pass", "url", "notes");

        ResultSet resultSet1 = accountsModel.getAccounts();

        int count = 0;
        while(resultSet1.next()) {
            count++;
        }

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

        Assertions.assertEquals(2, counter);
    }

    @Test
    @Order(7)
    void canUpdateAccounts() throws SQLException {
        AccountsModel accountsModel = new AccountsModel();
        accountsModel.updateAccount(1, "new", "user", "pass", "url", "notes");

        ResultSet resultSet = accountsModel.getAccount(1);

        Assertions.assertEquals("new", resultSet.getString("name"));
    }

    @Test
    @Order(8)
    void canDeleteAccounts() throws SQLException {
        AccountsModel accountsModel = new AccountsModel();
        accountsModel.deleteAccount(2);

        ResultSet resultSet = accountsModel.getAccount(2);

        Assertions.assertFalse(resultSet.next());
    }

    @Test
    @Order(9)
    void canCreateUserSettings() throws SQLException {
        UserSettingsModel userSettingsModel = new UserSettingsModel();
        userSettingsModel.createUserSetting(1, "option", "test");
    }

    @Test
    @Order(10)
    void canGetUserSettings() throws SQLException {
        UserSettingsModel userSettingsModel = new UserSettingsModel();

        ResultSet resultSet = userSettingsModel.getUserSetting(1, "option");

        Assertions.assertEquals("test", resultSet.getString("value"));
    }

    @Test
    @Order(11)
    void canUpdateUserSettings() throws SQLException {
        UserSettingsModel userSettingsModel = new UserSettingsModel();

        userSettingsModel.updateUserSetting(1, "option", "new");

        ResultSet resultSet = userSettingsModel.getUserSetting(1, "option");

        Assertions.assertEquals("new", resultSet.getString("value"));
    }

    @Test
    @Order(12)
    void canDeleteUserSettings() throws SQLException {
        UserSettingsModel userSettingsModel = new UserSettingsModel();
        userSettingsModel.deleteUserSetting(1, "option");

        ResultSet resultSet = userSettingsModel.getUserSetting(1, "option");

        Assertions.assertFalse(resultSet.next());
    }

    @Test
    @Order(13)
    void canCreateSystemSettings() throws SQLException {
        SystemSettingsModel systemSettingsModel = new SystemSettingsModel();
        systemSettingsModel.createSystemSetting("option", "test");
    }

    @Test
    @Order(14)
    void canGetSystemSettings() throws SQLException {
        SystemSettingsModel systemSettingsModel = new SystemSettingsModel();

        ResultSet resultSet = systemSettingsModel.getSystemSetting("option");

        Assertions.assertEquals("test", resultSet.getString("value"));
    }

    @Test
    @Order(15)
    void canUpdateSystemSettings() throws SQLException {
        SystemSettingsModel systemSettingsModel = new SystemSettingsModel();

        systemSettingsModel.updateSystemSetting("option", "new");

        ResultSet resultSet = systemSettingsModel.getSystemSetting("option");

        Assertions.assertEquals("new", resultSet.getString("value"));
    }

    @Test
    @Order(16)
    void canDeleteSystemSettings() throws SQLException {
        SystemSettingsModel systemSettingsModel = new SystemSettingsModel();
        systemSettingsModel.deleteSystemSetting("option");

        ResultSet resultSet = systemSettingsModel.getSystemSetting("option");

        Assertions.assertFalse(resultSet.next());
    }
}
