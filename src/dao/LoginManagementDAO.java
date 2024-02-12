package dao;


public interface LoginManagementDAO {
    int logIn(String id, String password);

    void logOut();

}
