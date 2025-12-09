package com.example;

public class Account {
    private final long id;
    private final String name;
    private final String firstName;
    private final String lastName;
    private final String ssn;

    public Account(long id, String name, String firstName, String lastName, String ssn) {
        this.id = id;
        this.name = name;
        this.firstName = firstName;
        this.lastName = lastName;
        this.ssn = ssn;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getSsn() {
        return ssn;
    }
}
