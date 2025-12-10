package com.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class JdbcAccountRepository implements AccountRepository{
    private final DataSource dataSource;

    // Constructor inject DataSource
    public JdbcAccountRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public boolean createAccount(String firstName, String lastName, String ssn, String password) {
        String query = "insert into account (first_name, last_name, ssn, password, name) values (?, ?, ?, ?, ?)";

        // Create username with first 3 of firstname and lastname
        String username = nameSubstring(firstName) + nameSubstring(lastName);

        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, ssn);
            statement.setString(4, password);
            statement.setString(5, username);

            int updated = statement.executeUpdate();
            // Return true if account created, else false
            return updated > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean updatePassword(long id, String password) {
        if (password == null) {
            throw new IllegalArgumentException("Password cannot be null");
        }

        String query = "update account set password = ? where user_id = ?";

        try(Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)){

            statement.setString(1, password);
            statement.setLong(2, id);

            int updated = statement.executeUpdate();
            // Return true if password updated, else false
            return updated > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean deleteAccount(long id) {
        String query = "delete from account where user_id = ?";

        try (Connection connection = dataSource.getConnection();
            PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setLong(1, id);
            int update = statement.executeUpdate();

            // Return true if account deleted, else false
            return update > 0;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isLoginValid(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password cannot be null");
        }

        String query = "select count(*) from account where name = ? and password = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, username);
            statement.setString(2, password);
            try(ResultSet rs = statement.executeQuery()){
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /* ############ EXTRA ############## */

    public String nameSubstring (String name) {
        // Length of 3 or name length if shorter than 3
        int length = Math.min(name.length(), 3);
        return name.substring(0, length);
    }
}
