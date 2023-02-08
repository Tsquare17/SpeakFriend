import com.tsquare.speakfriend.database.model.UsersModel;
import com.tsquare.speakfriend.session.ApplicationSession;
import com.tsquare.speakfriend.session.UserSession;
import org.junit.jupiter.api.*;

import java.sql.ResultSet;
import java.sql.SQLException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Order(3)
public class SessionTest {
    @Test
    @Order(1)
    public void canSetApplicationSession() {
        ApplicationSession applicationSession = ApplicationSession.getInstance();

        applicationSession.setDirtyAccounts(false);
        applicationSession.setLoadingMessage("loading");
        applicationSession.setSelectedAccountId(1);
        applicationSession.setExportFileString("export");
        applicationSession.setAccountSearchString("search");

        Assertions.assertFalse(applicationSession.isDirtyAccounts());
        Assertions.assertEquals("loading", applicationSession.getLoadingMessage());
        Assertions.assertEquals(1, applicationSession.getSelectedAccountId());
        Assertions.assertEquals("export", applicationSession.getExportFileString());
        Assertions.assertEquals("search", applicationSession.getAccountSearchString());
    }

    @Test
    @Order(2)
    public void canSetUserSession() throws SQLException {
        UsersModel usersModel = new UsersModel();
        ResultSet resultSet = usersModel.getUser(1);

        UserSession userSession = UserSession.getInstance();
        userSession.setUser(resultSet);
        userSession.setKey("key");
        userSession.setVersion(1);

        Assertions.assertEquals(1, userSession.getId());
        Assertions.assertEquals("new", userSession.getName());

        resultSet.close();
        usersModel.close();
    }

    @Test
    @Order(3)
    public void canGetUserSessionProperties() {
        UserSession userSession = UserSession.getInstance();

        Assertions.assertEquals("new", userSession.getName());
        Assertions.assertEquals("key", userSession.getKey());
        Assertions.assertEquals(1, userSession.getVersion());
    }
}
