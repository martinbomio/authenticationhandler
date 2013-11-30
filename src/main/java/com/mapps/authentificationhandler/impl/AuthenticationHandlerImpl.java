package com.mapps.authentificationhandler.impl;

import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.log4j.Logger;

import com.mapps.authentificationhandler.AuthenticationHandler;
import com.mapps.authentificationhandler.encryption.Encrypter;
import com.mapps.authentificationhandler.exceptions.InvalidTokenException;
import com.mapps.authentificationhandler.exceptions.InvalidUserException;
import com.mapps.authentificationhandler.model.Token;
import com.mapps.exceptions.UserNotFoundException;
import com.mapps.model.Role;
import com.mapps.model.User;
import com.mapps.persistence.UserDAO;

@Stateless(name = "AuthenticationHandler")
public class AuthenticationHandlerImpl implements AuthenticationHandler {

    private static Logger logger = Logger.getLogger(AuthenticationHandlerImpl.class);
    private Encrypter encrypter;
    private int duration = 6000;

    @EJB(beanName = "UserDao")
    protected UserDAO userDao;

    public AuthenticationHandlerImpl() {
        this.encrypter = new Encrypter();
    }

    /**
     * sets the default token duration in seconds
     * @param duration of the tokens in seconds
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public boolean validateToken(String token) {
        if(token==null){
            logger.error("validate token ran with token null");
            throw new IllegalArgumentException("Token is null");
        }
        try {
            Token aToken = Token.getToken(token);
            Date date = new Date();
            long now = date.getTime();
            long createdAt = aToken.getCreatedAt();
            long difference = now-createdAt;
            long maxDifference = duration*1000;
            if(difference>maxDifference){
                logger.trace("validate token: token expired");
                return false;
            }
            String username = aToken.getUsername();
            User user = userDao.getUserByUsername(username);
            if(user.getPassword().equals(aToken.getPassword())){
                    logger.trace("validate token: token valid");
                    return true;
            }else{
                logger.trace("validate token: password of token not correct");
                return false;
            }

        } catch (InvalidTokenException e) {
            logger.trace("validate token: token invalid");
            return false;
        } catch (UserNotFoundException e) {
            logger.trace("validate token: user not found");
            return false;
        }
    }

    @Override
    public String authenticate(User user) throws InvalidUserException{
        if(user==null){
            logger.error("authenticate ran with user null");
            throw new InvalidUserException();
        }
        try {
            logger.trace("authenticate ran with user "+user.getUserName());
            User storedUser = userDao.getUserByUsername(user.getUserName());
            String name = user.getUserName();
            if(storedUser!=null && storedUser.getUserName().equals(user.getUserName())
                    && storedUser.getPassword().equals(user.getPassword())){
                return (new Token(storedUser.getUserName(),storedUser.getPassword())).toString();
            }else{
                logger.trace("authenticate user "+user.getUserName()+" not the same with stored");
                throw new InvalidUserException();
            }
        } catch (UserNotFoundException e) {
            logger.trace("in authenticate user " + user.getUserName() + " not found");
            throw new InvalidUserException();
        }
    }

    @Override
    public User getUserOfToken(String token) throws InvalidTokenException {
       if(token==null){
           logger.error("getUserOftoken token is null");
           throw new InvalidTokenException();
       }
       if(!this.validateToken(token)){
           throw new InvalidTokenException();
       }
       Token aToken = Token.getToken(token);
        try {
            return userDao.getUserByUsername(aToken.getUsername());
        } catch (UserNotFoundException e) {
            logger.trace("getUserOfToken user not found");
            throw new InvalidTokenException();
        }
    }



    @Override
    public boolean isUserInRole(String token, Role role) throws InvalidTokenException {
        if(token==null){
            logger.error("isUserInRole token is null");
            throw new InvalidTokenException();
        }
        if(role==null){
            logger.error("isUserInRole role is null");
            throw new IllegalArgumentException("Role is null");
        }
        if(!this.validateToken(token)){
            throw new InvalidTokenException();
        }
        Token aToken = Token.getToken(token);
        try {
            User user = userDao.getUserByUsername(aToken.getUsername());
            Role storedRole = user.getRole();
            if(role.toInt() == storedRole.toInt()){
                logger.trace("isUserInRole is true");
                return true;
            }else{
                logger.trace("isUserInRole is false");
                return false;
            }
        } catch (UserNotFoundException e) {
            logger.trace("isUserInRole user not found");
            throw new InvalidTokenException();
        }
    }

}
