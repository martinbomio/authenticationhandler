package edu.umflix.authenticationhandler.model;

import edu.umflix.authenticationhandler.encryption.Encrypter;
import edu.umflix.authenticationhandler.encryption.exception.ErrorInDecryptionException;
import edu.umflix.authenticationhandler.exceptions.InvalidTokenException;
import org.apache.log4j.Logger;

import java.util.Date;

/**
 * Represents a token
 */
public class Token {

    String email;
    String password;
    long createdAt;
    static Encrypter encrypter = new Encrypter();
    private static Logger logger = Logger.getLogger(Token.class);

    public Token(String email, String password, long createdAt) {
        this.email = email;
        this.password = password;
        this.createdAt = createdAt;
    }

    public Token(String email, String password) {
        this.email = email;
        this.password = password;
        this.createdAt = (new Date()).getTime();
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
       String decrypted = Long.toString(this.createdAt)+"@"+this.email+"@"+this.password;
       return encrypter.encrypt(decrypted);
    }

    public static Token getToken(String token) throws InvalidTokenException {
        try {
            String decrypted = encrypter.decrypt(token);
            String[] pieces = decrypted.split("@",4);
            String s = pieces[0];
            long createdAt = Long.valueOf(s);
            String email = pieces[1]+"@"+pieces[2];
            String password = pieces[3];
            return new Token(email,password,createdAt);
        } catch (ErrorInDecryptionException e) {
            logger.warn("getToken token not valid");
            throw new InvalidTokenException();
        }
    }

}
