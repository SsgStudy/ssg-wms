package service;

public interface LoginManagementService {
    int logIn(String id, String password);
    void logOut();
}
