package com.example;

import javax.security.auth.login.AccountNotFoundException;
import java.sql.*;
import java.util.Arrays;
import java.util.Scanner;

public class Main {

    private final Scanner scanner = new Scanner(System.in);
    private AccountRepository accountRepository;
    private DataSource dataSource;

    static void main(String[] args) {
        if (isDevMode(args)) {
            DevDatabaseInitializer.start();
        }
        new Main().run();
    }

    public void run() {
        // Resolve DB settings with precedence: System properties -> Environment variables
        String jdbcUrl = resolveConfig("APP_JDBC_URL", "APP_JDBC_URL");
        String dbUser = resolveConfig("APP_DB_USER", "APP_DB_USER");
        String dbPass = resolveConfig("APP_DB_PASS", "APP_DB_PASS");

        if (jdbcUrl == null || dbUser == null || dbPass == null) {
            throw new IllegalStateException(
                    "Missing DB configuration. Provide APP_JDBC_URL, APP_DB_USER, APP_DB_PASS " +
                            "as system properties (-Dkey=value) or environment variables.");
        }

        // Creates a DataSource once at startup
        dataSource = new SimpleDriverManagerDataSource(jdbcUrl, dbUser, dbPass);

        // Creates and injects repositories with DataSource
        accountRepository = new JdbcAccountRepository(dataSource);

        if (login()) {
            menu();
        }

        scanner.close();
    }

    private void menu (){
        String number = scanner.nextLine();
        System.out.println("scanner menu");

        boolean isInMenu = true;
        while (isInMenu) {
            switch (number) {
                case "1" -> System.out.println("test");// listMoonMissions();
                case "2" -> System.out.println("test");// getMissionById();
                case "3" -> System.out.println("test");// countMissionsByYear();
                case "4" -> createAccount();
                case "5" -> updatePassword();
                case "6" -> deleteAccount();
                case "0" -> isInMenu = false;
                default -> System.out.println("Choose a number between 0 and 6.");
            }
        }
    }

    // ######### ACCOUNT RELATED ###########

    private boolean login (){
        try {
            System.out.println("Enter username:");
            String username = scanner.nextLine();


            System.out.println("Enter password:");
            String password = scanner.nextLine();


            boolean isLoginValid = accountRepository.isLoginValid(username, password);

            if (isLoginValid) {
                System.out.println("Login Successful!");
            }
            else {
                System.out.println("Invalid username or password");
            }
            return isLoginValid;

        }catch (Exception e){
            System.out.println(e.getMessage());
            return false;
        }
    }

    private void updatePassword() {
        try {
            System.out.println("Enter id of the account you want to update the password:");
            long id = Long.parseLong(scanner.nextLine());

            System.out.println("Enter password:");
            String password = scanner.nextLine();

            // Update the password
            boolean isUpdated = accountRepository.updatePassword(id, password);

            if (isUpdated) {
                System.out.println("Password updated successfully!");
            }
            else {
                System.out.println("Password could not be updated!");
            }
        }
        catch (NumberFormatException e) {
            System.out.println("Please enter valid id and password.");
        }
    }

    private void deleteAccount() {
        try {
            System.out.println("Enter id of the account you want to delete:");
            long id = Long.parseLong(scanner.nextLine());

            boolean isDeleted = accountRepository.deleteAccount(id);

            if(isDeleted) {
                System.out.println("Account deleted successfully!");
            } else {
                System.out.println("Account could not be deleted!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid id.");
        }
    }

    private void createAccount () {
        try {
            System.out.println("Enter first name:");
            String firstName = scanner.nextLine();


            System.out.println("Enter last name:");
            String lastName = scanner.nextLine();


            System.out.println("Enter ssn (xxxxxx-xxxx):");
            String ssn = scanner.nextLine();


            System.out.println("Enter password:");
            String password = scanner.nextLine();


            // Create the account
            boolean isUpdated = accountRepository.createAccount(firstName, lastName, ssn, password);

            if (isUpdated) {
                System.out.println("Account created.");
            }
            else {
                System.out.println("Account could not be created.");
            }
        }
        catch (NumberFormatException e) {
            System.out.println("Please enter valid values.");
        }
    }

    // ######### MOON MISSION RELATED ###########














    /**
     * Determines if the application is running in development mode based on system properties,
     * environment variables, or command-line arguments.
     *
     * @param args an array of command-line arguments
     * @return {@code true} if the application is in development mode; {@code false} otherwise
     */
    private static boolean isDevMode(String[] args) {
        if (Boolean.getBoolean("devMode"))  //Add VM option -DdevMode=true
            return true;
        if ("true".equalsIgnoreCase(System.getenv("DEV_MODE")))  //Environment variable DEV_MODE=true
            return true;
        return Arrays.asList(args).contains("--dev"); //Argument --dev
    }

    /**
     * Reads configuration with precedence: Java system property first, then environment variable.
     * Returns trimmed value or null if neither source provides a non-empty value.
     */
    private static String resolveConfig(String propertyKey, String envKey) {
        String v = System.getProperty(propertyKey);
        if (v == null || v.trim().isEmpty()) {
            v = System.getenv(envKey);
        }
        return (v == null || v.trim().isEmpty()) ? null : v.trim();
    }
}
