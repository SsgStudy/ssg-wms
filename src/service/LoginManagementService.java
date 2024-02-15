package service;

/**
 * The interface Login management service.
 */
public interface LoginManagementService {

    /**
     * Log in int.
     *
     * @param id       the id
     * @param password the password
     * @return the int
     */
    int logIn(String id, String password);

    /**
     * Log out.
     */
    void logOut();
}
