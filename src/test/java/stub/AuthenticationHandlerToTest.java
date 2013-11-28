package stub;

import com.mapps.authentificationhandler.impl.AuthenticationHandlerImpl;
import com.mapps.persistence.UserDAO;

/**
 *
 */
public class AuthenticationHandlerToTest extends AuthenticationHandlerImpl {

    public void setUserDao(UserDAO userDao){
        this.userDao=userDao;
    }

}
