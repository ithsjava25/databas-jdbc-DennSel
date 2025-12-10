package com.example;

import java.sql.*;
import java.util.*;

public class Main {

    private final Scanner scanner = new Scanner(System.in);
    private AccountRepository accountRepository;
    private MoonMissionRepository moonMissionRepository;

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
        DataSource dataSource = new SimpleDriverManagerDataSource(jdbcUrl, dbUser, dbPass);

        // Creates and injects repositories with DataSource
        accountRepository = new JdbcAccountRepository(dataSource);
        moonMissionRepository = new JdbcMoonMissionRepository(dataSource);

        if (login()) {
            menu();
        }

        scanner.close();
    }

    private void menu (){
        boolean isInMenu = true;
        while (isInMenu) {
            System.out.println("1. List moon missions");
            System.out.println("2. Get mission by ID");
            System.out.println("3. Count missions by year");
            System.out.println("4. Create account");
            System.out.println("5. Update password");
            System.out.println("6. Delete account");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            String number = scanner.nextLine();
            switch (number) {
                case "1" -> listMoonMissions();
                case "2" -> getMissionById();
                case "3" -> countMissionsByYear();
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
        catch (RuntimeException e) {
            System.out.println("Please enter valid values.");
        }
    }

    // ######### MOON MISSION RELATED ###########

    public void listMoonMissions() {
        moonMissionRepository.spacecraftNames().forEach(System.out::println);
    }

    public void getMissionById() {
        try {
            System.out.println("Enter id of the mission:");

            long id = Long.parseLong(scanner.nextLine());

            // Optional to be able to use isPresent
            Optional<MoonMissionObj> moonMissionObj = moonMissionRepository.findMoonMission(id);
            if (moonMissionObj.isPresent()) {
                System.out.println(moonMissionObj.get().spacecraft());
                System.out.println(moonMissionObj.get().launchDate());
                System.out.println(moonMissionObj.get().carrierRocket());
                System.out.println(moonMissionObj.get().operator());
                System.out.println(moonMissionObj.get().missionId());
                System.out.println(moonMissionObj.get().outcome());

            }
            else {
                System.out.println("Mission not found!");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid id.");
        }
    }

    public void countMissionsByYear() {
        try {
            System.out.println("Enter year:");
            int year = Integer.parseInt(scanner.nextLine());
            System.out.println(year + " had " + moonMissionRepository.countMissionsByYear(year) + " missions");
        } catch (NumberFormatException e){
            System.out.println("Please enter a valid year.");
        }
    }











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
