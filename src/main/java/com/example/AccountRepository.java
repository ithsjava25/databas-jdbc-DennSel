package com.example;

public interface AccountRepository {
     boolean createAccount(String firstName, String lastName, String ssn, String password);
     boolean updatePassword(long id, String password);
     boolean deleteAccount(long id);
     boolean isLoginValid(String username, String password);
}
