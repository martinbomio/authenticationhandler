package edu.umflix.authenticationhandler;

import java.util.List;

import edu.umflix.authenticationhandler.exceptions.InvalidTokenException;
import edu.umflix.model.Role;
import edu.umflix.model.User;

/**
 * Handles authentication of users
 */
public interface AuthenticationHandler {

    /**
     * Checks if a token has not expired and if it belongs to a user that has a specific role
     * @param token the token to validate
     * @param roles list of roles to validate with the token
     * @return true if the token has not expired and if it corresponds to a user that has one of the roles of the list, false if not
     * @throws InvalidTokenException if the token is not valid
     */
    public boolean validateToken(String token,List<Role> roles) throws InvalidTokenException;

    /**
     * Generates a token for a given user
     * @param user the user to generate the token for
     * @return a token to manage the authentication of the user
     */
    public String authenticate(User user);

    /**
     * Gets the user that corresponds with a given token
     * @param token the token to get the user from
     * @return the user that corresponds with the given token
     * @throws InvalidTokenException if the token is not valid
     */
    public User getUserOfToken(String token) throws InvalidTokenException;

}
