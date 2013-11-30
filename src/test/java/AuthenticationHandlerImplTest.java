import stub.AuthenticationHandlerToTest;

import org.junit.Before;
import org.junit.Test;

import com.mapps.authentificationhandler.encryption.Encrypter;
import com.mapps.authentificationhandler.exceptions.InvalidTokenException;
import com.mapps.authentificationhandler.exceptions.InvalidUserException;
import com.mapps.authentificationhandler.impl.AuthenticationHandlerImpl;
import com.mapps.authentificationhandler.model.Token;
import com.mapps.exceptions.UserNotFoundException;
import com.mapps.model.Role;
import com.mapps.model.User;
import com.mapps.persistence.UserDAO;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests AuthenticationHandlerImpl
 */
public class AuthenticationHandlerImplTest {


    AuthenticationHandlerImpl authenticationHandler;
    User registeredUser;
    User unregisteredUser;
    UserDAO userDao;

    @Before
    public void prepare() throws UserNotFoundException {
        //mock role


        //mock user
        User user = mock(User.class);
        when(user.getUserName()).thenReturn("hugo@gmail.com");
        when(user.getName()).thenReturn("huguito");
        when(user.getPassword()).thenReturn("hugo@password@123");
        when(user.getRole()).thenReturn(Role.USER);

        //mock exception
        UserNotFoundException userNotFoundException = mock(UserNotFoundException.class);

        //mock userDao
        userDao = mock(UserDAO.class);
        when(userDao.getUserByUsername("hugo@gmail.com")).thenReturn(user);
        when(userDao.getUserByUsername("unregisteredUser@gmail.com")).thenThrow(userNotFoundException);

        //mock unregisteredUser & mock registeredUser
        registeredUser = mock(User.class);
        when(registeredUser.getUserName()).thenReturn("hugo@gmail.com");
        when(registeredUser.getPassword()).thenReturn("hugo@password@123");
        unregisteredUser = mock(User.class);
        when(unregisteredUser.getUserName()).thenReturn("unregisteredUser@gmail.com");


        AuthenticationHandlerToTest authenticationHandlerToTest = new AuthenticationHandlerToTest();
        authenticationHandlerToTest.setUserDao(userDao);
        authenticationHandler = authenticationHandlerToTest;
    }

    @Test
    public void testAuthenticateValidUser() {
        try {
            String token = authenticationHandler.authenticate(registeredUser);
            assertTrue(true);
        } catch (InvalidUserException e) {
            fail();
        }
    }

    @Test
    public void testAuthenticateWrongPassword() {
        try {
            User wrongUser = mock(User.class);
            when(registeredUser.getUserName()).thenReturn("hugo@gmail.com");
            when(registeredUser.getPassword()).thenReturn("23");
            String token = authenticationHandler.authenticate(wrongUser);
            fail();
        } catch (InvalidUserException e) {
            try {
                verify(userDao,times(0)).getUserByUsername("hugo@gmail.com");
            } catch (UserNotFoundException e1) {
                fail();
            }
        }
    }

