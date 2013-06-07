import edu.umflix.authenticationhandler.AuthenticationHandler;
import edu.umflix.authenticationhandler.exceptions.InvalidTokenException;
import edu.umflix.authenticationhandler.exceptions.InvalidUserException;
import edu.umflix.authenticationhandler.impl.AuthenticationHandlerImpl;
import edu.umflix.authenticationhandler.model.Token;
import edu.umflix.exceptions.UserNotFoundException;
import edu.umflix.model.Role;
import edu.umflix.model.User;
import edu.umflix.persistence.ActivityDao;
import edu.umflix.persistence.MovieDao;
import edu.umflix.persistence.UserDao;
import org.junit.Before;
import org.junit.Test;
import stub.AuthenticationHandlerToTest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 *
 */
public class AuthenticationHandlerImplTest {


    AuthenticationHandlerImpl authenticationHandler;
    User registeredUser;
    User unregisteredUser;
    UserDao userDao;

    @Before
    public void prepare() throws UserNotFoundException {
        //mock role
        Role userRole = mock(Role.class);
        when(userRole.getId()).thenReturn(Long.valueOf(2));

        //mock user
        User user = mock(User.class);
        when(user.getEmail()).thenReturn("hugo@gmail.com");
        when(user.getName()).thenReturn("huguito");
        when(user.getPassword()).thenReturn("hugo@password@123");
        when(user.getRole()).thenReturn(userRole);

        //mock exception
        UserNotFoundException userNotFoundException = mock(UserNotFoundException.class);

        //mock userDao
        userDao = mock(UserDao.class);
        when(userDao.getUser("hugo@gmail.com")).thenReturn(user);
        when(userDao.getUser("unregisteredUser@gmail.com")).thenThrow(userNotFoundException);

        //mock unregisteredUser & mock registeredUser
        registeredUser = mock(User.class);
        when(registeredUser.getEmail()).thenReturn("hugo@gmail.com");
        when(registeredUser.getPassword()).thenReturn("hugo@password@123");
        unregisteredUser = mock(User.class);
        when(unregisteredUser.getEmail()).thenReturn("unregisteredUser@gmail.com");


        AuthenticationHandlerToTest authenticationHandlerToTest = new AuthenticationHandlerToTest();
        authenticationHandlerToTest.setUserDao(userDao);
        authenticationHandler=authenticationHandlerToTest;
    }

    @Test
    public void testAuthenticateValidUser(){
        try {
            String token = authenticationHandler.authenticate(registeredUser);
            assertTrue(true);
        } catch (InvalidUserException e) {
            fail();
        }
    }

    @Test
    public void testAuthenticateInvalidUser(){
    try {
        String token = authenticationHandler.authenticate(unregisteredUser);
        fail();
    } catch (InvalidUserException e) {
        assertTrue(true);
    }
    }

    @Test
    public void testValidateTokenValidToken(){
        try {
            String token = authenticationHandler.authenticate(registeredUser);
            assertTrue(authenticationHandler.validateToken(token));
        } catch (InvalidUserException e) {
            fail();
        }
    }

    @Test
    public void testValidateTokenExpiredToken(){
        try {
            String token = authenticationHandler.authenticate(registeredUser);
            authenticationHandler.setDuration(1);
            assert(authenticationHandler.validateToken(token));
            try {
                Thread.sleep(1001);
                assertFalse(authenticationHandler.validateToken(token));
            } catch (InterruptedException e) {

            }
        } catch (InvalidUserException e) {
            fail();
        }
        authenticationHandler.setDuration(6000);
    }

    @Test
    public void testValidateTokenInvalidToken(){
        try {
            String token = authenticationHandler.authenticate(registeredUser)+"@";
            assertFalse(authenticationHandler.validateToken(token));
        } catch (InvalidUserException e) {
            fail();
        }
    }

    @Test
    public void testGetUserOfToken(){
        try {
            String token = authenticationHandler.authenticate(registeredUser);
            User user = authenticationHandler.getUserOfToken(token);
            verify(userDao, times(3)).getUser("hugo@gmail.com");
            User userInDao = userDao.getUser(registeredUser.getEmail());
            assertTrue(userInDao.equals(user));
        } catch (InvalidUserException e) {
            fail();
        } catch (InvalidTokenException e) {
            fail();
        } catch (UserNotFoundException e) {
            fail();
        }
    }

    @Test
    public void testGetUserOfTokenInvalidUser(){
        try {
            Token token = new Token(unregisteredUser.getEmail(),"password@user");
            String stringToken = token.toString();
            User user = authenticationHandler.getUserOfToken(stringToken);
            fail();
        } catch (InvalidTokenException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testGetUserOfTokenInvalidToken(){
        try {
            String token = "hola";
            User user = authenticationHandler.getUserOfToken(token);
            fail();
        } catch (InvalidTokenException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testGetUserOfTokenExpiredToken(){
        try {
            String token = authenticationHandler.authenticate(registeredUser);
            authenticationHandler.setDuration(1);
            Thread.sleep(1001);
            User user = authenticationHandler.getUserOfToken(token);
            fail();
        } catch (InvalidTokenException e) {
            assertTrue(true);
        } catch (InvalidUserException e) {
            fail();
        } catch (InterruptedException e) {

        }
        authenticationHandler.setDuration(6000);
    }

    @Test
    public void testIsUserInRoleValidUserInRole(){}

    @Test
    public void testIsUserInRoleInvalidUserInRole(){}

    @Test
    public void testIsUserInRoleValidUserNotInRole(){}

    @Test
    public void testIsUserInRoleInvalidToken(){}
}
