package service;

import dao.LoginManagementDAOImpl;

/**
 * The type Login management service.
 */
public class LoginManagementServiceImpl implements LoginManagementService {
    private final LoginManagementDAOImpl loginDao = LoginManagementDAOImpl.getInstance();

    @Override
    public int logIn(String id, String password) {
        return loginDao.logIn(id, password);
    }

    @Override
    public void logOut() {
        loginDao.logOut();
    }
}
