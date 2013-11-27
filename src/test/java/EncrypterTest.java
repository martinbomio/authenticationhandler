import com.mapps.authentificationhandler.encryption.Encrypter;
import com.mapps.authentificationhandler.encryption.exception.ErrorInDecryptionException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 *
 */
public class EncrypterTest {

    Encrypter encrypter;

    @Before
    public void prepare() {
        encrypter = new Encrypter();
    }

    @Test
    public void testEncryptInputNull() {
        try {
            String s = null;
            encrypter.encrypt(s);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testDecrypt() {
        String clearText = "cristalClear";
        String cipherText = encrypter.encrypt(clearText);
        try {
            assertEquals(clearText,encrypter.decrypt(cipherText));
        } catch (ErrorInDecryptionException e) {
            fail();
        }
    }

    @Test
    public void testDecryptNotEncryptedMessage() {
        String clearText = "cristalClear";
        try {
            String cipher = encrypter.decrypt(clearText);
            fail();
        } catch (ErrorInDecryptionException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testDecryptNullMessage() {
        String text = null;
        try {
            String cipher = encrypter.decrypt(text);
            fail();
        } catch (ErrorInDecryptionException e) {
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }
}
