package com.mapps.authentificationhandler;


import com.mapps.authentificationhandler.exceptions.InvalidTokenException;
import com.mapps.authentificationhandler.exceptions.InvalidUserException;

import javax.ejb.Local;

import com.mapps.model.Role;
import com.mapps.model.User;

/**
 * Handles authentication of users
 */
@Local
public interface AuthenticationHandler {

    /**
     * Checks if a token has not expired
     * @param token the token to validate
     * @return true if the token has not expired and is a valid token, false if not
     */
    public boolean validateToken(String token);

    /**
     * Generates a token for a given user
     * @param user the user to generate the token for
     * @return a token to manage the authentication of the user
     * @throws InvalidUserException in case the user is not registered
     */
    public String authenticate(User user) throws InvalidUserException;

    /**
     * Gets the user that corresponds with a given token
     * @param token the token to get the user from
     * @return the user that corresponds with the given token
     * @throws InvalidTokenException if the token is not valid
     */
    public User getUserOfToken(String token) throws InvalidTokenException;

    /**
     * Checks if the user that corresponds with the given token has the given role
     * @param token the token to correspond with a user
     * @param role the role that the user must have
     * @return true in case that the user that corresponds with the token has that role, false if not
     * @throws InvalidTokenException if the token is not a valid one
     */
    public boolean isUserInRole(String token,Role role) throws InvalidTokenException;

}
