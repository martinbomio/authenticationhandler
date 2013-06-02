package edu.umflix.authenticationhandler.impl;

import edu.umflix.authenticationhandler.AuthenticationHandler;
import edu.umflix.authenticationhandler.encryption.Encrypter;
import edu.umflix.authenticationhandler.exceptions.InvalidTokenException;
import edu.umflix.authenticationhandler.exceptions.InvalidUserException;
import edu.umflix.exceptions.UserNotFoundException;
import edu.umflix.model.Role;
import edu.umflix.model.User;
import edu.umflix.persistence.RoleDao;
import edu.umflix.persistence.UserDao;
import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless(name = "AuthenticationService")
public class AuthenticationHandlerImpl implements AuthenticationHandler {

    private static Logger logger = Logger.getLogger(AuthenticationHandlerImpl.class);
    private Encrypter encrypter;
    private static final int duration = 6000;



    @EJB(beanName = "RoleDao")
    RoleDao roleDao;

    @EJB(beanName = "UserDao")
    UserDao userDao;

    public AuthenticationHandlerImpl() {
        this.encrypter = new Encrypter();
    }

    @Override
    public boolean validateToken(String token) {
        return false;  //TODO
    }

    @Override
    public String authenticate(User user) throws InvalidUserException{
        if(user==null){
            throw new IllegalArgumentException("User is null");
        }
        if(user.getEmail()==null){
            throw new InvalidUserException();
        }
        try {
            User storedUser = userDao.getUser(user.getEmail());
            if(storedUser.equals(user)){
                 return null; //TODO
            }else{
                throw new InvalidUserException();
            }
        } catch (UserNotFoundException e) {
            throw new InvalidUserException();
        }


    }

    @Override
    public User getUserOfToken(String token) throws InvalidTokenException {
        return null;  //TODO
    }

    @Override
    public boolean isUserInRole(String token, Role role) throws InvalidTokenException {
        return false;  //TODO
    }
}
