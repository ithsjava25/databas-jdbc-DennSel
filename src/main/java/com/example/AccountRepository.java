package com.example;

import java.util.Optional;

/*
   4) Create an account (prompts: first name, last name, ssn, password; prints confirmation).
   5) Update an account password (prompts: user_id, new password; prints confirmation).
   6) Delete an account (prompts: user_id; prints confirmation).
 */

public interface AccountRepository {
     boolean createAccount(String firstName, String lastName, String ssn, String password);
     boolean updatePassword(long id, String password);
     boolean deleteAccount(long id);
     boolean isLoginValid(String username, String password);
}
