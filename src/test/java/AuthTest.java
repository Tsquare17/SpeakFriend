import com.tsquare.speakfriend.session.UserSession;
import com.tsquare.speakfriend.utils.Auth;
import org.junit.jupiter.api.*;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Order(4)
public class AuthTest {
    @Test
    @Order(1)
    public void canAuthenticate() throws SQLException, InvalidKeySpecException, NoSuchAlgorithmException {
        UserSession userSession = UserSession.getInstance();
        userSession.clear();

        Assertions.assertEquals(0, userSession.getVersion());

        Auth auth = new Auth();

        Assertions.assertTrue(auth.checkIn("new", "pass"));
    }

    @AfterAll
    static void tearDown() {
        TestSuite.tearDown();
    }
}
