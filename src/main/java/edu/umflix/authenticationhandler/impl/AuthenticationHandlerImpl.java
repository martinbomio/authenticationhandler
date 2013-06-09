package edu.umflix.authenticationhandler.impl;

import edu.umflix.authenticationhandler.AuthenticationHandler;
import edu.umflix.authenticationhandler.encryption.Encrypter;
import edu.umflix.authenticationhandler.exceptions.InvalidTokenException;
import edu.umflix.authenticationhandler.exceptions.InvalidUserException;
import edu.umflix.authenticationhandler.model.Token;
import edu.umflix.exceptions.UserNotFoundException;
import edu.umflix.model.Role;
import edu.umflix.model.User;
import edu.umflix.persistence.UserDao;
import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.Date;

@Stateless(name = "AuthenticationService")
public class AuthenticationHandlerImpl implements AuthenticationHandler {

    private static Logger logger = Logger.getLogger(AuthenticationHandlerImpl.class);
    private Encrypter encrypter;
    private int duration = 6000;

    @EJB(beanName = "UserDao")
    protected UserDao userDao;

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
            String email = aToken.getEmail();
            User user = userDao.getUser(email);
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
            logger.trace("authenticate ran with user "+user.getEmail());
            User storedUser = userDao.getUser(user.getEmail());
            if(storedUser!=null && storedUser.getEmail().equals(user.getEmail()) && storedUser.getPassword().equals(user.getPassword())){
                return (new Token(storedUser.getEmail(),storedUser.getPassword())).toString();
            }else{
                logger.trace("authenticate user "+user.getEmail()+"not the same with stored");
                throw new InvalidUserException();
            }
        } catch (UserNotFoundException e) {
            logger.trace("in authenticate user " + user.getEmail() + " not found");
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
            return userDao.getUser(aToken.getEmail());
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
            User user = userDao.getUser(aToken.getEmail());
            Role storedRole = user.getRole();
            if(role.getId()==storedRole.getId()){
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