    @Test
    public void testAuthenticateInvalidUser() {
        try {
            String token = authenticationHandler.authenticate(unregisteredUser);
            fail();
        } catch (InvalidUserException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testAuthenticateNullUser() {
        User n = null;
        try {
            authenticationHandler.authenticate(n);
            fail();
        } catch (InvalidUserException e) {
            assertTrue(true);
        } catch (IllegalArgumentException e) {
            fail();
        }
    }

    @Test
    public void testValidateTokenValidToken() {
        try {
            String token = authenticationHandler.authenticate(registeredUser);
            assertTrue(authenticationHandler.validateToken(token));
        } catch (InvalidUserException e) {
            fail();
        }
    }

    @Test
    public void testValidateTokenPasswordOfTokenNotCorrect() {
        Encrypter encrypter = new Encrypter();
        Token token = new Token("hugo@gmail.com", "differentPass");
        assertFalse(authenticationHandler.validateToken(token.toString()));
    }

    @Test
    public void testValidateTokenExpiredToken() {
        try {
            String token = authenticationHandler.authenticate(registeredUser);
            authenticationHandler.setDuration(1);
            assert (authenticationHandler.validateToken(token));
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
    public void testValidateTokenInvalidToken() {
        try {
            String token = authenticationHandler.authenticate(registeredUser) + "@";
            assertFalse(authenticationHandler.validateToken(token));
        } catch (InvalidUserException e) {
            fail();
        }
    }

    @Test
    public void testValidateTokenNullToken() {
        String n = null;
        try {
            authenticationHandler.validateToken(n);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testGetUserOfToken() {
        try {
            String token = authenticationHandler.authenticate(registeredUser);
            String name = registeredUser.getUserName();
            String pass = registeredUser.getPassword();
            User user = authenticationHandler.getUserOfToken(token);
            verify(userDao, times(3)).getUserByUsername("hugo@gmail.com");
            User userInDao = userDao.getUserByUsername(registeredUser.getUserName());
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
    public void testGetUserOfTokenInvalidUser() {
        try {
            Token token = new Token(unregisteredUser.getUserName(), "password@user");
            String stringToken = token.toString();
            User user = authenticationHandler.getUserOfToken(stringToken);
            fail();
        } catch (InvalidTokenException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testGetUserOfTokenInvalidToken() {
        try {
            String token = "hola";
            User user = authenticationHandler.getUserOfToken(token);
            fail();
        } catch (InvalidTokenException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testGetUserOfTokenExpiredToken() {
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
    public void testGetUserOfTokenNullToken() {
        String n = null;
        try {
            authenticationHandler.getUserOfToken(n);
            fail();
        } catch (IllegalArgumentException e) {
            fail();
        } catch (InvalidTokenException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testIsUserInRoleValidUserInRole() {
        try {
            String token = authenticationHandler.authenticate(registeredUser);
            assertTrue(authenticationHandler.isUserInRole(token, Role.USER));
            verify(userDao, times(3)).getUserByUsername(registeredUser.getUserName());
        } catch (InvalidUserException e) {
            fail();
        } catch (InvalidTokenException e) {
            fail();
        } catch (UserNotFoundException e) {
            fail();
        }
    }

    @Test
    public void testIsUserInRoleInvalidUser() {
        try {
            Token token = new Token(unregisteredUser.getUserName(), "password@user");
            String stringToken = token.toString();
            boolean b = authenticationHandler.isUserInRole(stringToken, Role.TRAINER);
            fail();
        } catch (InvalidTokenException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testIsUserInRoleValidUserNotInRole() {
        try {
            String token = authenticationHandler.authenticate(registeredUser);
            assertFalse(authenticationHandler.isUserInRole(token, Role.ADMINISTRATOR));
            verify(userDao, times(3)).getUserByUsername(registeredUser.getUserName());
        } catch (InvalidUserException e) {
            fail();
        } catch (InvalidTokenException e) {
            fail();
        } catch (UserNotFoundException e) {
            fail();
        }
    }

    @Test
    public void testIsUserInRoleInvalidToken() {
        try {
            String token = "235@jefef@edfef.efef@ldken";
            boolean b = authenticationHandler.isUserInRole(token, Role.USER);
        } catch (InvalidTokenException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testIsUserInRoleNullToken() {
        String n = null;
        try {
            authenticationHandler.isUserInRole(n, Role.USER);
            fail();
        } catch (IllegalArgumentException e) {
            fail();
        } catch (InvalidTokenException e) {
            assertTrue(true);
        }
    }

    @Test
    public void testIsUserInRoleNullRole() {
        Role n = null;
        try {
            String token = authenticationHandler.authenticate(registeredUser);
            authenticationHandler.isUserInRole(token, n);
            fail();
        } catch (IllegalArgumentException e) {
            assertTrue(true);
        } catch (InvalidTokenException e) {
            fail();
        } catch (InvalidUserException e) {
            fail();
        }
    }
}
