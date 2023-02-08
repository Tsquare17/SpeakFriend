import com.tsquare.speakfriend.session.UserSession;
import com.tsquare.speakfriend.utils.Crypt;
import org.junit.jupiter.api.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Order(5)
public class CryptographyTest {
    @Test
    public void canGeneratePassword() throws NoSuchAlgorithmException, InvalidKeySpecException {
        Crypt crypt = new Crypt();
        String password = "testing";
        String hash = crypt.generatePassword(password);

        Assertions.assertTrue(crypt.match(password, hash));
    }

    @Test
    public void canEncryptAndDecrypt() {
        Crypt crypt = new Crypt();
        String text = "testing";

        UserSession userSession = UserSession.getInstance();

        String encrypted = null;
        try {
            encrypted = crypt.encrypt(userSession.getKey(), text);
        } catch (InvalidAlgorithmParameterException | IllegalBlockSizeException | NoSuchPaddingException | NoSuchAlgorithmException | InvalidKeySpecException | BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }

        Assertions.assertNotNull(encrypted);

        String decrypted = null;
        try {
            decrypted = crypt.decrypt(userSession.getKey(), encrypted);
        } catch (InvalidAlgorithmParameterException | NoSuchPaddingException | IllegalBlockSizeException | NoSuchAlgorithmException | InvalidKeySpecException | BadPaddingException | InvalidKeyException e) {
            e.printStackTrace();
        }

        Assertions.assertEquals(text, decrypted);
    }
}
